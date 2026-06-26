package com.document.ai.document.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SummaryResult {
    private String summary;
    private String keyPoints;
    private String sentiment;
    private int originalWordCount;
    private int summaryWordCount;
}