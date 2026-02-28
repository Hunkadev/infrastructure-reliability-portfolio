-- NOTE: This logic utilizes chained CTEs to enforce state-preservation across legacy datasets.
-- Designed for read-reliability and auditability during critical incident response.
-- NOTE: Proprietary data (Customer IDs, Schema Names, IP Addresses) has been sanitized for public viewing.

--><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><
--><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><
--><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><
-- CURRENT PRODUCTION CODE FOR TRACKING ANALYSIS
--><><><><><><><><><> UPDATED JULY 2024 ><><><><><><><><><><><><><><><
-- change the ids in id_ref
-- Check the landscape of ALL program exits summed by month
-- Adding rolling open account tracking "churn"
--
-- Get the ids for programs
with
  id_ref (id_old,cd1,cd2) as (
    select      id_new, cd1, cd2
    from        team_schema.new_xref
    where       id_new in
                  (000000)),
--
-- Get a list of accts from the  programs
-- This is needed because relying on program ID will filter out movements
--   outside the group programs
-- Above we maintain a way to reference group programs (by ID/cd1/cd2), whichever
--   is needed. We then pull all the accts from those group programs and allow "outsider"
--   program IDs back in. Then we profile the data and remove the non-group programs
  cust_accts (acct) as (
    select      distinct acct
    from        cust_table
    where       id_old in (select id_old from id_ref)),
--
-- Setup the initial data and give each record a unique number for reference in a guaranteed order
-- Create YYYYMM versions of enter and exit date
-- The partition groups accts into logical subgroups, grouping all accts that are identical
--   together. Then order those by the exit date (should be same outcome as enter date)
  cur_loc_set (cur_loc,cd1,cd2,id_old,acct,ajdt,acdt,oyyyymm,cyyyymm) as (
    select      row_number() over (partition by acct order by close_dt) cur_loc,
                cd1, cd2, id_old, acct,
                join_dt, close_dt, to_char(join_dt,'YYYYMM'),
                case
                  when close_dt < sysdate
                    then to_char(close_dt,'YYYYMM')
                  else
                    NULL
                end as close_yyyymm
    from        cust_table
    where       acct in (select acct from cust_accts)),
--
-- Join the current location set to itself progressing the unique number by 1
-- This lets us determine, when an account left a program, where it went next
-- Then, join again regressing the unique number by 1
-- This lets us determine where an account came from
-- 
-- The '-1' for the forward table and '+1' of the reverse table may seem
--   opposite, but we are "looking from the perspective of those tables"
--   Since the "hop" table is forward biased, it want to look for the
--   "current" table's record, which is one behind the hop table. 
  hop_loc_set (cur_loc,hop_loc,rev_loc,cur_id_old,acct,cur_cd1,hop_cd1,rev_cd1,
               cur_cd2,hop_cd2,rev_cd2,ajdt,acdt,oyyyymm,cyyyymm) as (
    select      cur.cur_loc, hop.cur_loc, rev.cur_loc, cur.id_old, cur.acct,
                cur.cd1, hop.cd1, rev.cd1, cur.cd2, hop.cd2, rev.cd2,
                cur.ajdt, cur.acdt, cur.oyyyymm, cur.cyyyymm
    from        cur_loc_set cur
    left join   cur_loc_set hop
      on        cur.acct = hop.acct
      and       cur.cur_loc = (hop.cur_loc - 1)
    left join   cur_loc_set rev
      on        cur.acct = rev.acct
      and       cur.cur_loc = (rev.cur_loc + 1)),
--
-- Use the group cd1s as a filter to determine when a customer closed, if they stayed
--   in group programs or moved outside group
-- When the account is still open, there is no "hop_loc", this is the current program
--   that the account is open within. Set "CURR" state
-- When cd1 is not in group cd1s, set "EXIT" state
-- When cd1 is in group cd1s, set "STAY"
-- Catchall at the end sets "NONE" to any unprofiled records
  exit_set (cur_loc,hop_loc,cur_id_old,cur_cd1,hop_cd1,acct,ajdt,acdt,oyyyymm,cyyyymm,affinity) as (
    select      cur_loc,hop_loc,cur_id_old,cur_cd1,hop_cd1,acct,ajdt,acdt,oyyyymm,cyyyymm,
                case
                  -- When there is no forward record to "hop to" next AND the closing
                  --   date is NULL, this is the program the account is currently open
                  --   within.
                  when ((hop_loc is null)
                         and
                        (cyyyymm is null))
                    then 'CURR'
                  -- This is the last record for an account and it is closed
                  -- Check if it closed from within group and mark 'EXIT' if so
                  when ((hop_loc is null)
                         and
                        (cyyyymm is not null)
                         and
                        (cur_cd1 in (select distinct cd1 from id_ref)))
                    then 'EXIT'
                  -- This is the last record for an account and it is closed
                  -- If non-group, this left the programs entirely
                  --   Mark as 'DEAD'
                  when ((hop_loc is null)
                         and
                        (cyyyymm is not null)
                         and
                        (cur_cd1 not in (select distinct cd1 from id_ref)))
                    then 'DEAD'
                  -- These accounts are leaving group programs for non-group programs
                  when ((cur_cd1 in (select distinct cd1 from id_ref))
                         and
                        (hop_cd1 not in (select distinct cd1 from id_ref)))
                    then 'EXIT'
                  -- These accounts are "moving" inside the group
                  -- Specifically, this catches "relinks" to the same exact program
                  when ((cur_cd1 in (select distinct cd1 from id_ref))
                         and
                        (hop_cd1 in (select distinct cd1 from id_ref))
                         and
                         (cur_cd2 = hop_cd2))
                    then 'FAKE'
                  -- These accounts moved within group programs
                  when ((cur_cd1 in (select distinct cd1 from id_ref))
                         and
                        (hop_cd1 in (select distinct cd1 from id_ref)))
                    then 'STAY'
                  -- These accounts moved between two non-group programs
                  when ((cur_cd1 not in (select distinct cd1 from id_ref))
                         and
                        (hop_cd1 not in (select distinct cd1 from id_ref)))
                    then 'IGN1'
                  -- These accounts moved into a group program, not relevant to closing analysis
                  when ((cur_cd1 not in (select distinct cd1 from id_ref))
                         and
                        (hop_cd1 in (select distinct cd1 from id_ref)))
                    then 'IGN2'
                  else
                    'NONE'
                end
    from        hop_loc_set),
--
-- Same thing as exits but for enters. Only capture a "Join" when an account enters
--   a group program from a non-group program.
  enter_set (cur_loc,rev_loc,cur_id_old,cur_cd1,rev_cd1,acct,ajdt,acdt,oyyyymm,cyyyymm,affinity) as (
    select      cur_loc,rev_loc,cur_id_old,cur_cd1,rev_cd1,acct,ajdt,acdt,oyyyymm,cyyyymm,
                case
                  -- First program joined is a group program
                  when ((rev_loc is null)
                         and
                        (cur_cd1 in (select distinct cd1 from id_ref)))
                    then 'ENTR'
                  -- Previously in a non-group program and then joined a group program
                  when ((cur_cd1 in (select distinct cd1 from id_ref))
                         and
                        (rev_cd1 not in (select distinct cd1 from id_ref)))
                    then 'ENTR'
                  -- Previously in group and currently in group with the
                  --   same cd2 code (rejoined same program)
                  when ((cur_cd1 in (select distinct cd1 from id_ref))
                         and
                        (rev_cd1 in (select distinct cd1 from id_ref))
                         and
                         (cur_cd2 = rev_cd2))
                    then 'FAKE'
                  -- Previously and currently in group programs
                  when ((cur_cd1 in (select distinct cd1 from id_ref))
                         and
                        (rev_cd1 in (select distinct cd1 from id_ref)))
                    then 'STAY'
                  -- First program joined is non-group
                  when ((rev_loc is null)
                         and
                        (cur_cd1 not in (select distinct cd1 from id_ref)))
                    then 'IGN1'
                  -- Previously and currently in non-group (non-group move)
                  when ((cur_cd1 not in (select distinct cd1 from id_ref))
                         and
                        (rev_cd1 not in (select distinct cd1 from id_ref)))
                    then 'IGN2'
                  -- Previously in group and currently in non-group
                  when ((cur_cd1 not in (select distinct cd1 from id_ref))
                         and
                        (rev_cd1 in (select distinct cd1 from id_ref)))
                    then 'IGN3'
                  else
                    'NONE'
                end
    from        hop_loc_set),
--
-- Interpret the states from exit processing
-- If "STAY", set pstate to 1 else 0
-- If "EXIT", set cstate to 1 else 0
  exit_state (cur_id_old,cyyyymm,pstate,cstate) as (
    select      cur_id_old, cyyyymm,
                case
                  when affinity = 'STAY'
                    then 1
                  else
                    0
                end,
                case
                  when affinity = 'EXIT'
                    then 1
                  else
                    0
                end
    from        exit_set
    where       affinity <> 'CURR'),
--
-- Interpret the states from enter processing
-- If "ENTR", set ostate to 1 else 0
  enter_state (cur_id_old,oyyyymm,ostate) as (
    select      cur_id_old, oyyyymm,
                case
                  when affinity = 'ENTR'
                    then 1
                  else
                    0
                end
    from        enter_set),
--
-- Count the exit "pstate" (Intra-group account move) and "cstate" (Intra-group account close)
  exit_cnt (cur_id_old,cyyyymm,mcnt,ccnt) as (
    select      cur_id_old, cyyyymm, sum(pstate), sum(cstate)
    from        exit_state
    group by    cur_id_old, cyyyymm),
-- 
-- Count the enter "ostate" (Intra-group account open)
  enter_cnt (cur_id_old,oyyyymm,ocnt) as (
    select      cur_id_old, oyyyymm, count(ostate)
    from        enter_state
    group by    cur_id_old, oyyyymm),
--
-- Combine the enter counts and exit counts into one source using program ID and YYYYMM
  cmb (yyyymm,enters,moves,exits) as (
    select          coalesce(enter.oyyyymm,exit.cyyyymm) dt_yyyymm,
                    coalesce(sum(ocnt),0) enters,
                    coalesce(sum(mcnt),0) moves,
                    coalesce(sum(ccnt),0) exits
    from            enter_cnt enter
    full join       exit_cnt exit
      on            enter.cur_id_old = exit.cur_id_old
      and           enter.oyyyymm = exit.cyyyymm
    group by        coalesce(enter.oyyyymm,exit.cyyyymm)),
--
-- Get the "churn" total 
  churn (yyyymm,churn) as (
    select          yyyymm, (enters - exits)
    from            cmb),
--
-- Expand the set of data by attaching every month in the data set to every
--   equal and prior month of data. This creates a table of duplicate month
--   values that hold non-duplicated data for all prior months.
--
-- This is step 1 of 2, see next step for the reason to expand this data in this way
  rolling_enrollments (yyyymm,enters,moves,exits) as (
    select          a.yyyymm, b.enters, b.moves, b.exits
    from            cmb a
    join            cmb b
      on            a.yyyymm >= b.yyyymm),
--
-- Now we recompress the rolling_enrollments data into a single record per month
-- This results in the sum of the current and all previous differences between
--   enters and exits into a single value. This results in the total open account
--   count for each month that follows the churn of the account pool
  enroll_sum (yyyymm,roll_enrl) as (
    select          yyyymm, sum(enters - exits)
    from            rolling_enrollments
    group by        yyyymm),
--
-- Put it all into one final table
  enrl_track (yyyymm,enters,moves,exits,churn,roll_enrl) as (
    select          a.yyyymm, a.enters, a.moves, a.exits,
                    b.churn,
                    c.roll_enrl
    from            cmb a
    join            churn b
      on            a.yyyymm=b.yyyymm
    join            enroll_sum c
      on            a.yyyymm=c.yyyymm)
--
-- Output result set
select          yyyymm dt_yyyymm, roll_enrl total_enroll, churn,
                enters, moves, exits
from            enrl_track
order by        1;

-- CURRENT PRODUCTION CODE FOR TRACKING ANALYSIS
--><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><
--><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><
--><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><
