# ADR 0001: GitOps Secrets Management Strategy

## Status
Accepted

## Context
Transitioning to a GitOps methodology requires storing Infrastructure as Code (Terraform) and Kubernetes manifests in a version-controlled repository. These files inherently contain sensitive data (API tokens, database passwords). Committing plaintext secrets to Git is a critical security vulnerability.

## Decision
We will implement **Mozilla SOPS (Secrets OPerationS)** utilizing **Age** encryption as the cryptographic backend.
* `.sops.yaml` is deployed at the repository root to automatically enforce encryption on all `*.enc.yaml` and `*.enc.env` files.
* The Age private key is strictly isolated to the execution environment and is never committed to version control.

## Consequences
* **Positive:** Complete elimination of plaintext secrets in the repository. Enables secure Trunk-Based Development.
* **Negative:** Introduces a slight operational overhead; engineers must possess the Age private key to view or edit infrastructure states locally.
