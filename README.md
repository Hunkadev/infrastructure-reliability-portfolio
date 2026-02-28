# Infrastructure & Reliability Portfolio
**Christian Hunkus** | *Site Reliability & Network Infrastructure Engineer*

## Overview
This repository demonstrates my approach to **Systems Engineering**, **Data Reliability**, and **Legacy Modernization**. It contains logic samples from my work in high-availability environments and architectural foundations.

## Repository Structure

### 📂 [sql-data-pipelines](./sql-data-pipelines)
* **Focus:** Algorithmic State-Reconstruction and Legacy Migration.
* **Key Tech:** Oracle SQL, Chained CTEs, Window Functions (`ROW_NUMBER`, `PARTITION`).
* **Use Case:** Replaced rigid SAS logic (limited to fixed 3-step lookbacks) with a dynamic SQL state engine.
    * **`program_enrollment_flow_tracker.sql`:** A dynamically-scoped state engine (Single Program vs. Full DB) that aggregates raw customer movements into high-level monthly churn and health metrics.
    * **`customer_state_index.sql`:** A traversable index of customer history, enabling state-aware analytics without redundant processing cycles.

### 📂 [java-core-logic](./java-core-logic)
* **Focus:** Object-Oriented Design (OOD) and Framework Architecture.
* **Key Tech:** Java, **Command Pattern**, **Strategy Pattern**, Decoupled Interfaces.
* **Use Case:** A modular framework that separates UI from Business Logic, demonstrating scalable software design beyond standard academic requirements.

---
*Note: Proprietary data (Customer IDs, Schema Names, IP Addresses) has been sanitized for compliance and public viewing.*
