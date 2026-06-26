package com.document.ai.document.DocumentService;

import com.document.ai.document.ai.DocumentClassifier;
import com.document.ai.document.model.ClassificationResult;
import com.document.ai.document.model.DocumentType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentClassificationService {

    private final DocumentClassifier documentClassifier;

    public ClassificationResult classify(String documentText) {
        log.info("Classifying document of {} characters", documentText.length());

        try {
            String result = documentClassifier.classify(documentText);
            log.debug("Raw classification result: {}", result);

            return parseClassificationResult(result);

        } catch (Exception e) {
            log.error("Classification failed", e);
            return ClassificationResult.builder()
                    .documentType(DocumentType.UNKNOWN)
                    .confidence(0.0)
                    .reasoning("Classification failed: " + e.getMessage())
                    .build();
        }
    }

    private ClassificationResult parseClassificationResult(String result) {
        // Expected format: CATEGORY|CONFIDENCE|REASONING
        String[] parts = result.split("\\|", 3);
        
        DocumentType type = DocumentType.UNKNOWN;
        double confidence = 0.0;
        String reasoning = result;

        if (parts.length >= 1) {
            try {
                type = DocumentType.valueOf(parts[0].trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Unknown document type: {}", parts[0]);
            }
        }

        if (parts.length >= 2) {
            try {
                confidence = Double.parseDouble(parts[1].trim());
                confidence = Math.max(0.0, Math.min(1.0, confidence));
            } catch (NumberFormatException e) {
                log.warn("Invalid confidence value: {}", parts[1]);
            }
        }

        if (parts.length >= 3) {
            reasoning = parts[2].trim();
        }

        return ClassificationResult.builder()
                .documentType(type)
                .confidence(confidence)
                .reasoning(reasoning)
                .category(type.name())
                .build();
    }
}