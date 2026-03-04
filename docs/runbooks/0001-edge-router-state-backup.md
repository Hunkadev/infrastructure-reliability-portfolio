# Runbook 0001: Edge Router State Backup

## Objective
Mitigate configuration drift and provide a disaster recovery path for the Cisco RV260P Edge Router, which cannot be managed via Infrastructure as Code.

## Execution Trigger
This procedure must be executed immediately following any manual modification to the Cisco RV260P web interface (e.g., ACL changes, VLAN provisioning, Port Forwarding).

## Procedure
1. Authenticate to the Cisco RV260P Web GUI via the Secure Admin Bypass network (VLAN 50).
2. Navigate to **Administration** > **Configuration Management**.
3. Select **Download Running Configuration** to the local machine.
4. Rename the file using the ISO 8601 date standard: `rv260p_running_config_YYYYMMDD.xml`.
5. Move the configuration file to the secure local storage archive (DO NOT commit raw router configurations to the Git repository).
6. Document the manual change in the Git repository changelog to maintain an audit trail.
