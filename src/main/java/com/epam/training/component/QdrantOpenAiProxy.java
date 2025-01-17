package com.epam.training.component;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.models.EmbeddingItem;
import com.azure.ai.openai.models.EmbeddingsOptions;
import com.epam.training.dto.embedding.SearchResult;
import io.qdrant.client.*;
import io.qdrant.client.grpc.JsonWithInt;
import io.qdrant.client.grpc.Points;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@Component
public class QdrantOpenAiProxy {

    private static final String SAVE_ERROR_FORMAT = "Unable to save points for the text: %s";

    private static final String SEARCH_ERROR_FORMAT = "Unable to search points for the text: %s";

    private static final String POINT_STRUCT_PAYLOAD_KEY = "info";

    private final QdrantClient qdrantClient;

    private final OpenAIAsyncClient openAIAsyncClient;

    private final String textEmbeddingModel;

    public QdrantOpenAiProxy(QdrantClient qdrantClient,
                             OpenAIAsyncClient openAIAsyncClient,
                             @Value("${openai.text.embedding.model}") String textEmbeddingModel) {
        this.qdrantClient = qdrantClient;
        this.openAIAsyncClient = openAIAsyncClient;
        this.textEmbeddingModel = textEmbeddingModel;
    }

    /**
     * Creates embeddings from the text and stores them into {@code Qdrant collection}
     *
     * @param collectionName the name of {@code Qdrant collection}
     * @param text the text embeddings of which will be stored into {@code Qdrant collection}
     * @return the result of operation {@link Points.UpdateResult}
     */

    public Points.UpdateResult put(String collectionName, String text) {
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
            log.info("Point structs saved for the text: {} with the result: {}",
                    text, updateResult);

            return updateResult;
        } catch (ExecutionException | InterruptedException ex) {
            String errorMessage = String.format(SAVE_ERROR_FORMAT, text);
            log.error(errorMessage, ex);
            throw new QdrantException(errorMessage);
        }
    }

    /**
     * Searches for closest embeddings of the text in {@code Qdrant collection}
     *
     * @param collectionName the name of {@code Qdrant collection}
     * @param text the text for searching
     * @param limit the count of texts to be returned
     * @return the result of searching {@link SearchResult}
     */
    public Set<SearchResult> get(String collectionName, String text, long limit) {
        log.info("Searching embeddings for text {}", text);

        List<EmbeddingItem> items = getEmbeddings(text);
        List<Float> vectors = items.stream()
                .flatMap(item -> item.getEmbedding().stream())
                .toList();

        try {
            List<Points.ScoredPoint> scoredPoints = qdrantClient.searchAsync(
                    Points.SearchPoints.newBuilder()
                            .setCollectionName(collectionName)
                            .addAllVector(vectors)
                            .setWithPayload(WithPayloadSelectorFactory.enable(true))
                            .setLimit(limit)
                            .build()
            ).get();
            log.info("Scored points found. Count: {}", scoredPoints.size());

            return scoredPoints.stream()
                    .map(QdrantOpenAiProxy::mapSearchResult)
                    .collect(Collectors.toSet());
        } catch (ExecutionException | InterruptedException ex) {
            String errorMessage = String.format(SEARCH_ERROR_FORMAT, text);
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
        return Objects.requireNonNull(openAIAsyncClient.getEmbeddings(textEmbeddingModel, options).block()).getData();
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
