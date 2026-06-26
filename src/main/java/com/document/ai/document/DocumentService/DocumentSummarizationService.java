package com.document.ai.document.DocumentService;

import com.document.ai.document.ai.DocumentAssistant;
import com.document.ai.document.model.SummaryResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentSummarizationService {

    private final DocumentAssistant documentAssistant;

    public SummaryResult summarize(String documentText) {
        log.info("Summarizing document of {} characters", documentText.length());

        long startTime = System.currentTimeMillis();

        try {
            String summary = documentAssistant.summarize(documentText);
            String insights = documentAssistant.extractInsights(documentText);

            long duration = System.currentTimeMillis() - startTime;

            int originalWords = documentText.trim().split("\\s+").length;
            int summaryWords = summary.trim().split("\\s+").length;

            log.info("Summarization completed in {}ms. Compression ratio: {}:1", 
                duration, String.format("%.1f", (double) originalWords / summaryWords));

            return SummaryResult.builder()
                    .summary(summary)
                    .keyPoints(extractKeyPoints(insights))
                    .sentiment(extractSentiment(insights))
                    .originalWordCount(originalWords)
                    .summaryWordCount(summaryWords)
                    .build();

        } catch (Exception e) {
            log.error("Summarization failed", e);
            throw new com.document.ai.document.exception.DocumentProcessingException(
                "Failed to summarize document: " + e.getMessage(), e);
        }
    }

    private String extractKeyPoints(String insights) {
        // Parse insights to extract key points section
        String[] lines = insights.split("\n");
        StringBuilder keyPoints = new StringBuilder();
        boolean inKeyPoints = false;

        for (String line : lines) {
            if (line.toLowerCase().contains("key") || line.toLowerCase().contains("entities")) {
                inKeyPoints = true;
            }
            if (inKeyPoints && !line.isBlank()) {
                keyPoints.append(line).append("\n");
            }
            if (inKeyPoints && line.isBlank() && keyPoints.length() > 50) {
                break;
            }
        }
        return keyPoints.toString().trim();
    }

    private String extractSentiment(String insights) {
        if (insights.toLowerCase().contains("positive")) return "POSITIVE";
        if (insights.toLowerCase().contains("negative")) return "NEGATIVE";
        return "NEUTRAL";
    }
}