package com.epam.training.service;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.models.ChatCompletions;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatRequestUserMessage;
import com.epam.training.component.ResponseFormatFactory;
import com.epam.training.exception.AiEmptyResponseException;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.orchestration.FunctionResult;
import com.microsoft.semantickernel.orchestration.PromptExecutionSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class BooksServiceImpl implements BooksService {

    private static final String BOOK_JSON_SCHEMA_NAME = "book_result";

    private final OpenAIAsyncClient aiAsyncClient;

    private final Kernel kernel;

    private final ResponseFormatFactory responseFormatFactory;

    private final String deploymentOrModelName;

    public BooksServiceImpl(OpenAIAsyncClient aiAsyncClient,
                            Kernel kernel,
                            ResponseFormatFactory responseFormatFactory,
                            @Value("${client-azureopenai-deployment-name}") String deploymentOrModelName) {
        this.aiAsyncClient = aiAsyncClient;
        this.deploymentOrModelName = deploymentOrModelName;
        this.kernel = kernel;
        this.responseFormatFactory = responseFormatFactory;
    }

    @Override
    public String getBooksSample1(String prompt) {
        var chatCompletions = aiAsyncClient.getChatCompletions(deploymentOrModelName,
                new ChatCompletionsOptions(List.of(new ChatRequestUserMessage(prompt))))
                .block();
        var messages = Optional.ofNullable(chatCompletions)
                .map(ChatCompletions::getChoices).stream()
                .flatMap(Collection::stream)
                .map(chatChoice -> chatChoice.getMessage().getContent())
                .toList();

        return messages.getFirst();
    }

    @Override
    public String getBooksSample2(String prompt) {
        PromptExecutionSettings executionSettings = PromptExecutionSettings.builder()
                .withResponseFormat(responseFormatFactory.createJsonResponseFormat(BOOK_JSON_SCHEMA_NAME))
                .withMaxTokens(16384)
                .build();
        var result = kernel.invokePromptAsync(prompt)
                .withPromptExecutionSettings(executionSettings)
                .block();

        return Optional.ofNullable(result)
                .map(FunctionResult::getResult)
                .map(Object::toString)
                .orElseThrow(AiEmptyResponseException::new);
    }
}
