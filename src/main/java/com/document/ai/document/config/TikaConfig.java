package com.document.ai.document.config;

import org.apache.tika.Tika;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TikaConfig {

    @Bean
    public Tika tika() {
        return new Tika();
    }

    @Bean
    public AutoDetectParser autoDetectParser() {
        return new AutoDetectParser();
    }

    @Bean
    public ParseContext parseContext() {
        ParseContext context = new ParseContext();
        context.set(Parser.class, autoDetectParser());
        return context;
    }

    @Bean
    public BodyContentHandler bodyContentHandler() {
        // -1 means unlimited text length
        return new BodyContentHandler(-1);
    }
}