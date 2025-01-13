package com.epam.training.controllers;

import com.epam.training.dto.embedding.CreateRequest;
import com.epam.training.dto.embedding.CreateResponse;
import com.epam.training.dto.embedding.SearchRequest;
import com.epam.training.dto.embedding.SearchResponse;
import com.epam.training.service.EmbeddingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/embeddings")
public class EmbeddingController {

    private final EmbeddingService embeddingService;

    /**
     * Receives a request {@link CreateRequest} and calls service method to create embeddings.
     *
     * @param request the request with texts to create embeddings {@link CreateRequest}
     * @return the result of creation {@link CreateResponse}
     */
    @PostMapping(value = "/create", consumes = "application/json", produces = "application/json")
    public CreateResponse create(@RequestBody CreateRequest request) {
        return embeddingService.create(request);
    }

    /**
     * Receives a request {@link SearchRequest} and calls service method to find embeddings.
     *
     * @param request the request with text to find embeddings {@link SearchRequest}
     * @return the result of searching {@link SearchResponse}
     */
    @PostMapping(value = "/search", consumes = "application/json", produces = "application/json")
    public SearchResponse search(@RequestBody SearchRequest request) {
        return embeddingService.search(request);
    }
}
