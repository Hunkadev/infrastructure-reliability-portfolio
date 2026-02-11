# Object-Oriented Design & Framework Architecture
**Context:** A modular sales management system built to demonstrate scalable design patterns in a constrained environment (Academic 5-week course).

Unlike standard student projects that rely on rigid `switch` statements, this solution implements a reusable framework to decouple the User Interface from the Business Logic.

## Architectural Highlights

### 1. Command Pattern (Decoupling)
* **File:** [`SalespersonMenuBuilder.java`](./week4/newStructure/SalespersonMenuBuilder.java)
* **Concept:** Utilizes anonymous inner classes to inject behavior into menu items. The menu system (`week4.menu`) has no dependency on the business logic; it simply executes the command it is given.
* **Why:** Allows for infinite extensibility without modifying the core menu code.

### 2. Strategy Pattern (Interchangeability)
* **File:** [`TablePrinter.java`](./week4/newStructure/TablePrinter.java) & [`ScannerInputFilter.java`](./week4/newStructure/ScannerInputFilter.java)
* **Concept:** Defines behavior through interfaces (`InputObject`, `TablePrinter`).
* **Why:** Enables hot-swapping of input methods (Scanner vs. GUI) or output formats (Console vs. CSV) without breaking the application flow.

### 3. Framework Design (Composition)
* **Package:** `week4.menu`
* **Concept:** A standalone library for console interfaces. The business logic (`week4.newStructure`) imports and consumes this library rather than hard-coding menu structures.
