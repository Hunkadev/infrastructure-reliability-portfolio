# Bare-Metal Memory Allocation & Serialization
C programs demonstrating low-level memory management, dynamic pointer allocation, and custom data serialization without reliance on high-level garbage collection.

### Core Architecture
* **Custom Stack Serialization (`stack.c`):** A custom memory manager that writes arbitrary data types into a contiguous byte array. Implements a custom 11-byte memory header to enable type-agnostic pushing and popping.
* **Bitwise Inspection (`vararg_ptr.c`):** Analyzes raw memory blocks using bitwise `&` logic to print native binary states. 
* **Dynamic Database Allocation:** Refactored a rigid database into a dynamic memory architecture using `malloc` and `free`, implementing strict bounds checking to prevent buffer overflows.
