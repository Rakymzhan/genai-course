package com.epam.training.service;

import com.epam.training.dto.embedding.CreateRequest;
import com.epam.training.dto.embedding.CreateResponse;
import com.epam.training.dto.embedding.SearchRequest;
import com.epam.training.dto.embedding.SearchResponse;

public interface EmbeddingService {

    /**
     * Creates embeddings from texts in {@link CreateRequest} and stores them in {@code Qdrant}
     *
     * @param request the request with texts to create embeddings
     * @return the result of creation {@link CreateResponse}
     */
    CreateResponse create(CreateRequest request);

    /**
     * Searches for closest embeddings using the text in the request
     *
     * @param request the request with the text for searching {@link SearchRequest}
     * @return the result of searching {@link SearchResponse}
     */
    SearchResponse search(SearchRequest request);
}
