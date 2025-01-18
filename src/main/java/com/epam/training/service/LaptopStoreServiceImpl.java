package com.epam.training.service;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.epam.training.component.HelperComponent;
import com.epam.training.component.QdrantOpenAiProxy;
import com.epam.training.dto.embedding.SearchRequest;
import com.epam.training.dto.embedding.SearchResult;
import com.epam.training.dto.laptop.AddLaptopsResponse;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.orchestration.InvocationContext;
import com.microsoft.semantickernel.orchestration.PromptExecutionSettings;
import com.microsoft.semantickernel.services.chatcompletion.AuthorRole;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.microsoft.semantickernel.services.chatcompletion.ChatMessageContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LaptopStoreServiceImpl implements LaptopStoreService {

    private static final String FINAL_PROMPT_FORMAT =
            "Based on only the context provided, answer the query below: Context: [%s]\\n\\n Query: %s";

    private static final String NO_RESPONSE_MESSAGE = "No response";

    private static final String DELIMITER = ",";

    private static final Predicate<ChatMessageContent<?>> ASSISTANT_MESSAGE =
            messageContent -> (AuthorRole.ASSISTANT.equals(messageContent.getAuthorRole()));

    private final HelperComponent helperComponent;

    private final QdrantOpenAiProxy qdrantOpenAiProxy;

    private final OpenAIAsyncClient openAIAsyncClient;

    private final String collectionName;

    private final String openAiModel;

    public LaptopStoreServiceImpl(HelperComponent helperComponent,
                                  QdrantOpenAiProxy qdrantOpenAiProxy,
                                  OpenAIAsyncClient openAIAsyncClient,
                                  @Value("${qdrant.collection.name.laptop}") String collectionName,
                                  @Value("${openai.predefined.deployments}") List<String> predefinedDeployments) {
        this.helperComponent = helperComponent;
        this.qdrantOpenAiProxy = qdrantOpenAiProxy;
        this.openAIAsyncClient = openAIAsyncClient;
        this.collectionName = collectionName;
        this.openAiModel = predefinedDeployments.getFirst();
    }

    @Override
    public AddLaptopsResponse add(MultipartFile file) {
        List<String> priceList = helperComponent.loadPriceList(file);
        priceList.forEach(line -> qdrantOpenAiProxy.put(collectionName, line));

        return new AddLaptopsResponse(true, priceList.size());
    }

    @Override
    public String find(SearchRequest request) {
        Set<SearchResult> ragResults = qdrantOpenAiProxy.get(collectionName, request.text(), 9);
        String context = ragResults.stream()
                .map(SearchResult::text)
                .collect(Collectors.joining(DELIMITER));

        String finalPrompt = String.format(FINAL_PROMPT_FORMAT, context, request.text());
        log.info(finalPrompt);

        ChatCompletionService chatCompletionService = OpenAIChatCompletion.builder()
                .withOpenAIAsyncClient(openAIAsyncClient)
                .withModelId(openAiModel)
                .build();
        List<ChatMessageContent<?>> contents = chatCompletionService.getChatMessageContentsAsync(
                finalPrompt, null, buildInvocationContext())
                .block();

        String response = contents.stream()
                .filter(ASSISTANT_MESSAGE)
                .map(ChatMessageContent::getContent)
                .findAny()
                .orElse(NO_RESPONSE_MESSAGE);

        log.info("Response received from AI: {}", response);

        return response;
    }

    private static InvocationContext buildInvocationContext() {
        PromptExecutionSettings executionSettings = PromptExecutionSettings.builder()
                .withTemperature(0.0)
                .build();

        return InvocationContext.builder()
                .withPromptExecutionSettings(executionSettings)
                .build();
    }
}
