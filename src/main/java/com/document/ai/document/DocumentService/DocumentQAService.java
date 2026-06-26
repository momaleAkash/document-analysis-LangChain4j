package com.document.ai.document.DocumentService;

import com.document.ai.document.ai.DocumentAssistant;
import com.document.ai.document.model.QAResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentQAService {

    private final DocumentAssistant documentAssistant;

    public QAResult answerQuestion(String documentText, String question) {
        log.info("Answering question: '{}' on document of {} chars", 
            question, documentText.length());

        long startTime = System.currentTimeMillis();

        try {
            String answer = documentAssistant.answerQuestion(documentText, question);
            long duration = System.currentTimeMillis() - startTime;

            // Simple confidence heuristic based on answer content
            double confidence = calculateConfidence(answer);

            List<String> references = extractReferences(answer, documentText);

            log.info("Q&A completed in {}ms with confidence {}", duration, confidence);

            return QAResult.builder()
                    .question(question)
                    .answer(answer)
                    .confidence(confidence)
                    .sourceReferences(references)
                    .build();

        } catch (Exception e) {
            log.error("Q&A failed for question: {}", question, e);
            throw new com.document.ai.document.exception.DocumentProcessingException(
                "Failed to answer question: " + e.getMessage(), e);
        }
    }

    private double calculateConfidence(String answer) {
        if (answer.toLowerCase().contains("not found") || 
            answer.toLowerCase().contains("cannot find") ||
            answer.toLowerCase().contains("not in the document")) {
            return 0.1;
        }
        if (answer.length() < 20) return 0.3;
        if (answer.contains("Based on the document") || answer.contains("According to")) {
            return 0.9;
        }
        return 0.7;
    }

    private List<String> extractReferences(String answer, String documentText) {
        // Simple reference extraction - in production, use chunking + embedding similarity
        return Collections.emptyList();
    }
}