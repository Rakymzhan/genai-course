package com.epam.training.service;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.models.EmbeddingItem;
import com.azure.ai.openai.models.EmbeddingsOptions;
import com.epam.training.dto.embedding.*;
import io.qdrant.client.*;
import io.qdrant.client.grpc.JsonWithInt;
import io.qdrant.client.grpc.Points;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EmbeddingServiceImpl implements EmbeddingService {

    private static final String POINT_STRUCT_PAYLOAD_KEY = "info";

    private static final String FAILED = "FAILED";

    private static final String SEARCH_ERROR_FORMAT = "Unable to search points for the text: %s";

    private final String textEmbeddingModel;

    private final OpenAIAsyncClient openAIAsyncClient;

    private final String collectionName;

    private final QdrantClient qdrantClient;

    public EmbeddingServiceImpl(@Value("${openai.text.embedding.model}") String textEmbeddingModel,
                                OpenAIAsyncClient openAIAsyncClient,
                                @Value("${qdrant.collection.name}") String collectionName,
                                QdrantClient qdrantClient) {
        this.textEmbeddingModel = textEmbeddingModel;
        this.openAIAsyncClient = openAIAsyncClient;
        this.collectionName = collectionName;
        this.qdrantClient = qdrantClient;
    }

    @Override
    public CreateResponse create(CreateRequest request) {
        log.info("Creating embeddings for the given request:\n Text count in the request: {}", request.texts());

        Set<CreateResult> results = new LinkedHashSet<>();
        for (String text : request.texts()) {
            List<EmbeddingItem> items = getEmbeddings(text);
            List<List<Float>> vectorsList = items.stream()
                    .map(EmbeddingItem::getEmbedding)
                    .toList();
            log.info("Vectors retrieved for the text: {}", text);

            List<Points.PointStruct> pointStructs = vectorsList.stream()
                    .map(vectors -> mapPointStruct(vectors, text))
                    .toList();
            log.info("Point structs generated for the text: {}", text);

            try {
                Points.UpdateResult updateResult = qdrantClient.upsertAsync(collectionName, pointStructs).get();
                String updateStatus = updateResult.getStatus().name();
                log.info("Point structs saved for the text: {} with the result: {}",
                        text, updateResult);

                results.add(new CreateResult(updateStatus, text));
            } catch (ExecutionException | InterruptedException ex) {
                log.error("Unable to save point structs for the text: " + text, ex);
                results.add(new CreateResult(FAILED, text));
            }
        }

        return new CreateResponse(results);
    }

    @Override
    public SearchResponse search(SearchRequest request) {
        log.info("Searching embeddings for text {}", request.text());

        List<EmbeddingItem> items = getEmbeddings(request.text());
        List<Float> vectors = items.stream()
                .flatMap(item -> item.getEmbedding().stream())
                .toList();

        try {
            List<Points.ScoredPoint> scoredPoints = qdrantClient.searchAsync(
                    Points.SearchPoints.newBuilder()
                            .setCollectionName(collectionName)
                            .addAllVector(vectors)
                            .setWithPayload(WithPayloadSelectorFactory.enable(true))
                            .setLimit(1)
                            .build()
                    ).get();
            log.info("Scored points found. Count: {}", scoredPoints.size());

            Set<SearchResult> results = scoredPoints.stream()
                    .map(EmbeddingServiceImpl::mapSearchResult)
                    .collect(Collectors.toSet());

            return new SearchResponse(results);
        } catch (ExecutionException | InterruptedException ex) {
            String errorMessage = String.format(SEARCH_ERROR_FORMAT, request.text());
            log.error(errorMessage, ex);
            throw new QdrantException(errorMessage);
        }
    }

    /**
     * Retrieves embeddings from {@code AI} for the given text.
     *
     * @param text the text to be used to get embeddings
     * @return the list of embeddings {@link EmbeddingItem}
     */
    private List<EmbeddingItem> getEmbeddings(String text) {
        EmbeddingsOptions options = new EmbeddingsOptions(List.of(text));
        return Objects.requireNonNull(openAIAsyncClient.getEmbeddings(textEmbeddingModel, options)
                .block())
                .getData();
    }

    /**
     * Maps the given text and its vectors to {@link Points.PointStruct}.
     *
     * @param vectors the vectors of the given text
     * @param text the text
     * @return {@link Points.PointStruct}
     */
    private static Points.PointStruct mapPointStruct(List<Float> vectors, String text) {
        return Points.PointStruct.newBuilder()
                .setId(PointIdFactory.id(UUID.randomUUID()))
                .setVectors(VectorsFactory.vectors(vectors))
                .putAllPayload(Map.of(POINT_STRUCT_PAYLOAD_KEY, ValueFactory.value(text)))
                .build();
    }

    /**
     * Maps the given {@code scoredPoint} to {@link SearchResult}.
     *
     * @param scoredPoint the scored point of the text
     * @return the result {@link SearchResult}
     */
    private static SearchResult mapSearchResult(Points.ScoredPoint scoredPoint) {
        JsonWithInt.Value payload = scoredPoint.getPayloadOrThrow(POINT_STRUCT_PAYLOAD_KEY);
        return new SearchResult(payload.getStringValue(), scoredPoint.getScore());
    }
}
