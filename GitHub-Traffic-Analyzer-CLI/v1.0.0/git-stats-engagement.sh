#!/usr/bin/env bash

# --- Configuration ---
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")

# TODO: Remove static coding and load config according to XDG
TOKEN_FILE="$HOME/{your/path/here}/.token"
ARCHIVE_DIR="$HOME/{your/path/here}"
ARCHIVE_FILE="$ARCHIVE_DIR/traffic_${TIMESTAMP}.json"
REPO="${1:-{default_repo}}"
# --- End config ---

# Make the archive
mkdir -p "$ARCHIVE_DIR"

# Check if the token file exists
if [[ ! -f "$TOKEN_FILE" ]]; then
    echo "[-] Error: Token file not found at $TOKEN_FILE"
    exit 1
fi

# Load the token into memory
GITHUB_TOKEN=$(cat "$TOKEN_FILE")

echo "=== GitHub Traffic Report for $REPO ==="
echo "[*] Authenticating with GitHub API..."

# Capture the JSON response and the HTTP status code
# Append a newline and the HTTP code to the end of the curl output
RESPONSE=$(curl -s -w "\n%{http_code}" -L \
  -H "Accept: application/vnd.github+json" \
  -H "Authorization: Bearer $GITHUB_TOKEN" \
  -H "X-GitHub-Api-Version: 2022-11-28" \
  "https://api.github.com/repos/$REPO/traffic/popular/paths")

# Split the response into the Body and the Status Code
HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
JSON_BODY=$(echo "$RESPONSE" | sed '$d')

# HTTP Error Routing
if [[ "$HTTP_CODE" -ne 200 ]]; then
    # If it failed, we know it's an error object, so .message is safe
    ERROR_MSG=$(echo "$JSON_BODY" | jq -r '.message // "Unknown error"')
    echo "[-] API Connection Failed! (HTTP $HTTP_CODE)"
    echo "    GitHub returned: $ERROR_MSG"
    exit 1
fi

# Dump the raw JSON
echo "$JSON_BODY" > "$ARCHIVE_FILE"
echo "[*] Raw data successfully archived to the configured storage directory."

echo "[*] Fetching popular paths..."
# Parse the raw array
echo "$JSON_BODY" | jq -r '
    ["PATH", "VIEWS", "UNIQUE_VISITORS"],
    (["----", "-----", "---------------"]),
    (.[] | [.path, .count, .uniques]) | @tsv
  ' | column -t

echo ""
echo "[*] Data fetched successfully."
