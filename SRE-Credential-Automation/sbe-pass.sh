#!/usr/bin/bash

# Author: Christian Hunkus
# Date: 12/24/2020
# Contact: christian.hunkus.osv@company.com
# Version: 1.0.0

# Setup vars (if needed)
appdir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/"

# Functions
_usage () {
echo "Usage: sbe-pass
 sbe-pass [option] <file>
 sbe-pass [option]

Options:
 -d, --decrypt <file>		Decrypt LDAP for use.
 -e, --encrypt <file>		Encrypt LDAP for storage.
 -h, --help			Print this help message.
 -m, --make-plaintext-passfile	Create a compatible plaintext file for encrypting.
 -p, --example-passfile		Print an example format for passfile.
 -s, --encode-sas-pass		Encode your LDAP with pwencode."
}

_check_switch_collision () {
declare -a solo_switches
#declare -a group_switches

[[ "$args" = *"-d"* ]] && { solo_switches[${#solo_switches[@]}]="-d"; }
[[ "$args" = *"-e"* ]] && { solo_switches[${#solo_switches[@]}]="-e"; }
[[ "$args" = *"-h"* ]] && { solo_switches[${#solo_switches[@]}]="-h"; }
[[ "$args" = *"-m"* ]] && { solo_switches[${#solo_switches[@]}]="-m"; }
[[ "$args" = *"-p"* ]] && { solo_switches[${#solo_switches[@]}]="-p"; }
[[ "$args" = *"-s"* ]] && { solo_switches[${#solo_switches[@]}]="-s"; }

if [[ "${#solo_switches[@]}" > 1 ]] ; then
	echo "The switches ${solo_switches[@]} can only be used individually."
	_usage
	exit 1
fi
}

_example_pass_file () {
echo "Example of ready-to-encode password file:

 Structure:
	BOF<LDAP-password>:<system-username>:<system-name>EOF

 Example:
	FakePass:aa999999:db_name_oracle
	or
	FakePass:zz999999:db_name_teradata

 Explanation:
	Your three fields should start directly at the beginning-of-file and end
	directly before the end-of-file. The cannot be any other characters or
	system commands in the file or they will either be interpreted as part
	of the password or corrupt the file and prevent encoding.

	Each field should be separated, delimited, by a colon ':'.

	Field 1 should be your LDAP password.

	Field 2 should be your login name for the respective system. This field
	must be only alpha-numeric.

	Field 3 should be a consistent name reference for the system you are
	logging into. This field currently only accepts letters, numbers or
	underscores.

	Fields 2 and 3 don't matter as to their value, these are more
	suddestions; however, once you choose something, it must be consistent.
	Field 1 must be your LDAP or this will not work."
}


# Get program arguments
args=$(getopt -n sbe-pass -o d:e:hmps -l decrypt:,encrypt:,help,\
make-plaintext-passfile,example-passfile,encode-sas-pass -- "$@")
valid_args=$?

# Check if valid args were passed
if [[ "$valid_args" != "0" ]] ; then
	_usage
	exit 1
fi

# Make sure at least one argument was passed
option_args="$(echo "$args" | grep -oe "^.*--")"
[[ "$option_args" = " --" ]] \
&& { >&2 echo "This program requires at least one argument"; _usage; exit 1; }

# Make sure no extra arguments were passed
non_option_args="$(echo "$args" | grep -oe "--.*")"
[[ "$non_option_args" != "--" ]] \
&& { >&2 echo "This program does not accept non-option arguments: $non_option_args"; _usage; exit 1; }

# Check for switch collisions
_check_switch_collision

# Print processed arguments
#echo "Args passed: $args"

# pass arguments back to shell
eval set -- "$args"
while true ; do
	case "$1" in
		-d | --decrypt)
		[[ -r "$2" ]] || { >&2 echo "File does not exist or is not readable"; exit 1; }
		[[ -r "${appdir}decrypt-pass" ]] || { >&2 echo "Missing decrypt-pass utility"; exit 1; }
		${appdir}decrypt-pass "$2"
		shift 2 ;
		;;
		-e | --encrypt)
		[[ -r "$2" ]] || { >&2 echo "File does not exist or is not readable"; exit 1; }
		[[ -r "${appdir}encrypt-pass" ]] || { >&2 echo "Missing encrypt-pass utility"; exit 1; }
		${appdir}encrypt-pass "$2"
		shift 2 ;
		;;
		-h | --help)
		_usage
		shift ;
		;;
		-m | --make-plaintext-passfile)
		[[ -r "${appdir}plaintext-passfile" ]]\
		|| { >&2 echo "Missing plaintext-passfile utility"; exit 1; }
		${appdir}plaintext-passfile
		shift ;
		;;
		-p | --example-passfile)
		_example_pass_file
		shift ;
		;;
		-s | --encode-sas-pass)
		[[ -r "${appdir}sbesaspw002" ]]\
		|| { >&2 echo "Missing sbesaspw002 utility"; exit 1; }
		${appdir}sbesaspw002
		shift ;
		;;
		--) shift ; break ;;
		*) echo "Unexpected option: $1 - something went wrong." ; _usage ; exit 2 ;;
	esac
done

exit 0
