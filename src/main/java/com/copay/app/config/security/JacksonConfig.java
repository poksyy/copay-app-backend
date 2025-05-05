package com.copay.app.config.security;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

@Configuration
public class JacksonConfig {

    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATETIME_FORMAT);

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Configure to fail on unknown properties during deserialization.
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);

        // Create and configure the JavaTimeModule to support Java 8 Date/Time API.
        JavaTimeModule javaTimeModule = new JavaTimeModule();

        // Register a custom serializer for LocalDateTime with a readable format.
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(FORMATTER));

        // Register the configured module.
        mapper.registerModule(javaTimeModule);

        // Disable timestamps and use readable date/time format instead.

        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return mapper;
    }
}