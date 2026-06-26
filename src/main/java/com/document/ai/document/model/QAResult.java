package com.document.ai.document.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class QAResult {
    private String question;
    private String answer;
    private double confidence;
    private List<String> sourceReferences;
}