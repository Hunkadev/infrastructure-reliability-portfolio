# GitHub Traffic Analytics CLI (v1.0.0)

A lightweight, secure Bash utility to bypass GitHub's truncated web UI and extract raw repository traffic telemetry directly to the terminal.

## The Problem
GitHub's built-in "Popular Content" traffic UI truncates deep file paths. When auditing engagement on projects (like deep Java OOP structures or nested IaC directories), the UI hides exactly which files visitors are actively reading.

## The Solution
This CLI tool acts as the Extractor in an ETL pipeline. It queries the GitHub REST API (2022-11-28), archives the raw JSON payload for future time-series analysis, and parses the immediate results into a clean, untruncated stdout table.

## Features
* **Zero Hardcoded Secrets:** Implements dynamic token injection by reading credentials from a locked local file (`chmod 600`).
* **Robust Error Handling:** Intercepts raw HTTP status codes from `curl` to gracefully catch API, rate-limit, or authentication failures before passing data to `jq`.
* **Dynamic Targeting:** Accepts the target repository as an optional command-line argument, defaulting to a configured primary repository if none is provided.
* **Idempotent Archiving:** Automatically generates local directory structures and timestamps for raw JSON storage without overwriting previous data.

## Prerequisites
* `bash`
* `curl`
* `jq`

## Installation & Configuration

1. **Clone the repository:**
   ```bash
   git clone https://github.com/Hunkadev/infrastructure-reliability-portfolio.git
   ```

2. **Secure the Token:** Generate a fine-grained, read-only GitHub Personal Access Token. Save the raw token into a hidden file in your chosen path and lock the permissions:

   ```bash
   echo "your_fine_grained_token_here" > ~/.token
   chmod 600 ~/.token
   ```

3. **Configure the Script:**
Update the variables at the top of the `git-stats-engagement.sh` file to match your local environment paths.

## Usage
Execute the script directly from your terminal. If no argument is passed, it defaults to the repository defined in the script.

   ```bash
   ./git-stats-engagement.sh
   ```

   To target a specific repository dynamically:

   ```bash
   ./git-stats-engagement.sh Hunkadev/another-repo-name
   ```

## Architectural Roadmap
This script is the foundation for a larger event-driven observability stack.

* **v1.0.0** (Current): Raw data extraction, JSON archiving, and CLI formatting (Bash).

* **v1.1.0** (Planned): Migration of hardcoded variables to the XDG Base Directory standard (`~/.config/`).

* **v1.2.0** (Planned): A analysis engine to ingest timestamped JSON archives and calculate engagement velocity (Python).

* **v2.0.0** (Planned): Event-based alerting daemon to trigger Discord webhooks if the Python analyzer detects engagement thresholds exceeding expected baselines (Go).

* **v2.1.0** (Planned): Automation via `systemd` timers for headless ETL pipeline execution.

* **v2.2.0** (Planned): CLI wrapper utility utilizing `getopts` for unified execution and configuration management.
