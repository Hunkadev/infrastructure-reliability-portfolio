# ADR 0002: Zero-Trust Network Boundary & Hardware Constraints

## Status
Accepted

## Context
Establishing a secure perimeter for the compute cluster utilizing existing hardware (Cisco RV260P Edge Router, SG-Series Switches). The network must isolate the execution environments from standard client traffic and IoT devices while maintaining strict SLA uptime for local users.

## Decision
1.  **Logical Segmentation:** Implement a strict Layer 3 Access Control List (ACL) with a `Deny All to All` baseline across 7 VLANs.
2.  **Telemetry Mitigation:** Force VLAN 60 DNS traffic through a local AdGuard sinkhole. Explicitly deny WAN access to VLAN 30 (IoT).
3.  **Technical Debt Acceptance:** The Cisco RV260P lacks a native REST API or CLI, making it incompatible with Terraform/Ansible. We accept this constraint; the network boundary will be managed manually until hardware lifecycle replacement.

## Consequences
* **Positive:** High-security, enterprise-grade network isolation.
* **Negative:** Configuration drift is possible due to the inability to manage the routing layer declaratively. Mitigated by Runbook 0001.
