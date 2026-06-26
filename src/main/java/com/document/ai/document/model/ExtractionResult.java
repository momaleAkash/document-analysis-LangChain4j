package com.document.ai.document.model;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ExtractionResult {
    private String text;
    private String contentType;
    private Map<String, String> metadata;
    private int wordCount;
    private int pageCount;
    private String language;
}