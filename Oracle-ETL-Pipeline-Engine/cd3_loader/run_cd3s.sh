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
file="cd3_list"
ctl="load_cd3_list.ctl"
manager="table_manager_create-trunc.plsql.sql"

##
asset_dir="${script_dir}/assets"
output_dir="$script_dir/$file"
tabman="$asset_dir/$manager"
infile="$script_dir/${file}.csv"
ctlfile="$asset_dir/$ctl"
badfile="$output_dir/${file}.bad"
dscfile="$output_dir/${file}.dsc"
logfile="$output_dir/${file}.log"


# Set the user ID and adhoc prompt for password
#
user="$(read -p "Enter fedex id prefaced with 'a': " && echo $REPLY)"
pass="$(read -sp "Enter LDAP password: " && echo $REPLY)"

# Run the table mantainer
sqlplus $user/$pass@data_base @$tabman "$asset_dir"

# Run sqlldr to insert data to database
#
sqlldr control="$ctlfile", data="$infile", log="$logfile", bad="$badfile", discard="$dscfile" userid="$user/$pass"

# Store the load file with a timestamp
#
mv "$infile" "${output_dir}/${file}_$(date '+%Y%m%d-%H%M%S').csv"

exit 0
