#!/usr/bin/bash

# Author: Christian Hunkus
# Date: 12/24/2020
# Contact: christian.hunkus.osv@fedex.com
# Version: 1.0.0

# Setup vars
appdir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
passfiles="${HOME}/.pass_files"

# Make sure no arguments were passed, exit otherwise.
[[ "$#" = "0" ]] || { >&2 echo "This script does not take any arguments."; exit 1; }

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
	_get_password "Enter your LDAP password: "
	[[ $? -eq 0 ]] && { opass=$REPLY; } || { >&2 echo "Error retreiving password"; exit 1; }
	_get_password "Re-enter your LDAP password: "
	[[ $? -eq 0 ]] && { vpass=$REPLY; } || { >&2 echo "Error retreiving password"; exit 1; }
}

cnt=0
while [[ $cnt -lt 3 ]] ; do
	cnt=$(($cnt+1))
	_dual_pass_entry
	[[ "$opass" != "$vpass" ]]\
	&& { [[ $cnt -eq 3 ]] && exit 1;\
	>&2 echo "Passwords did not match. $((3 - $cnt)) attempts remaining."; }\
	|| { break; }
done

cnt=0
while [[ $cnt -lt 3 ]] ; do
	cnt=$(($cnt+1))
	_get_input "Enter your system username: "
	[[ $? -eq 0 ]] && { uname=$REPLY; } || { >&2 echo "Error retreiving input"; exit 1; }
	[[ "$uname" != +([a-zA-Z0-9]) ]]\
	&& { [[ $cnt -eq 3 ]] && exit 1;\
	>&2 echo "Username can only contain letters and numbers. $((3 - $cnt)) attempts remaining."; }\
	|| { break; }
done

cnt=0
while [[ $cnt -lt 3 ]] ; do
	cnt=$(($cnt+1))
	_get_input "Enter the system name: "
	[[ $? -eq 0 ]] && { sname=$REPLY; } || { >&2 echo "Error retreiving input"; exit 1; }
	[[ "$sname" != +([a-zA-Z0-9_]) ]]\
	&& { [[ $cnt -eq 3 ]] && exit 1;\
	>&2 echo "System name can only contain letters, numbers or underscores. $((3 - $cnt)) attempts remaining."; }\
	|| { break; }
done

# Check for the '.pass_files' directory in the user's $HOME
[[ -d "$passfiles" ]]\
|| { echo "${passfiles} does not exist. Creating directory."; mkdir "$passfiles"; chmod 700 "$passfiles"; }
cd "$passfiles"
passfile="${sname}_pass"
echo "${opass}:${uname}:${sname}" > "$passfile"
[[ $? -eq 0 ]] && { echo "Plaintext password file successfully created in ${passfiles}/${passfile}"; }\
|| { >&2 echo "Error occurred when saving file."; exit 1; }
chmod 600 "$passfile"


exit 0
