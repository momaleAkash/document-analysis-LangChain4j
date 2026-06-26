package com.document.ai.document.DocumentService;

import com.document.ai.document.model.ExtractionResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentExtractionService {

    private final Tika tika;
    private final AutoDetectParser parser;
    private final ParseContext parseContext;

    @Value("${docintel.tika.max-text-length:50000}")
    private int maxTextLength;

    public ExtractionResult extractContent(MultipartFile file) {
        log.info("Extracting content from file: {}", file.getOriginalFilename());

        try (InputStream inputStream = file.getInputStream()) {
            // Detect content type
            String detectedType = tika.detect(file.getInputStream());
            //inputStream.reset();

            // Extract metadata and content
            Metadata metadata = new Metadata();
            // metadata.set(Metadata.RESOURCE_NAME_KEY, file.getOriginalFilename());
            metadata.set("resourceName", file.getOriginalFilename());
            
            BodyContentHandler handler = new BodyContentHandler(maxTextLength);
            parser.parse(inputStream, handler, metadata, parseContext);

            String text = handler.toString();
            Map<String, String> metaMap = extractMetadataMap(metadata);

            // Detect language
            String language = detectLanguage(text);

            return ExtractionResult.builder()
                    .text(truncateIfNeeded(text))
                    .contentType(detectedType)
                    .metadata(metaMap)
                    .wordCount(countWords(text))
                    .pageCount(extractPageCount(metadata))
                    .language(language)
                    .build();

        } catch (Exception e) {
            log.error("Failed to extract content from {}", file.getOriginalFilename(), e);
            throw new com.document.ai.document.exception.DocumentProcessingException(
                "Failed to extract content: " + e.getMessage(), e);
        }
    }

    private Map<String, String> extractMetadataMap(Metadata metadata) {
        Map<String, String> map = new HashMap<>();
        Arrays.stream(metadata.names()).forEach(name -> 
            map.put(name, metadata.get(name))
        );
        return map;
    }

    private String detectLanguage(String text) {
        if (text == null || text.isBlank()) return "unknown";
        // Simple heuristic - could use Tika's LanguageDetector for better accuracy
        return text.length() > 100 ? "en" : "unknown";
    }

    private int countWords(String text) {
        if (text == null || text.isBlank()) return 0;
        return text.trim().split("\\s+").length;
    }

    private int extractPageCount(Metadata metadata) {
        String pages = metadata.get("xmpTPg:NPages");
        if (pages != null) {
            try {
                return Integer.parseInt(pages);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    private String truncateIfNeeded(String text) {
        if (text == null) return "";
        if (text.length() > maxTextLength) {
            log.warn("Text truncated from {} to {} characters", text.length(), maxTextLength);
            return text.substring(0, maxTextLength) + "\n\n[Content truncated...]";
        }
        return text;
    }
}