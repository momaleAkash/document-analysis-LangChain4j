package com.document.ai.document.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface DocumentAssistant {

    @SystemMessage("""
        You are an expert document analysis assistant. Your tasks include:
        - Summarizing documents accurately while preserving key information
        - Answering questions based strictly on the provided document content
        - Extracting key insights and action items
        - Identifying entities, dates, and important figures
        
        Always base your answers strictly on the provided document text.
        If the answer cannot be found in the document, clearly state that.
        """)
    @UserMessage("""
        Summarize the following document in 3-5 concise paragraphs.
        Include the main topic, key findings, and any action items.
        
        Document:
        {{documentText}}
        """)
    String summarize(@V("documentText") String documentText);

    @SystemMessage("You are a precise document Q&A assistant. Answer based ONLY on the provided document.")
    @UserMessage("""
        Document:
        {{documentText}}
        
        Question: {{question}}
        
        Provide a clear, accurate answer. If the answer is not in the document, say so explicitly.
        """)
    String answerQuestion(
        @V("documentText") String documentText,
        @V("question") String question
    );

    @SystemMessage("You are a document analysis expert. Extract key insights.")
    @UserMessage("""
        Analyze this document and extract:
        1. Key entities (people, organizations, locations)
        2. Important dates and deadlines
        3. Main topics/themes
        4. Sentiment (positive/neutral/negative)
        5. Action items
        
        Document:
        {{documentText}}
        """)
    String extractInsights(@V("documentText") String documentText);
}