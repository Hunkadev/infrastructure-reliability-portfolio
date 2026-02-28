#!/usr/bin/bash

# Author: Christian Hunkus
# Date: 12/24/2020
# Contact: christian.hunkus.osv@fedex.com
# Version: 1.0.0

# var setup
appdir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
passdir="${HOME}/.encrypted_passwords"

# Check that only one argument was supplied
[[ $# -eq 1 ]] || { >&2 echo "Must supply exactly one parameter"  && exit 1; }

# Check if the argument is a valid reference to a readable file
[[ -d "$1" ]] && { >&2 echo "Input must be a file"; exit 1; }
[[ -r "$1" ]] || { >&2 echo "File does not exist or is not readable"; exit 1; }

# Ingest file
encode_str="$(cat "$1")"

# Check that format is correct before cutting the string
[[ "$encode_str" == +([a-zA-Z0-9])@([:])+([a-zA-Z0-9])@([:])+([a-zA-Z0-9_]) ]] \
|| { >&2 echo "Malformed input. Terminating."; exit 1; }

# Get password and salt from pos-param 1
pass=$(echo "$encode_str" | cut -d ":" -sf 1)
salt=$(echo "$encode_str" | cut -d ":" -sf 2)
pkey=$(echo "$encode_str" | cut -d ":" -sf 3)

# Convert salt value into hexadicimal and truncate to 16 hex chars maximum
# (64 bits total)
hexs=$(echo "$salt" | xxd -g 0 -p)
hexs=${hexs:0:16}

# Encrypt password
encp=$(echo "$pass" | openssl aes-256-cbc -a -k "$pkey" -S "$hexs")

# Store encrypted password
[[ -d "$passdir" ]]\
|| { echo "${passdir} does not exist. Creating directory."; mkdir "$passdir"; chmod 700 "$passdir"; }
cd "$passdir"

encfile="${pkey}.enc"
echo "${encp}:${salt}:${pkey}" > "$encfile"
[[ $? -eq 0 ]] && { echo "Encrypted password file successfully saved in ${passdir}/${encfile}"; }\
|| { >&2 echo "Error occurred when saving file."; exit 1; }
chmod 600 "$encfile"

exit 0
