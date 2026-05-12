# Remaining Production Gaps

This repository is deployable as a small campus QA service, but it still needs the following work before being treated as a mature production system.

## P0 Before Real Users

| Gap | Impact | Recommended fix |
| --- | --- | --- |
| No user authentication | Anyone reaching the API can upload and query documents. | Add OAuth/OIDC or gateway auth before public exposure. |
| No tenant-scoped document isolation | One school's data can mix with another if reused in multi-tenant mode. | Add `tenantId` to documents, chunks, conversations, and retrieval filters. |
| No Bot idempotency store | Platform retries can trigger duplicate LLM calls. | Store `(channel, messageId)` in Redis or MySQL with TTL. |
| No source citation response model | Operators cannot audit why an answer was produced. | Return answer plus chunk IDs, document names, and scores. |

## P1 Operational Hardening

| Gap | Impact | Recommended fix |
| --- | --- | --- |
| `ddl-auto: update` | Schema changes are implicit. | Introduce Flyway/Liquibase migrations. |
| Limited document parsing | UTF-8 text works, PDFs/DOCX/HTML are narrow. | Add Tika or dedicated parsers with file-type validation. |
| No reranker | Retrieval quality can drop as corpus grows. | Add a reranking step before prompt assembly. |
| No alert rules | Metrics exist but no alert policy. | Add Prometheus alert rules for 5xx, latency, memory, and disk. |

## P2 Product Refinement

| Gap | Impact | Recommended fix |
| --- | --- | --- |
| SSE emits one completed event | UI is streaming-ready but model output is not truly token streaming. | Switch chat model calls to streaming APIs. |
| No evaluation set | Regressions are hard to detect. | Add golden campus QA examples and CI evaluation. |
| No admin console | Uploads and index health are not visible to operators. | Add an authenticated operator dashboard later. |

## Maintenance Focus

- Keep this repo intentionally small: RAG plus Wiki only.
- Avoid adding Agent/GBrain/Hyper logic here; use the other two repositories for those paths.
- Update README screenshots whenever the workbench UI changes.
- Run `mvn -B test`, `npm run build`, and `npm audit --audit-level=moderate` before releases.
