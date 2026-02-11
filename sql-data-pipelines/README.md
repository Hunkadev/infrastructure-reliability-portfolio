# Data Pipeline Logic
Complex SQL procedures used for revenue validation and state-tracking across legacy systems.

## Highlights
* **State-Aware Logic:** Uses window functions (`ROW_NUMBER`, `PARTITION`) to track customer movement between programs.
* **Auditability:** CTE structures designed for clear debugging during critical incidents.
