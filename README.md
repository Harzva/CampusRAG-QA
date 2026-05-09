# Campus RAG Demo (Java Edition)

This repository contains a fully runnable demonstration of a retrieval‑augmented generation (RAG) system built on a modern Java stack.  It showcases how to combine classical backend engineering (Spring Boot, MySQL, Redis) with generative AI tooling (LangChain4j, Milvus, MinIO) and a Vue 3 frontend.

> **Disclaimer**: This demo is intentionally kept simple to make it approachable.  It ingests entire files as single vectors and does not implement sophisticated chunking, reranking or vector merging.  Use it as a starting point and extend it with the advanced techniques described in the referenced Milvus tutorial【789819952613773†L190-L238】.

## 🧱 Architecture

- **Backend**: A Spring Boot 3 application exposes REST endpoints for document ingestion and chat.  It stores metadata in MySQL, caches conversation state in Redis, uploads files to MinIO and stores vector embeddings in Milvus.  LangChain4j provides the embedding and chat models, memory, and Milvus integration【789819952613773†L190-L238】.  Server‑sent events (SSE) are used to stream answers back to the client【789819952613773†L252-L273】.
- **Frontend**: A Vue 3 single‑page application allows users to upload documents and ask questions.  It uses the Fetch API to call the backend and displays streaming responses with a simple chat UI.
- **Deployment**: Docker Compose orchestrates MySQL, Redis, Milvus (with its etcd and MinIO dependencies), the backend service and the frontend service.  Start the entire stack with a single command.

## 🚀 Getting Started

### Prerequisites

- Docker and Docker Compose installed
- An OpenAI API key (optional).  The backend is configured to use a dummy key; replace `OPENAI_API_KEY` in `docker-compose.yml` or `spring_rag_project/backend/src/main/resources/application.yml` with a real key if you want meaningful answers.

### Running with Docker Compose

From the root of the `spring_rag_project` directory run:

```bash
docker compose up -d
```

The services will build and start.  Once the stack is ready:

- Open the frontend at `http://localhost:3000`.
- Upload a plain text file in the **Upload Knowledge File** section.  The file is stored in MinIO, indexed into Milvus and recorded in MySQL.
- Ask a question in the **Ask a Question** section.  The system embeds your query, performs a vector search in Milvus and calls the language model to generate a response.  The answer is streamed back to the browser.

### Development without Docker

If you prefer to run the services locally:

1. Start supporting services (MySQL, Redis, Milvus and MinIO) manually or via Docker.
2. In the `backend` directory run:

   ```bash
   mvn spring-boot:run
   ```

3. In the `frontend` directory run:

   ```bash
   npm install
   npm run dev
   ```

### Extending the Demo

This project lays the groundwork for a production‑ready knowledge assistant.  To make it more powerful you could:

1. **Implement proper document chunking and parent‑child context assembly**.  The Milvus tutorial highlights using `TikaDocReader` and token‑based splitting to process PDFs and Word documents, then storing the resulting vectors in Milvus【789819952613773†L190-L238】.
2. **Use multiple retrieval strategies**.  Combine dense vector search from Milvus with BM25 keyword search from MySQL or ElasticSearch and fuse the results, mirroring the hybrid retrieval pipeline described in the Milvus article【789819952613773†L190-L238】.
3. **Add Rerankers and context windows**.  After retrieving candidates from Milvus, use a cross‑encoder to rerank them and assemble a prompt within the model’s context window【789819952613773†L252-L273】.
4. **Persist chat memory in Redis** for multi‑turn conversations.  LangChain4j offers a Redis‑backed `ChatMemory` implementation which can be plugged in via `langchain4j-redis`.
5. **Replace OpenAI with local LLMs**.  The Milvus tutorial uses Ollama to run models like Mistral and `nomic‑embed‑text` locally【789819952613773†L190-L238】.  You can switch `chatModel` and `embeddingModel` beans to use `OllamaChatModel` and `OllamaEmbeddingModel` instead of OpenAI.

## 📚 References

- Milvus: *From Docs to Dialogue: Building a Production‑Ready AI Assistant with Spring Boot and Milvus*【789819952613773†L190-L238】 – This blog post provided the inspiration and guidance for integrating Spring Boot with Milvus, including document ingestion, vector storage and chat streaming.
- Medium: *Java’s AI Awakening: A Backend Engineer’s Perspective on LangChain4j vs Spring AI* – Highlights the differences between Spring AI and LangChain4j and notes that LangChain4j supports RAG pipelines, streaming responses and tool‑calling features【589166017370999†L61-L89】.
- LangChain4j & Spring AI: Both frameworks aim to bring LLMs to the Java ecosystem; LangChain4j emphasises agentic workflows and modular retrieval, while Spring AI offers declarative integrations and vector store support including Milvus【589166017370999†L61-L74】.