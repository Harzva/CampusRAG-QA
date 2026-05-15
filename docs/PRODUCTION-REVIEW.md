# Production Review

## Current Position

CampusRAG-QA is the baseline repository. It should stay small, readable, and useful as the first runnable campus knowledge QA system.

## Improvements Applied

| Area | Change |
| --- | --- |
| Product scope | README and UI now position the project as the RAG baseline with a Wiki switch. |
| Frontend | Replaced the demo-like screen with a cleaner workbench layout and product-specific naming. |
| Backend | Added Wiki upload/chat endpoints so the frontend can switch between RAG and Wiki modes. |
| Retrieval | Wiki mode now reads through the shared retrieval core instead of dumping stored pages. |
| Configuration | Replaced `dummy` API-key defaults with `replace-me` and documented `.env` usage. |
| Documentation | Added operations notes, architecture map, and production checklist. |
| CI | Added GitHub Actions jobs for frontend build, backend Maven tests, Compose config validation, and Docker image builds. |
| Bot integration | Added a disabled-by-default Bot gateway for Feishu, DingTalk, and WeChat adapters. |
| Observability | Added Prometheus metrics exposure and graceful shutdown settings. |
| Deployment hardening | Kubernetes manifests now separate runtime config from secrets and include startup probes plus non-root container security settings. |

## Highest-Impact Next Work

| Priority | Work | Why |
| --- | --- | --- |
| P1 | Return structured source citations | Makes answers auditable. |
| P1 | Add parsers for PDF, DOCX, Markdown, and HTML | Current UTF-8 extraction is too narrow. |
| P2 | Add reranking before prompt assembly | Improves answer quality on larger corpora. |
| P2 | Add basic auth and workspace isolation | Required before real campus use. |
| P2 | Add Bot idempotency storage | Prevents duplicate platform retries from creating duplicate model calls. |
| P2 | Add an authenticated staging smoke environment | Verifies the whole runtime with real dependencies and guarded secrets. |

## Known Tradeoffs

- The current runtime still uses MySQL, Milvus, MinIO, and Redis for the Docker demo.
- The backend sends one completed SSE event; the frontend can consume streaming chunks once the model layer is upgraded.
- No production secrets, uploaded files, or generated data should be committed.
