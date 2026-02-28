# Oracle ETL Pipeline & Preflight Validation

A state-aware automation suite combining Bash and PL/SQL to manage the ingestion, validation, and archiving of flat files into a legacy Oracle database.

### Core Architecture
* **Idempotent Table Management:** Utilizes dynamic PL/SQL to query database state and gracefully handle exception codes, automatically routing logic to truncate or create tables.
* **Pipeline Observability:** Executes SQL snapshots for synthetic monitoring and hierarchy validation on payloads prior to ingestion.
* **Fail-Fast Authentication:** Engineered a preemptive database ping to validate LDAP credentials before initiating automated loops, instantly intercepting bad passwords to prevent Active Directory account lockouts.
* **Auditability:** Automates Oracle `sqlldr` control files, actively capturing `.bad` records and time-stamping ingested CSVs.
