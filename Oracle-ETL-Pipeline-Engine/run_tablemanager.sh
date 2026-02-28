#!/usr/bin/bash

# Set the base directory for the process (the location of this file).
# -- Start with the path to this file: ${BASH_SOURCE[0]}
# -- Get the owning directory instead of the file with 'dirname'
# -- Use 'cd' to change to this file's location (if not already here)
# -- Print the working directory (absolute path) once 'cd' completes
# -- Return the result and store in the 'script_dir' variable
#
script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Set variables using the 'script_dir', when appropriate.
#
## File name definitions (can be passed or enumerated from the directory)
manager="table_manager_create-trunc.plsql.sql"

##
asset_dir="assets"
tabman="$asset_dir/$manager"

# Set the user ID and adhoc prompt for password
#
user="$(read -p "Enter fedex id prefaced with 'a': " && echo $REPLY)"
pass="$(read -sp "Enter LDAP password: " && echo $REPLY)"
# Give the terminal space after the password entry
echo -e "\n"

# Test the connection before you nuke access to the database for the user
echo -e "Testing connection...\n"
sqlplus $user/$pass@data_base << EOQ > /dev/null
whenever sqlerror exit sql.sqlcode
whenever oserror exit failure
select 1 from dual;
EOQ

er_stat=$?
if [[ $er_stat = 0 ]]; then
  echo -e "\nAccess verified, proceeding with process\n"
else
  echo -e "\nAccess check failed, aborting...\n" && exit 1
fi

# Run the table mantainer - iteratively on each loader - effectively a table refresh
## Pull the loaders to TRUNCATE or CREATE the empty tables
cd $script_dir; loader_stack=$(ls | grep loader)
echo -e "\nLoader stack:\n$loader_stack\n"

# Then process the loader_stack variable in a sane way
for ldr in $loader_stack; do
  sqlplus $user/$pass@data_base @"${script_dir}/${ldr}/${tabman}" "${script_dir}/${ldr}/$asset_dir";
done

exit 0
