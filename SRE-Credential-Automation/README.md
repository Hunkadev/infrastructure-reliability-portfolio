# Enterprise Credential Automation Engine

A modular, Bash-driven CLI utility designed to automate the secure rotation and encoding of LDAP credentials across a proprietary SAS grid environment.

### Core Architecture
* **CLI Wrapper:** Utilizes `getopt` as a central routing engine to manage user operations natively.
* **Toil Reduction:** Automates a multi-step password rotation process, extracting obfuscated hashes directly from output logs via `grep`.
* **State Management:** Employs asynchronous process monitoring (`wait $!`) to ensure background tasks complete before proceeding.
* **Input Validation & Safety Limits:** Implements strict 3-strike validation loops in Bash to catch malformed inputs and mismatched passwords, mitigating LDAP lockout risks during manual entry.
