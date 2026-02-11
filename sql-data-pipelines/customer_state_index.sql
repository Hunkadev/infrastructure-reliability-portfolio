-- NOTE: This logic utilizes chained CTEs to enforce state-preservation across legacy datasets.
-- Designed for read-reliability and auditability during critical incident response.
-- NOTE: Proprietary data (Customer IDs, Schema Names, IP Addresses) has been sanitized for public viewing.

--SETUP AND VARS
-- Manual toggles should be at the top
---- Set the reporting end date
def end_offset = -1
---- Set the reporting start date
def start_offset = &end_offset-11
---- Set the name for program or programs being reported
def prog_name = fulldb

---- Define shell vars for the dates used in the query
---- Use the offsets about to adjust your window
---- These variable artificial fillers for NULL values to ensure data capture
---- When a customer is at the beginning or ending of an enrollment chain
----   They will not have a program that they either came from or
----   went to, meaning there is no start/stop dt for the next record.
----   These vars prevent the NULLs from limiting revenue.
def end_dt
def start_dt

-- Capture the dates

---- Capture the end_dt
column edt new_value end_dt
select last_day(add_months(sysdate,&end_offset)) as edt from dual;

---- Capture the start_dt
column sdt new_value start_dt
select trunc(add_months(sysdate,&start_offset),'MM') as sdt from dual;


-- PL/SQL BLOCK
-- Procedure to conditionally delete the table
---- This is required because attempting to drop a table that
----   does not exist will cause an error and cancel the query.
set serveroutput on

begin
  execute immediate 'drop table index_lookup_table purge';
  exception
  when others then
    -- Code 942 is for object not existing
    if sqlcode != -942 then
      dbms_output.put_line('Unexpected error: ' || sqlerrm);
    else
      dbms_output.put_line('Table does not exist, aborting delete.');
    end if;
end;
/

set serveroutput off
-- END PL/SQL BLOCK

-- We don't want too much written to the screen
set termout on
set feedback on
  prompt "Creating the lookup table"
set termout off
set feedback off

-- Drop the index
drop index table_index;

--><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><
--><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><
--><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><
-- TAKE THE "BASIC" CUST DATABASE AND SNAZ IT UP WITH SOME INDICIES
--
--
-- FOR INTEGRATION EASE WITH EXISTING CODEBASE, I CREATED THE XREF WITH ORIGINAL program
--   ID AND id_new. IN THE user cust_source TABLE,
--   I REFER TO id_old AS "agg_id", AS IT IS AN AGGREGATOR, AND
--   I REFER TO id_new AS "id_old", THIS WAS TO MAKE LEGACY CODE EASILY
--   RUNNABLE. JUST A HEADS UP.
--
create table index_lookup_table as select * from (
select * from (
with
  id_map (id_old,id_new,cd1,cd2) as (
    select      id_old, id_new, cd1, cd2
    from        translation_xref),
-- Uncomment below and insert specific id_new for reporting
--    where       id_old in
--                  (000000)),
--
-- Get a list of cnbrs
--
-- This has gotten more complex. Added three numbers that project a customer's
--   association to the program hierarchy.
---- Histnbr - History Number
----   Represents a reverse traversal of the customer through programs.
----   The starting number, one, is the most recent association.
----   The oldest association will be the highest number.
---- main_nbr - program Number
----   Represents changes between programs, with the same program numbers matching main_nbr.
----   This uses dense rank and descending order to label program ids with numbers.
---- sub_nbr - Sub-program Number
----   Represents a reverse traverasl to a link event within a single program.
----   Each time a customer closes in a program and reopens in the same program,
----      it is caught by this number.
----   Same as histnbr, lower number is a newer event, higher number is an older event.
  accts (histnbr,main_nbr,sub_nbr,agg_id,id_old,cnbr,cd1,cd2,cd3,cd4,cd5,cd6,start_dt,stop_dt,
         start_tmstp,stop_tmstp,opn_flg,win_opn_flg) as (
                -- Line adds a number to each duplicate cnbr ordered by close date
                -- This creates a forward history lookup per cnbr
    select      row_number() over (partition by cust_nbr order by stop_dt asc),
                -- Give each program ID a unique reference
                -- Dense rank is used to "rank" all program IDs
                ---- This gives the highest ID the lowest number
                ---- Dense rank does not skip over sequential values like rank
                ----   If 8 IDs contained 4 of 2 given IDs (ID:1234 occurs 4 times, 5678 x4)
                ----     then 5678 would be rank 1 and 1234 is rank 2, all of them
                ----   Rank would skip over the 3 additional ties and rank 1234 at "5"
                dense_rank() over (partition by cust_nbr order by id_old desc),
                -- Give every consecutive start/close event to the same program ID a unique number
                ---- This tells us the reverse history of a customer that keeps jumping
                ----   in and out of the same program without moving to other programs
                ---- Eventually, this labelling method and the doubly linked customer list method
                ----   will be used to collapse appropriate "restarts" into a single record
                ------ Collapsing a record is done at the day level
                ------   Ensure that the customer restarts same day or next day, further is
                ------   considered a seperate event, unless redefined
                row_number() over (partition by id_old, cust_nbr order by stop_dt asc),
                agg_id, id_old, cust_nbr,
                cd1, cd2, cd3, cd4, cd5, cd6,
                start_dt, stop_dt, start_tmstp, stop_tmstp,
                --
                -- An account is open when the closing date id larger (more recent)
                --   than the last day of the reporting window - use below
                -- when stop_dt > to_date('&end_dt')
                --   than the sysdate (current date) - use below
                -- when stop_dt > sysdate
                case
                 when stop_dt > sysdate
                   then 'Y'
                  else 'N'
                end,
                -- An account is open during the window (TTM):
                ---- The closing date for the account must occur after the first day in the window
                ---- The starting date for the account must occur before or on the last day
                ---- Effectively, this sets a "wall" condition at the end of the window
                case
                 when stop_dt > to_date('&end_dt')
                  and start_dt <= to_date('&end_dt')
                   then 'Y'
                  else 'N'
                end
    from        cust_source
                -- This could be id_new/id_old, which is somewhat synonymous with id_old/agg_id
                -- agg_id give the "intended relatability" from the original system
                -- id_new is completely 1 to 1, so generally use that in targeting
                -- I just need the IDs here, so agg_id works and constitutes all id_news
    where       agg_id in (select id_old from id_map)),
--
-- Join the current location set to itself progressing the unique number by 1
-- This lets us determine, when an account left a program, where it went next
-- Then, join again regressing the unique number by 1
-- This lets us determine where an account came from
--
-- The '-1' for the forward table and '+1' of the reverse table may seem
--   opposite, but we are "looking from the perspective of those tables"
--   Since the "fwd" table is forward biased, it want to look for the
--   "current" table's record, which is one behind the fwd table.
  hop_loc_set (cur_loc,fwd_loc,rev_loc,cur_id_old,cnbr,fwd_cd1,rev_cd1,fwd_cd2,rev_cd2,rstop_dt,fstart_dt,
               cur_cd1,cur_cd2,cur_cd3,cur_cd4,cur_cd5,cur_cd6,
               start_dt,stop_dt,start_tmstp,stop_tmstp,main_nbr,sub_nbr,opn_flg,win_opn_flg) as (
    select      cur.histnbr, fwd.histnbr, rev.histnbr,
                cur.id_old, cur.cnbr,
                fwd.cd1, rev.cd1, fwd.cd2, rev.cd2, rev.stop_dt, fwd.start_dt,
                cur.cd1, cur.cd2, cur.cd3, cur.cd4,cur.cd5,cur.cd6,
                cur.start_dt, cur.stop_dt, cur.start_tmstp, cur.stop_tmstp,
                cur.main_nbr, cur.sub_nbr, cur.opn_flg, cur.win_opn_flg
    from        accts cur
    left join   accts fwd
      on        cur.cnbr = fwd.cnbr
      and       cur.histnbr = (fwd.histnbr - 1)
    left join   accts rev
      on        cur.cnbr = rev.cnbr
      and       cur.histnbr = (rev.histnbr + 1))
--
select            cur_loc histnbr, fwd_loc fwdhist, rev_loc revhist, main_nbr main_nbr, sub_nbr sub_nbr,
                  fwd_cd1 fwd_cd1, rev_cd1 rev_cd1, fwd_cd2 fwd_cd2, rev_cd2 rev_cd2,
                  cur_id_old id_new, cnbr,
                  cur_cd1 cd1, cur_cd2 cd2, cur_cd3 cd3, cur_cd4 cd4, cur_cd5 cd5, cur_cd6 cd6,
                  start_dt join_event, stop_dt close_event, start_tmstp cust_join_tmstp, stop_tmstp cust_close_tmstp,
                  fstart_dt fwd_join_dt, rstop_dt rev_close_dt, opn_flg, win_opn_flg ttm_flg
from            hop_loc_set));

-- We don't want too much written to the screen
set termout on
set feedback on
  prompt "Creating the lookup index"
set termout off
set feedback off

-- Compute Index
create index table_index
          on index_lookup_table
             (histnbr, fwdhist, revhist, main_nbr, sub_nbr,
              fwd_cd1, rev_cd1, fwd_cd2, rev_cd2,
              id_new, cnbr,
              cd1, cd2, cd3, cd4,
              join_event, close_event, cust_join_tmstp, cust_close_tmstp fwd_join_dt, rev_close_dt);


quit;
