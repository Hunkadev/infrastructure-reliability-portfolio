#!/usr/bin/bash

# Author: Christian Hunkus
# Date: 12/24/2020
# Contact: christian.hunkus.osv@company.com
# Version: 1.0.0

# var setup
appdir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Check that only one argument was supplied
[[ $# -eq 1 ]] || { >&2 echo "Must supply exactly one parameter"; exit 1; }

# Check if the argument is a valid reference to a readable file
[[ -d "$1" ]] && { >&2 echo "Input must be a file"; exit 1; }
[[ -r "$1" ]] || { >&2 echo "File does not exist or is not readable"; exit 1; }

# Ingest file
decode_str="$(cat "$1")"

# Check if there are 3 fields delimited by colons
[[ "$decode_str" == +([^:])@([:])+([a-zA-Z0-9])@([:])+([a-zA-Z0-9_]) ]] \
|| { >&2 echo "Malformed input. Terminating process."; exit 1; }

# Get password and salt from pos-param 1
encp=$(echo "$decode_str" | cut -d ":" -sf 1)
salt=$(echo "$decode_str" | cut -d ":" -sf 2)
pkey=$(echo "$decode_str" | cut -d ":" -sf 3)

# Convert salt value into hexidecimal and truncate to 16 hex chars maximum
# (64 bits total)
hexs=$(echo "$salt" | xxd -g 0 -p)
hexs=${hexs:0:16}

# Decrypt password
dcpw=$(echo "$encp" | openssl aes-256-cbc -a -d -k "$pkey" -S "$hexs")

echo "$dcpw"

exit 0
