package com.epam.training.component;

import com.google.common.util.concurrent.ListenableFuture;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantException;
import io.qdrant.client.grpc.Collections;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Slf4j
@Profile("qdrant")
@Component
public class QdrantInitializer {

    private final String collectionName;

    private final long collectionSize;

    private final QdrantClient qdrantClient;

    public QdrantInitializer(@Value("${qdrant.collection.name}") String collectionName,
                             @Value("${qdrant.collection.size}") long collectionSize,
                             QdrantClient qdrantClient) {
        this.collectionName = collectionName;
        this.collectionSize = collectionSize;
        this.qdrantClient = qdrantClient;
    }

    @PostConstruct
    public void initialize() {
        try {
            ListenableFuture<Boolean> exists = qdrantClient.collectionExistsAsync(collectionName);
            if (Boolean.TRUE.equals(exists.get())) {
                log.info("The collection '{}' already exists", collectionName);
                return;
            }

            log.info("Trying to create a new collection: {}", collectionName);

            Collections.VectorParams params = Collections.VectorParams.newBuilder()
                    .setDistance(Collections.Distance.Cosine)
                    .setSize(collectionSize)
                    .build();

            var creationResponse = qdrantClient.createCollectionAsync(collectionName, params).get();
            log.info("A new collection was created:\nCollection: {}\nSize: {}\nResult: {}",
                    collectionName, collectionSize, creationResponse.getResult());
        } catch (ExecutionException | InterruptedException ex) {
            log.error("Unable to create Qdrant collection", ex);
            throw new QdrantException("Unable to create Qdrant collection");
        }
    }
}
