#!/usr/bin/bash

# Author: Christian Hunkus
# Date: 12/24/2020
# Contact: christian.hunkus.osv@company.com
# Version: 1.0.0

# Check for paramaters, there should be nothing passed to the program
[[ $# -gt 0 ]] && { >&2 echo "This program does not accept arguments."; exit 1; }

# Setup vars
calldir="$(pwd)"
homedir="$HOME"
appdir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
sasdir="${appdir}/sas"

progname="sas002_pw_encoder"
gsub="_sasgsub.out"

# Check for the required sas encoding program
[[ -d "$sasdir" ]] || { >&2 echo "Missing required 'sas' subdirectory"; exit 1; }
[[ -r "${sasdir}/${progname}.sas" ]] \
|| { >&2 echo "Missing required ${progname}.sas utility"; exit 1; }

_get_input () {
        read -p "$1"
        [[ $? -ne 0 ]] && { >&2 echo "An error occurred with your input."; exit 1; }
        return 0
}

_get_password () {
        read -sp "$1"
        [[ $? -ne 0 ]] && { >&2 echo "An error occurred with your password."; exit 1; }
        echo ""
        return 0
}

_dual_pass_entry () {
        _get_password "Enter your new LDAP password: "
        [[ $? -eq 0 ]] && { opass=$REPLY; } || { >&2 echo "Error retreiving password"; exit 1; }
        _get_password "Re-enter your new LDAP password: "
        [[ $? -eq 0 ]] && { vpass=$REPLY; } || { >&2 echo "Error retreiving password"; exit 1; }
	return 0
}

_delete_readable_file () {
	file="$1"
	
	[[ -r "$file" ]] && { echo "Attempting to delete $file"; $(rm "$file"); [[ $? -ne 0 ]] \
	&& { echo "Attempt to delete $file was unsuccessful, delete manually"; exit 1; } \
	|| { echo "Deletion successful"; return 0; } }
}

# Get user's company ID
_get_input "Enter your company Employee ID: "
[[ $? -eq 0 ]] && { company_id=$REPLY; } || { >&2 echo "Error retreiving input"; exit 1; }
[[ "$company_id" == +([0-9]) ]] || { >&2 echo "company Employee IDs can only contain digits"; exit 1; }

# Get user's password for encoding
cnt=0
while [[ $cnt -lt 3 ]] ; do
        cnt=$(($cnt+1))
        _dual_pass_entry
        [[ "$opass" != "$vpass" ]]\
        && { [[ $cnt -eq 3 ]] && exit 1;\
        >&2 echo "Passwords did not match. $((3 - $cnt)) attempts remaining."; }\
        || { break; }
done

# Ensure there is not a log file with the same name alreay in the $HOME directory
[[ -r "${calldir}/${progname}${gsub}" ]] && { echo "Found pre-existing ${calldir}/${progname}${gsub}"; \
_delete_readable_file "${calldir}/${progname}${gsub}"; }
[[ -r "${homedir}/${progname}.log" ]] && { echo "Found pre-existing ${homedir}/${progname}.log"; \
_delete_readable_file "${homedir}/${progname}.log"; } 

# Run the sas program to generate new encoded password
# Force sas log to $HOME directory
echo "Running SAS encoding program..."
sas "${sasdir}/${progname}.sas" -set pass "$opass" -altlog "\"${homedir}/${progname}.log\"" &
wait $!

# Make sure SASGSUB has returned the '.log' and 'gsub.out' files before continuing
while [[ ! -e "${calldir}/${progname}${gsub}" ]] || [[ ! -e "${homedir}/${progname}.log" ]] ; do
	sleep 1
done

# Capture SAS002 password from log file
[[ -r "${homedir}/${progname}.log" ]] \
&& { sas002="$(grep -o '{SAS002}.*' "${homedir}/${progname}.log")"; } \
|| { >&2 echo "Error opening ${homedir}/${progname}.log"; exit 1; }

# Create .authinfo file
authinfon=".authinfo_new"
authinfot="default user $company_id password $sas002"
echo "$authinfot" > "${homedir}/${authinfon}"
[[ $? -eq 0 ]] && { echo "SAS002 password file successfully saved in ${homedir}/${authinfon}"; }\
|| { >&2 echo "Error occurred when saving file."; exit 1; }

echo -e "\nOnce your password change is complete, use the command
'cat .authinfo_new > .authinfo
to put your new SAS002 password into your .authinfo file\n"
 
# Clean up files
echo "Cleaning up SAS files"
_delete_readable_file "${calldir}/${progname}${gsub}" 
_delete_readable_file "${homedir}/${progname}.log" 

exit 0
