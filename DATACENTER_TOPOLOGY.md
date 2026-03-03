# Enterprise-Grade Branch Data Center & Zero-Trust Architecture

## Executive Summary
This document outlines the physical topology, network segmentation, and compute architecture of a remote data center deployment. Designed as a strict zero-trust branch office environment, it serves as a proving ground for **Infrastructure as Code (IaC)**, **Bare-Metal Hypervisor Provisioning**, and **Kubernetes (k3s) Orchestration**.

The architecture is currently undergoing a phased transition from legacy bare-metal Debian instances to a fully automated, CNCF-compliant modern infrastructure stack.

> **Note on Code Availability:** This is an active deployment. Sanitized IaC repositories (Terraform/Ansible) and Kubernetes manifests will be linked as sub-modules within this portfolio as each phase clears testing.

---

## Physical Topology (Distributed IDF/MDF Model)
To mimic real-world deployment constraints (power allocation, thermal limits, and rail constraints), the physical hardware is distributed across two distinct zones linked via Cat6 shielded 802.1Q trunk lines.

### Zone 1: Core Routing & Heavy Compute (Office MDF)
* **Housing:** Tripp Lite 12U Rack
* **Edge & Routing:** Cisco RV260P (IP-Passthrough)
* **Core Switch:** Cisco SG200-26
* **DNS/Security Sinkhole:** Raspberry Pi 3B (AdGuard Home)
* **Heavy Compute Node:** Dell Precision T7600 (Externally mounted due to physical rail constraints)

### Zone 2: High-Availability Cluster (Server Room IDF)
* **Housing:** Tripp Lite 48U Rack
* **Distribution Switch:** Cisco SG220-26
* **PoE Infrastructure:** GPOE-12-1U
* **Physical Console Access:** Dell FK989 1U KVM Drawer
* **Hypervisor Cluster:** Dell PowerEdge R720, R510, and 2x R410

---

## Strategic Deployment Roadmap

### Phase 0: GitOps Foundation & Secrets Management
* **Version Control:** Strict Trunk-Based Development via GitHub.
* **Secrets Management:** Integration of Mozilla SOPS / Sealed Secrets to ensure zero plaintext credentials exist in version control. All infrastructure states and manifests are encrypted at rest.

### Phase 1: Network & Security Boundary (Status: Complete)
**Time-to-Value (TTV):** 5 Days *(Executed via strict 1.5-hour zero-downtime maintenance windows to maintain production SLAs for local users).*

The network relies on a strict Layer 3 Access Control List (ACL) architecture with a default-deny (`Deny All to All`) baseline, utilizing 7 isolated subnets.
* **Targeted DNS Telemetry Interception:** Specific ACL rules route VLAN 60 exclusively through the local Raspberry Pi AdGuard sinkhole for telemetry filtering and ad suppression.
* **IoT Isolation:** VLAN 30 is explicitly denied WAN routing entirely to prevent edge-device dialing home.

### Phase 2: Compute & Hypervisor Fabric (Status: In Progress)
*Transitioning from Bare-Metal Debian to a Proxmox VE Cluster.*

* **Kubernetes Control Plane (Dell T7600):** Dual Intel Xeon E5-2687W (16C/32T) | 64GB RAM | 3.6TB Array (RAID 10)
* **Storage Abstraction (Dell PowerEdge R720):** Dual Intel Xeon E5-2630 (12C/24T) | 96GB RAM | 12.7TB Array
* **Database Backend (Dell PowerEdge R510):** Dual Intel Xeon X5650 (12C/24T) | 64GB RAM | 5.5TB Array
* **Worker Nodes (2x Dell PowerEdge R410):** Dual Intel Xeon X5650 (12C/24T per node) | 32GB RAM per node | 1.8TB Array per node

### Phase 3: Infrastructure Provisioning (Terraform)
Defining VM and LXC container states against the Proxmox API to replace manual hypervisor configuration.

### Phase 4: Configuration Management (Ansible)
Execution of automated playbooks for bootstrapping baseline OS security, installing dependencies (e.g., `containerd`), and preparing nodes for clustering.

### Phase 5: Container Orchestration & Disaster Recovery (Kubernetes)
* **Orchestration:** Deployment of k3s across the provisioned VMs to establish a highly available orchestration layer.
* **Disaster Recovery:** Implementation of Velero for automated cluster state backups and persistent volume snapshots.

### Phase 6: Core Services Deployment
* **Storage:** Deployment of MinIO (S3-compatible object storage) targeting the R720 array.
* **Database:** Deployment of a highly available PostgreSQL cluster on the R510.
* **Messaging:** Deployment of a Mosquitto MQTT broker to handle edge-node telemetry.

### Phase 7: Application Layer & CI/CD
* **CI/CD:** Implementation of GitHub Actions pipelines.
* **Data Ingestion Pipeline:** Edge node OpenPLC sensors publish to the MQTT broker. An event-driven Python worker consumes the topic, serializes payloads to MinIO for cold-storage data lakes, and executes concurrent writes to PostgreSQL for real-time dashboarding.

---

## Technical Debt & Environmental Constraints
In enterprise engineering, managing degraded states and hardware constraints is as critical as managing code.

* **Network Hardware Limitations:** The Cisco RV260P edge router lacks a native CLI or REST API, preventing declarative IaC management. Current mitigation involves manual configuration with scheduled running-config backups. Flagged for lifecycle replacement with an API-compliant appliance (e.g., pfSense/OPNsense).
* **Power Inrush Management & Panel Upgrades:** The 48U rack is operating near the limits of the existing electrical panel. Subpanel upgrade to support two dedicated 20A circuits is required to rack the T7600 natively.
* **Staged Boot Sequencing (BIOS Level):** To prevent breaker-tripping inrush current from 52 cores and 30 disk drives spinning up simultaneously after a total power loss, hardware is programmed with AC Power Recovery delays. The network layer is given a 10-minute stabilization buffer (accounting for the ISP gateway), followed by a staggered BIOS-level boot sequence (R720 -> T7600 -> R510 -> R410s).
* **Power Redundancy:** Enterprise UPS battery arrays have reached End-of-Life (EOL). Systems currently operate without HA power failover pending budget reallocation. Lifecycle replacement of UPS units is the primary hardware priority.
