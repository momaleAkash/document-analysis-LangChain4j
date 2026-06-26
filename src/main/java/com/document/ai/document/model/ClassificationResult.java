package com.document.ai.document.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClassificationResult {
    private DocumentType documentType;
    private double confidence;
    private String reasoning;
    private String category;
}