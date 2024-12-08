package com.epam.training.component;

import com.epam.training.exception.JsonFileNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.semantickernel.orchestration.responseformat.JsonResponseSchema;
import com.microsoft.semantickernel.orchestration.responseformat.JsonSchemaResponseFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

/**
 * Used to create various types of response
 */
@RequiredArgsConstructor
@Component
public class SettingsFactory {

    private static final String JSON_SCHEMA_NAME = "book_result";

    private static final String JSON_SCHEMA_PATH = String.format("json-schema/%s.json", JSON_SCHEMA_NAME);

    private static final String BOOK_REQUEST_SETTINGS_PATH = "prompt/book-prompt-settings.json";

    private final ObjectMapper objectMapper;

    private JsonSchemaResponseFormat jsonSchemaResponseFormat;

    private PromptSettings promptSettings;

    public JsonSchemaResponseFormat createJsonSchemaResponseFormat() {
        if (Objects.isNull(jsonSchemaResponseFormat)) {
            try {
                File jsonFile = new ClassPathResource(JSON_SCHEMA_PATH).getFile();
                String jsonSchema = new String(Files.readAllBytes(jsonFile.toPath()));
                JsonResponseSchema jsonResponseSchema = new JsonResponseSchema(JSON_SCHEMA_NAME, jsonSchema, true);

                jsonSchemaResponseFormat = new JsonSchemaResponseFormat(jsonResponseSchema);
            } catch (IOException ex) {
                throw new JsonFileNotFoundException(JSON_SCHEMA_PATH, ex);
            }
        }

        return jsonSchemaResponseFormat;
    }

    public PromptSettings createPromptSettings() {
        if (Objects.isNull(promptSettings)) {
            try {
                File jsonFile = new ClassPathResource(BOOK_REQUEST_SETTINGS_PATH).getFile();
                promptSettings = objectMapper.readValue(jsonFile, PromptSettings.class);
            } catch (IOException ex) {
                throw new JsonFileNotFoundException(BOOK_REQUEST_SETTINGS_PATH, ex);
            }
        }

        return promptSettings;
    }
}
