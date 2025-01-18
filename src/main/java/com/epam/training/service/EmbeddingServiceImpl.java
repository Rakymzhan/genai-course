package com.epam.training.service;

import com.epam.training.component.QdrantOpenAiProxy;
import com.epam.training.dto.embedding.*;
import io.qdrant.client.QdrantException;
import io.qdrant.client.grpc.Points;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.Set;

@Slf4j
@Service
public class EmbeddingServiceImpl implements EmbeddingService {

    private static final String FAILED = "FAILED";

    private final QdrantOpenAiProxy qdrantOpenAiProxy;

    private final String collectionName;

    public EmbeddingServiceImpl(QdrantOpenAiProxy qdrantOpenAiProxy,
                                @Value("${qdrant.collection.name.learning}") String collectionName) {
        this.qdrantOpenAiProxy = qdrantOpenAiProxy;
        this.collectionName = collectionName;
    }

    @Override
    public CreateResponse create(CreateRequest request) {
        log.info("Creating embeddings for the given request:\n Text count in the request: {}", request.texts());

        Set<CreateResult> results = new LinkedHashSet<>();
        for (String text : request.texts()) {
            try {
                Points.UpdateResult updateResult = qdrantOpenAiProxy.put(collectionName, text);
                results.add(new CreateResult(updateResult.getStatus().name(), text));
            } catch (QdrantException ex) {
                results.add(new CreateResult(FAILED, text));
            }
        }

        return new CreateResponse(results);
    }

    @Override
    public SearchResponse search(SearchRequest request) {
        return new SearchResponse(qdrantOpenAiProxy.get(collectionName, request.text(), 1));
    }
}
