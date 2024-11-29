package com.epam.training.component;

import com.epam.training.exception.JsonSchemaFileNotFoundException;
import com.microsoft.semantickernel.orchestration.responseformat.JsonResponseSchema;
import com.microsoft.semantickernel.orchestration.responseformat.JsonSchemaResponseFormat;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Used to create various types of response
 */
@Component
public class ResponseFormatFactory {

    private static final String JSON_PATH_FORMAT = "json-schema/%s.json";

    /**
     * Creates a {@link JsonSchemaResponseFormat} from JSON schema saved in file
     *
     * @param filename the name of JSON file without extension, because it's also used as schema name
     * @return an instance of {@link JsonSchemaResponseFormat}
     */
    public JsonSchemaResponseFormat createJsonResponseFormat(String filename) {
        try {
            File jsonFile = new ClassPathResource(String.format(JSON_PATH_FORMAT, filename)).getFile();
            String jsonSchema = new String(Files.readAllBytes(jsonFile.toPath()));
            JsonResponseSchema jsonResponseSchema = new JsonResponseSchema(filename, jsonSchema, true);

            return new JsonSchemaResponseFormat(jsonResponseSchema);
        } catch (IOException ex) {
            throw new JsonSchemaFileNotFoundException(filename, ex);
        }
    }
}
