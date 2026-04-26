# Project Cyno: Modular Microkernel ELT Platform 
**Status:** [In Active Development]

## Concept: The Agnostic "Warp Tunnel"
Project Cyno is an open-source, lightweight Python data pipeline framework built on the **Strategy Pattern**. It is designed to act as a stateless data connection engine, a localized "warp tunnel" that executes cross-platform data routing without heavy ORMs, vendor dependencies, or stateful data storage.

## Design Philosophy
Born from the need to bypass brittle, resource-constrained enterprise job schedulers, Project Cyno adheres strictly to the Unix Philosophy: Write programs that do one thing and do it well. It provides agnostic connections to APIs and enterprise data stores to extract payloads without the overhead.
* **Stateless Execution:** The engine does not store data; it only opens the tunnel, manages the memory buffer, and severs the connection.
* **Compute Delegation:** Forces distributed systems to execute the heavy lifting via Predicate Pushdown.
* **Zero-Fault Tolerance:** Infrastructure self-heals dynamically during runtime, executing schema validations (TRUNCATE vs. CREATE) on the fly.
* **Extreme Decoupling:** The `Pipeline House` requests hydrated connections from the `Engine House`, which dynamically interfaces with decoupled Credential Managers and JSON Registries. 

## Architectural Roadmap (V2)
* **Memory-Safe Generators:** Implementing `io.StringIO` in-memory serialization and chunking utilities to replicate the stateless stream-processing mechanics of tools like Kafka and Flink.
* **Edge-Compute Ready:** Structuring the core engine to be compiled as standalone Python Executables (PEX) for zero-dependency deployment on remote edge/IoT hardware.
* **Decoupled Telemetry:** JSON-structured, asynchronous logging module for scalable observability across distributed nodes.

*Documentation, Dependency Diagrams, and Core Routing Logic coming soon.*

---
*Copyright © 2026 Christian Hunkus. Licensed under the [GNU General Public License v3.0](LICENSE).*
