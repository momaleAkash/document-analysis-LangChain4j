package com.document.ai.document.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface DocumentClassifier {

    @SystemMessage("""
        You are a document classification expert. Classify documents into one of these categories:
        RESUME, INVOICE, CONTRACT, REPORT, RESEARCH_PAPER, LEGAL_DOCUMENT, 
        MEDICAL_RECORD, EMAIL, NEWS_ARTICLE, TECHNICAL_SPECIFICATION, UNKNOWN.
        
        Respond with ONLY the category name and confidence (0.0-1.0) in this format:
        CATEGORY|CONFIDENCE|BRIEF_REASONING
        """)
    @UserMessage("""
        Classify the following document:
        
        {{documentText}}
        """)
    String classify(@V("documentText") String documentText);
}