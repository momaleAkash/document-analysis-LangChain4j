# Document Intelligence Service

AI-powered document processing microservice that extracts text from **1000+ file formats**, classifies documents into **11 categories using LLM zero-shot classification**, and answers contextual questions using **full-document context injection**.

Built with **Spring Boot**, **LangChain4j**, **Apache Tika**, **OpenAI**, and **Ollama**.

---

# Features

| Feature | Description |
|---------|-------------|
| **Multi-Format Extraction** | Extract text from PDF, DOCX, TXT, PPTX, XLSX, and 1000+ file formats using Apache Tika |
| **AI Classification** | Zero-shot document classification into 11 predefined categories |
| **AI Summarization** | Generate concise summaries with key insights |
| **Contextual Q&A** | Ask natural language questions about uploaded documents |
| **Async Processing** | Non-blocking document processing using configurable thread pools |
| **Pluggable LLM** | Easily switch between OpenAI and Ollama via configuration |

---

# Tech Stack

- Java 17+
- Spring Boot 3.4
- LangChain4j 0.36
- Apache Tika 3.3
- OpenAI API
- Ollama (Llama 3.2)
- Spring Data JPA
- H2 / PostgreSQL
- Maven

---

# Quick Start

## Prerequisites

- Java 17+
- Maven 3.9+
- OpenAI API Key **OR**
- Ollama installed locally

---

## 1. Clone the Repository

```bash
git clone https://github.com/momaleAkash/document-analysis-LangChain4j.git
cd document-intelligence-service
```

---

## 2. Build the Project

```bash
mvn clean install
```

---

## 3. Configure LLM Provider

### Option A: OpenAI

```bash
export OPENAI_API_KEY=sk-your-key
export LLM_PROVIDER=openai
```

### Option B: Ollama (Local)

Pull the model:

```bash
ollama pull llama3.2
```

Configure:

```bash
export LLM_PROVIDER=ollama
export OLLAMA_BASE_URL=http://localhost:11434
```

---

## 4. Run the Application

```bash
mvn spring-boot:run
```

Application starts at:

```
http://localhost:8080
```

---

# REST API

## Upload & Process Document

```http
POST /api/v1/documents/upload
```

Example:

```bash
curl -X POST http://localhost:8080/api/v1/documents/upload \
  -F "file=@sample-contract.pdf"
```

### Response

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "fileName": "sample-contract.pdf",
  "contentType": "application/pdf",
  "classifiedType": "CONTRACT",
  "summary": "This agreement outlines service terms between...",
  "wordCount": 1247,
  "status": "PROCESSED"
}
```

---

## Ask Questions About a Document

```http
POST /api/v1/documents/{id}/ask
```

Example:

```bash
curl -X POST http://localhost:8080/api/v1/documents/{id}/ask \
  -H "Content-Type: application/json" \
  -d '{"question":"What is the termination clause?"}'
```

### Response

```json
{
  "question": "What is the termination clause?",
  "answer": "Either party may terminate this agreement with 30 days written notice.",
  "confidence": 0.90,
  "sourceReferences": []
}
```

---

## Get Document Details

```bash
curl http://localhost:8080/api/v1/documents/{id}
```

---

## Get Document Summary

```bash
curl http://localhost:8080/api/v1/documents/{id}/summary
```

---

# Architecture

```text
                     +----------------------+
                     |      Client          |
                     +----------+-----------+
                                |
                                v
                  +----------------------------+
                  |    DocumentController      |
                  +-------------+--------------+
                                |
                                v
                  +----------------------------+
                  |     DocumentService        |
                  +-------------+--------------+
                                |
        +-----------------------+-----------------------+
        |                       |                       |
        v                       v                       v
+----------------+     +----------------+      +----------------+
| Tika Extractor |     | LLM Classifier |      | LLM Summarizer |
+----------------+     +----------------+      +----------------+
                                |
                                v
                     +-----------------------+
                     |  Document QA Service  |
                     +-----------+-----------+
                                 |
               +-----------------+-----------------+
               |                                   |
               v                                   v
       +----------------+                +----------------+
       |    OpenAI      |                |    Ollama      |
       +----------------+                +----------------+

                                 |
                                 v
                          +-------------+
                          |  Database   |
                          | H2/Postgres |
                          +-------------+
```

---

# Configuration

| Property | Default | Description |
|-----------|---------|-------------|
| `docintel.llm.provider` | openai | LLM backend (openai or ollama) |
| `langchain4j.open-ai.chat-model.api-key` | - | OpenAI API Key |
| `langchain4j.open-ai.chat-model.model-name` | gpt-4o-mini | OpenAI Model |
| `langchain4j.ollama.chat-model.base-url` | http://localhost:11434 | Ollama URL |
| `langchain4j.ollama.chat-model.model-name` | llama3.2 | Ollama Model |
| `docintel.tika.max-text-length` | 50000 | Maximum extracted text length |

---

# Supported Document Categories

| Type | Description |
|------|-------------|
| RESUME | CVs and professional profiles |
| INVOICE | Bills and invoices |
| CONTRACT | Legal agreements |
| REPORT | Business reports |
| RESEARCH_PAPER | Academic papers |
| LEGAL_DOCUMENT | Legal filings |
| MEDICAL_RECORD | Medical documents |
| EMAIL | Email conversations |
| NEWS_ARTICLE | News articles |
| TECHNICAL_SPECIFICATION | API documentation, engineering specifications |
| UNKNOWN | Unable to classify |

---

# Project Structure

```text
src/main/java/com/docintel
│
├── DocumentIntelligenceApplication.java
│
├── config
│   ├── AsyncConfig.java
│   ├── LangChain4jConfig.java
│   └── TikaConfig.java
│
├── controller
│   └── DocumentController.java
│
├── service
│   ├── DocumentService.java
│   ├── DocumentExtractionService.java
│   ├── DocumentClassificationService.java
│   ├── DocumentSummarizationService.java
│   └── DocumentQAService.java
│
├── ai
│   ├── DocumentAssistant.java
│   └── DocumentClassifier.java
│
├── model
│   ├── Document.java
│   ├── DocumentType.java
│   ├── ExtractionResult.java
│   ├── SummaryResult.java
│   ├── QAResult.java
│   └── ClassificationResult.java
│
├── repository
│   └── DocumentRepository.java
│
└── exception
    ├── DocumentProcessingException.java
    └── GlobalExceptionHandler.java
```

---

# Testing

Run unit tests:

```bash
mvn test
```

Run with the test profile:

```bash
mvn spring-boot:run -Dspring.profiles.active=test
```

---

# Health Check

```bash
curl http://localhost:8080/actuator/health
```

---

# Future Enhancements

- OCR support for scanned PDFs and images
- Vector database integration for semantic search
- RAG-based document question answering
- Multi-language document support
- Batch document processing
- Streaming document uploads
- Docker and Kubernetes deployment
- Authentication & authorization using Spring Security + JWT

---

---

# Installing Ollama (Local LLM)

If you don't want to use the OpenAI API, you can run a local Large Language Model using **Ollama**.

## Ubuntu / Linux

```bash
curl -fsSL https://ollama.com/install.sh | sh
```

Verify installation:

```bash
ollama --version
```

Start the Ollama server:

```bash
ollama serve
```

> **Note:** `ollama serve` runs on **http://localhost:11434** by default.

---

## Windows

1. Download the installer from:

   https://ollama.com/download

2. Install Ollama.

3. Open Command Prompt or PowerShell and verify:

```powershell
ollama --version
```

Start the server:

```powershell
ollama serve
```

---

## macOS

Install using Homebrew:

```bash
brew install ollama
```

or download from:

https://ollama.com/download

Start the server:

```bash
ollama serve
```

---

# Download the Llama Model

Pull the **Llama 3.2** model.

```bash
ollama pull llama3.2
```

Verify installed models:

```bash
ollama list
```

Expected output:

```text
NAME         ID           SIZE
llama3.2     xxxxxxxxx    2.0 GB
```

---

# Test the Model

Run the model directly from the terminal:

```bash
ollama run llama3.2
```

Example:

```text
>>> What is Spring Boot?
Spring Boot is an open-source Java framework...
```

Exit the chat using:

```text
/bye
```

or press

```text
Ctrl + D
```

---

# Configure the Application to Use Ollama

Set the required environment variables.

### Linux/macOS

```bash
export LLM_PROVIDER=ollama
export OLLAMA_BASE_URL=http://localhost:11434
```

### Windows (PowerShell)

```powershell
$env:LLM_PROVIDER="ollama"
$env:OLLAMA_BASE_URL="http://localhost:11434"
```

---

# Verify Ollama API

Check whether the Ollama server is running.

```bash
curl http://localhost:11434/api/tags
```

You should receive a JSON response similar to:

```json
{
  "models": [
    {
      "name": "llama3.2"
    }
  ]
}
```

---

# Start the Application

Once Ollama is running and the model has been downloaded:

```bash
mvn spring-boot:run
```

The application will automatically connect to:

```
http://localhost:11434
```

and use the **llama3.2** model for document classification, summarization, and question answering.

---

# Troubleshooting

### Check if Ollama is running

```bash
ps aux | grep ollama
```

or

```bash
curl http://localhost:11434/api/tags
```

---

### Restart Ollama

```bash
pkill ollama
ollama serve
```

---

### Pull the latest Llama model again

```bash
ollama pull llama3.2
```

---

### List downloaded models

```bash
ollama list
```

---

### Remove a model

```bash
ollama rm llama3.2
```
# License

This project is intended for learning, experimentation, and enterprise document intelligence use cases.
