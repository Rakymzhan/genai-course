package com.epam.training.component;

import com.epam.training.dto.book.BookResponse;
import com.epam.training.exception.AiResponseParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ResponseParser {

    private final ObjectMapper objectMapper;

    public BookResponse mapToBookResponse(String modelId, String response) {
        if (StringUtils.isEmpty(response)) {
            return new BookResponse();
        }

        String jsonResponse = cleanResponse(response);
        try {
            BookResponse bookResponse = objectMapper.readValue(jsonResponse, BookResponse.class);
            bookResponse.setModelId(modelId);

            return bookResponse;
        } catch (JsonProcessingException ex) {
            log.error("Json string contains an error: {}", jsonResponse, ex);
            throw new AiResponseParseException(ex);
        }
    }

    private static String cleanResponse(String response) {
        int beginOfJson = response.indexOf("{");
        int endOfJson = response.lastIndexOf("}");
        return response.substring(beginOfJson, ++endOfJson);
    }
}
