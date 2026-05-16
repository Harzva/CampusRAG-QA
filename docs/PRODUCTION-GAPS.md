# Production Readiness Status

This repository now has the minimum closure needed for a controlled staging-to-production path.

## Closed Minimum Gates

| Gate | Status |
| --- | --- |
| API authentication and RBAC | Closed with token-based `USER`/`ADMIN` roles and tenant-bound access checks. Replace with OIDC before broad public use. |
| Tenant isolation | Closed for document upload, RAG retrieval, Wiki lookup, and Bot dispatch through normalized `tenantId`. |
| Durable Wiki state | Closed through `wiki_pages` JPA persistence. |
| Schema migration | Closed with Flyway baseline migration and `ddl-auto=validate`. |
| Golden QA in CI | Closed with offline golden suite validation in the CI workflow. |
| Bot idempotency and rate limit | Closed with Redis-backed duplicate suppression and fixed-window throttling. |
| Staging backup/restore and alert drill | Closed through `docs/STAGING-RUNBOOK.md` and `scripts/staging_drill.sh`. |

## Still Intentional Post-MVP Work

| Item | Why it remains |
| --- | --- |
| OAuth/OIDC login | Token RBAC is enough for a deployment gate, but not the final user identity system. |
| Rich document parsing | UTF-8 text ingestion is stable; PDF/DOCX/HTML quality work remains separate. |
| Reranking | Retrieval works without it, but corpus growth will need ranking quality improvements. |
| True token streaming | UI is streaming-ready; backend still emits completed responses. |
| Operator console | Operations are documented through scripts and metrics; a dashboard can come later. |

## Maintenance Boundary

Keep this repo intentionally small: RAG plus Wiki only. Do not add Agent, GBrain, or HyperMemory logic here.
