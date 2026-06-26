package com.document.ai.document.DocumentService;

import com.document.ai.document.model.*;
import com.document.ai.document.repository.DocumentRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentExtractionService extractionService;
    private final DocumentSummarizationService summarizationService;
    private final DocumentQAService qaService;
    private final DocumentClassificationService classificationService;
    private final DocumentRepository documentRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public Document processDocument(MultipartFile file) {
        log.info("Processing document: {}", file.getOriginalFilename());

        // Step 1: Extract content using Tika
        ExtractionResult extraction = extractionService.extractContent(file);

        // Step 2: Classify document using LLM
        ClassificationResult classification = classificationService.classify(extraction.getText());

        // Step 3: Summarize using LLM
        SummaryResult summary = summarizationService.summarize(extraction.getText());

        // Step 4: Persist
        Document document = Document.builder()
                .fileName(file.getOriginalFilename())
                .contentType(extraction.getContentType())
                .fileSize(file.getSize())
                .extractedText(extraction.getText())
                .classifiedType(classification.getDocumentType())
                .summary(summary.getSummary())
                .metadataJson(toJson(extraction.getMetadata()))
                .build();

        Document saved = documentRepository.save(document);
        log.info("Document processed and saved with ID: {}", saved.getId());

        return saved;
    }

    @Async("documentProcessingExecutor")
    public CompletableFuture<Document> processDocumentAsync(MultipartFile file) {
        return CompletableFuture.completedFuture(processDocument(file));
    }

    @Transactional(readOnly = true)
    public QAResult askQuestion(String documentId, String question) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new com.document.ai.document.exception.DocumentProcessingException(
                    "Document not found: " + documentId));

        return qaService.answerQuestion(document.getExtractedText(), question);
    }

    @Transactional(readOnly = true)
    public Document getDocument(String documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new com.document.ai.document.exception.DocumentProcessingException(
                    "Document not found: " + documentId));
    }

    @SneakyThrows
    private String toJson(Object obj) {
        return objectMapper.writeValueAsString(obj);
    }
}