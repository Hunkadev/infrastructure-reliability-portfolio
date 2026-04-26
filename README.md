# Infrastructure & Reliability Portfolio
**Christian Hunkus** | *Site Reliability & Network Infrastructure Engineer*

## Overview
This repository demonstrates my approach to **Systems Engineering**, **Data Reliability**, and **Legacy Modernization**. It contains logic samples from my work in high-availability environments and architectural foundations, showcasing a progression from bare-metal memory management to enterprise-grade data pipeline automation.

## Repository Structure

### 📂 [Project-Cyno-Microkernel](./Project-Cyno-Microkernel)
* **Status:** [In Active Development]
* **Focus:** Stateless ELT Frameworks, Predicate Pushdown, Memory-Safe Data Routing.
* **Key Tech:** Python, io.StringIO (In-Memory Serialization), Strategy Pattern, Azure/Oracle DBAPI.
* **Use Case:** An agnostic "Warp Tunnel" designed to bypass brittle, resource-constrained enterprise job schedulers. Replicates distributed stream-processing mechanics to execute hyper-efficient cross-platform bulk data loads without heavy ORMs.

### 📂 [Oracle-ETL-Pipeline-Engine](./Oracle-ETL-Pipeline-Engine)
* **Focus:** Data Pipeline Observability, Idempotent Execution, and Fail-Safe Automation.
* **Key Tech:** Bash, Oracle PL/SQL, SQL*Loader (`sqlldr`), Synthetic Monitoring.
* **Use Case:** A state-aware automation suite designed to safely ingest flat files into a legacy Oracle DB. 
    * Implements preflight data validation to guarantee payload integrity prior to insertion.
    * Utilizes preemptive database pinging (fail-fast authentication) to prevent Active Directory account lockouts during automated loops.

### 📂 [SRE-Credential-Automation](./SRE-Credential-Automation)
* **Focus:** Toil Reduction, Identity Management Automation, and Process Orchestration.
* **Key Tech:** Bash, Linux CLI (`getopt`), Process Backgrounding, Log Parsing (`grep`).
* **Use Case:** A modular CLI utility that automates the rotation and encoding of LDAP credentials across a proprietary SAS grid. Reduces a highly manual, multi-step rotation process into a single command while enforcing strict input safety limits.

### 📂 [sql-data-pipelines](./sql-data-pipelines)
* **Focus:** Algorithmic State-Reconstruction and Legacy Migration.
* **Key Tech:** Oracle SQL, Chained CTEs, Window Functions (`ROW_NUMBER`, `PARTITION`).
* **Use Case:** Replaced rigid SAS logic with a dynamic SQL state engine.
    * **`program_enrollment_flow_tracker.sql`:** A dynamically-scoped state engine that aggregates raw customer movements into high-level monthly churn metrics.
    * **`customer_state_index.sql`:** A traversable index of customer history using Doubly-Linked List logic, enabling state-aware analytics without redundant processing cycles.

### 📂 [C-Bare-Metal-Memory-Management](./C-Bare-Metal-Memory-Management)
* **Focus:** Low-Level Memory Allocation, Custom Data Serialization, and Pointer Arithmetic.
* **Key Tech:** C, Manual Memory Management (`malloc`/`free`), Bitwise Operations.
* **Use Case:** Foundational system architecture demonstrating hardware-level control without reliance on high-level garbage collection.
    * **`stack.c`:** A custom memory manager that serializes arbitrary data types into a contiguous byte array using a custom 11-byte routing header.

### 📂 [java-core-logic](./java-core-logic)
* **Focus:** Object-Oriented Design (OOD) and Framework Architecture.
* **Key Tech:** Java, **Composite Pattern**, **Strategy Pattern**, Decoupled Interfaces.
* **Use Case:** An event-driven CLI framework that strictly separates I/O from Business Logic, demonstrating highly modular and testable software design.

### 📊 [GitHub-Traffic-Analyzer-CLI](./GitHub-Traffic-Analyzer-CLI)
* **Focus:** Custom Observability, ETL Pipelines, and API Integration.
* **Key Tech:** Bash, `curl`, `jq`, REST APIs.
* **Use Case:** A custom CLI utility built to bypass GitHub's native web UI limitations and extract untruncated repository traffic telemetry.
    * Acts as the "Extractor" in a planned event-driven observability stack.
    * Implements dynamic, secure token injection (zero hardcoded secrets) and strict HTTP status code error handling.

---
*Note: Proprietary data (Customer IDs, Schema Names, IP Addresses) has been sanitized for compliance and public viewing.*

*Note: This portfolio contains sanitized demonstrations of enterprise architecture. Unless explicitly licensed in a project subdirectory, code is provided for portfolio review only and is not licensed for reuse or distribution.*
