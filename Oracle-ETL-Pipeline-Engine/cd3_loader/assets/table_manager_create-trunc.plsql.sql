-- Capture the directory to work within
-- This must only be present if the executing program resides in a different directory
-- See the "run_cd3.sh" file for implementation of this script
def asset_path = &1

-- PL/SQL BLOCK
-- Procedure to conditionally check object existence
set termout on
set serveroutput on

-- Create a bind variable to capture the script name from PL/SQL
variable script varchar2(60)

-- Create variable to hold the name of the script to execute
-- Start the PL/SQL code
begin
  -- Check if the table exists, set the script to create if DNE
  execute immediate 'select 1 from cd3_list';
  -- If something went wrong, move here
  exception
  when others then
    -- Code 942 is for object not existing
    -- We want to exclude non-942 error from processing, but still report them
    if sqlcode != -942 then
      dbms_output.put_line('Unexpected error: ' || sqlerrm);
      :script := 'null.sql';
    -- If the error code was -942, we need to create the table
    else
      -- Table does not exist, make it
      :script := 'create_cd3_list.sql';
    end if;
end;
/

begin
  -- Check if the bind variable is still empty, table exists, truncate it
  if :script is null then
    :script := 'truncate_cd3_list.sql';
  end if;
end;
/

-- Capture the script name and report the outcome
-- When script is set, that value is also stored in the "RUN_SCRIPT" var
prompt Script selected for execution:
print script;

-- Capture the bind variable in something usable
prompt Capturing bind variable 'script'
column script new_val RUN_SCRIPT
select :script script from dual;

-- Run the script
@&asset_path/&RUN_SCRIPT

set serveroutput off
-- END PL/SQL BLOCK

quit
