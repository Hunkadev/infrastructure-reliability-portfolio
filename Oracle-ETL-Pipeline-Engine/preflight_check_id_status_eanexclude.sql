
-- This file reviews the state of the ID tables prior to running the process
-- #1) Counts from all hierarchy and ID stages

-- Mimic the snapshot logic on the master DB for counts
-- Reverse get the accounts, like snapshot
create table dbshot as (
    -- Get cd4 level accounts
    select        group_id, prog_id, acct_nbr, close_dt,
                  cd1, cd2, cd3, cd4, cd5, cd6
    from        cust_tab
    where       cd4 in
                  (select cd4 from cd4_list)
    and         acct_nbr not in
                  (select acct_nbr from trgt_accts)
    union
    -- Get cd3 level accounts
    select        group_id, prog_id, acct_nbr, close_dt,
                  cd1, cd2, cd3, cd4, cd5, cd6
    from        cust_tab
    where       cd3 in
                  (select cd3 from cd3_list)
    and         acct_nbr not in
                  (select acct_nbr from trgt_accts)
    union
    -- Get cd2 level accounts
    select        group_id, prog_id, acct_nbr, close_dt,
                  cd1, cd2, cd3, cd4, cd5, cd6
    from        cust_tab
    where       cd2 in
                  (select cd2 from cd2_list)
    and         acct_nbr not in
                  (select acct_nbr from trgt_accts)
    union
    -- Get cd1 level accounts
    select        group_id, prog_id, acct_nbr, close_dt,
                  cd1, cd2, cd3, cd4, cd5, cd6
    from        cust_tab
    where       cd1 in
                  (select cd1 from cd1_list)
    and         acct_nbr not in
                  (select acct_nbr from trgt_accts));

--   All time accounts
prompt 'Account Funnel'
select      'all time' totl_typ, count(acct_nbr) cnt
from        dbshot
  union all
-- Current open accounts
select      'open' totl_typ, count(acct_nbr) cnt
from        dbshot
where       close_dt > sysdate
  union all
-- Excluded accounts
select      'exclusions' totl_typ, count(acct_nbr) cnt
from        trgt_accts
  union all
-- Level counts
-- cd1
select      'cd1s' totl_typ, count(cd1) cnt
from        cd1_list
  union all
-- cd2
select      'cd2s' totl_typ, count(cd2) cnt
from        cd2_list
  union all
-- cd3
select      'cd3s' totl_typ, count(cd3) cnt
from        cd3_list
  union all
-- cd4
select      'cd4s' totl_typ, count(cd4) cnt
from        cd4_list;
-- HOLD FOR ADDITIONAL LEVEL LOADERS

-- #2) List the Levels
prompt 'Ensure proper levels are loaded'
select      'cd1' nm, cd1 cd
from        cd1_list
  union all
select      'cd2' nm, cd2 cd
from        cd2_list
  union all
select      'cd3' nm, cd3 cd
from        cd3_list
  union all
select      'cd4' nm, cd4 cd
from        cd4_list
order by    1;

-- #3) Examine customer counts per cd1
prompt 'Breakdown the Accounts appearing in defined cd1 levels'
select      coalesce(group_id,prog_id) prog_id, prog_id new_prog_id, count(distinct acct_nbr) mems
from        dbshot
where       cd1 in (select cd1 from cd1_list)
  and       close_dt > sysdate
group by    group_id, prog_id
order by    1, 2;

-- #4) Examine customer counts per cd2ision
prompt 'Breakdown the Accounts appearing in defined cd2 levels'
select      coalesce(group_id,prog_id) prog_id, prog_id new_prog_id, count(distinct acct_nbr) mems
from        dbshot
where       cd2 in (select cd2 from cd2_list)
  and       close_dt > sysdate
group by    group_id, prog_id
order by    1, 2;

-- #5) Examine customer counts per cd3
prompt 'Breakdown the Accounts appearing in defined cd3 levels'
select      coalesce(group_id,prog_id) prog_id, prog_id new_prog_id, count(distinct acct_nbr) mems
from        dbshot
where       cd3 in (select cd3 from cd3_list)
  and       close_dt > sysdate
group by    group_id, prog_id
order by    1, 2;

-- #6) Examine customer counts per group
prompt 'Breakdown the Accounts appearing in defined cd4 levels'
select      coalesce(group_id,prog_id) prog_id, prog_id new_prog_id, count(distinct acct_nbr) mems
from        dbshot
where       cd4 in (select cd4 from cd4_list)
  and       close_dt > sysdate
group by    group_id, prog_id
order by    1, 2;

-- #7) Examine XREF for setup challenges
prompt 'Examine the XREF'
select      coalesce(prog_id,new_prog_id) prog_id, new_prog_id, cd1, cd2, expire_dt
from        adhoc_cht_acct
where       new_prog_id in
                      (select distinct prog_id from dbshot)
order by 1, 2;

drop table dbshot;

quit
