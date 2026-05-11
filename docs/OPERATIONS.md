# Operations Guide

## Local Runtime

```bash
cp .env.example .env
docker compose up -d --build
```

Check:

```bash
docker compose ps
curl http://localhost:8080/actuator/health
```

## Configuration

Use `.env` for runtime settings. Do not commit real API keys.

Important values:

| Variable | Purpose |
| --- | --- |
| `OPENAI_API_KEY` | Model provider key. |
| `FRONTEND_PORT` | Browser-facing frontend port. |
| `BACKEND_PORT` | Browser/API-facing backend port. |
| `MYSQL_ROOT_PASSWORD` | MySQL root password for local stack. |
| `MINIO_ROOT_USER` | MinIO console/access username. |
| `MINIO_ROOT_PASSWORD` | MinIO password. |

## Troubleshooting

| Symptom | Check |
| --- | --- |
| Frontend cannot call backend | Confirm nginx `frontend/nginx.conf` proxies `/api/` to `backend:8080`. |
| Model replies are empty or invalid | Confirm `OPENAI_API_KEY` is set. |
| Upload fails | Check MinIO container logs and bucket settings. |
| Retrieval is weak | Current prototype stores vector item IDs; production should persist chunk text and metadata. |

