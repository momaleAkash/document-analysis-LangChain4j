package com.document.ai.document.controller;

import com.document.ai.document.model.*;
import com.document.ai.document.DocumentService.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
@Tag(name = "Document Intelligence", description = "AI-powered document processing APIs")
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload and process a document", 
               description = "Uploads a PDF, DOCX, or other supported document. " +
                           "Extracts text, classifies, and summarizes using AI.")
    public ResponseEntity<Map<String, Object>> uploadDocument(
            @RequestParam("file") MultipartFile file) {

        log.info("Received upload request: {}", file.getOriginalFilename());

        Document document = documentService.processDocument(file);

        Map<String, Object> response = new HashMap<>();
        response.put("id", document.getId());
        response.put("fileName", document.getFileName());
        response.put("contentType", document.getContentType());
        response.put("classifiedType", document.getClassifiedType());
        response.put("summary", document.getSummary());
        response.put("wordCount", document.getExtractedText() != null ? 
            document.getExtractedText().split("\\s+").length : 0);
        response.put("status", "PROCESSED");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{documentId}/ask")
    @Operation(summary = "Ask a question about a document",
               description = "Uses RAG-style Q&A to answer questions based on document content")
    public ResponseEntity<QAResult> askQuestion(
            @PathVariable String documentId,
            @RequestBody Map<String, String> request) {

        String question = request.get("question");
        if (question == null || question.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        QAResult result = documentService.askQuestion(documentId, question);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{documentId}")
    @Operation(summary = "Get document details")
    public ResponseEntity<Document> getDocument(@PathVariable String documentId) {
        return ResponseEntity.ok(documentService.getDocument(documentId));
    }

    @GetMapping("/{documentId}/summary")
    @Operation(summary = "Get document summary")
    public ResponseEntity<Map<String, String>> getSummary(@PathVariable String documentId) {
        Document doc = documentService.getDocument(documentId);
        Map<String, String> response = new HashMap<>();
        response.put("summary", doc.getSummary());
        response.put("type", doc.getClassifiedType() != null ? doc.getClassifiedType().name() : "UNKNOWN");
        return ResponseEntity.ok(response);
    }
}