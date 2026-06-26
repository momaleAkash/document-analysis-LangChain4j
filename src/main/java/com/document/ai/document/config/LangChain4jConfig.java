package com.document.ai.document.config;

import com.document.ai.document.ai.DocumentAssistant;
import com.document.ai.document.ai.DocumentClassifier;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Duration;

@Slf4j
@Configuration
public class LangChain4jConfig {


    // @Value("${langchain4j.open-ai.chat-model.api-key}")
    // private String openAiApiKey;

    // @Value("${langchain4j.open-ai.chat-model.model-name:gpt-4o-mini}")
    // private String openAiModel;

    @Value("${docintel.llm.provider:ollama}")
    private String llmProvider;

    @Value("${langchain4j.ollama.chat-model.base-url:http://localhost:11434}")
    private String ollamaBaseUrl;

    @Value("${langchain4j.ollama.chat-model.model-name:llama3.2}")
    private String ollamaModel;

    @Bean
    @Primary
    public ChatLanguageModel chatLanguageModel() {
        log.info("Initializing LLM provider: {}", llmProvider);

        return createOllamaModel();
            // case "openai" -> createOpenAiModel();
            // default -> {
            //     log.warn("Unknown provider '{}', falling back to OpenAI", llmProvider);
            //     //yield createOpenAiModel();
            // }
        //};
    }

    // private ChatLanguageModel createOpenAiModel() {
    //     return OpenAiChatModel.builder()
    //             .apiKey(openAiApiKey)
    //             .modelName(openAiModel)
    //             .temperature(0.3)
    //             .timeout(Duration.ofSeconds(60))
    //             .build();
    // }

    private ChatLanguageModel createOllamaModel() {
        return OllamaChatModel.builder()
                .baseUrl(ollamaBaseUrl)
                .modelName(ollamaModel)
                .temperature(0.3)
                .timeout(Duration.ofSeconds(120))
                .build();
    }

    @Bean
    public DocumentAssistant documentAssistant(ChatLanguageModel chatLanguageModel) {
        return AiServices.builder(DocumentAssistant.class)
                .chatLanguageModel(chatLanguageModel)
                .build();
    }

    @Bean
    public DocumentClassifier documentClassifier(ChatLanguageModel chatLanguageModel) {
        return AiServices.builder(DocumentClassifier.class)
                .chatLanguageModel(chatLanguageModel)
                .build();
    }
}