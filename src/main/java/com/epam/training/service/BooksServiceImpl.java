package com.epam.training.service;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.models.ChatCompletions;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatRequestUserMessage;
import com.epam.training.component.ResponseFormatFactory;
import com.epam.training.dto.book.BookRequest;
import com.epam.training.dto.book.BookResponse;
import com.epam.training.exception.AiEmptyResponseException;
import com.epam.training.exception.AiResponseParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.orchestration.FunctionResult;
import com.microsoft.semantickernel.orchestration.InvocationContext;
import com.microsoft.semantickernel.orchestration.PromptExecutionSettings;
import com.microsoft.semantickernel.semanticfunctions.KernelFunction;
import com.microsoft.semantickernel.semanticfunctions.KernelFunctionArguments;
import com.microsoft.semantickernel.services.ServiceNotFoundException;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import com.microsoft.semantickernel.services.chatcompletion.ChatMessageContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class BooksServiceImpl implements BooksService {

    private static final String BOOK_JSON_SCHEMA_NAME = "book_result";

    private static final String SYSTEM_MESSAGE = "You are a scientist of World History";

    private final OpenAIAsyncClient aiAsyncClient;

    private final Kernel kernel;

    private final ResponseFormatFactory responseFormatFactory;

    private final String deploymentOrModelName;

    private final ObjectMapper objectMapper;

    public BooksServiceImpl(OpenAIAsyncClient aiAsyncClient,
                            Kernel kernel,
                            ResponseFormatFactory responseFormatFactory,
                            @Value("${client-azureopenai-deployment-name}") String deploymentOrModelName,
                            ObjectMapper objectMapper) {
        this.aiAsyncClient = aiAsyncClient;
        this.deploymentOrModelName = deploymentOrModelName;
        this.kernel = kernel;
        this.responseFormatFactory = responseFormatFactory;
        this.objectMapper = objectMapper;
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

    @Override
    public BookResponse getBooksSample3(BookRequest request) {
        log.info(request.toString());

        ChatHistory chatHistory = new ChatHistory();
        chatHistory.addSystemMessage(SYSTEM_MESSAGE);
        chatHistory.addUserMessage(request.getPrompt());

        InvocationContext context = buildInvocationContext(request);

        try {
            ChatCompletionService completionService = kernel.getService(ChatCompletionService.class);
            List<ChatMessageContent<?>> contents = completionService.getChatMessageContentsAsync(chatHistory, kernel, context).block();

            String response = contents.stream()
                    .map(ChatMessageContent::getContent)
                    .toList()
                    .getFirst();
            log.info("Response received from GhatGPT:\n" + response);

            return objectMapper.readValue(response, BookResponse.class);
        } catch (JsonProcessingException ex) {
            throw new AiResponseParseException(ex);
        } catch (ServiceNotFoundException ex) {
            throw new IllegalArgumentException("ChatCompletionService not found", ex);
        }
    }

    @Override
    public BookResponse getBooksSample4(BookRequest request) {
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.addSystemMessage(SYSTEM_MESSAGE);

        InvocationContext context = buildInvocationContext(request);

        FunctionResult<String> result = kernel.invokeAsync(getKernelFunction())
                .withArguments(getKernelFunctionArguments(request.getPrompt(), chatHistory))
                .withInvocationContext(context)
                .block();

        String response = result.getResult();
        chatHistory.addUserMessage(request.getPrompt());
        chatHistory.addAssistantMessage(response);
        log.info("Response received from GhatGPT:\n" + response);

        try {
            return objectMapper.readValue(response, BookResponse.class);
        } catch (JsonProcessingException ex) {
            throw new AiResponseParseException(ex);
        }
    }

    private KernelFunction<String> getKernelFunction() {
        return KernelFunction.<String>createFromPrompt("""
                        {{$chatHistory}}
                        <message role="user">{{$request}}</message>""")
                .build();
    }

    private KernelFunctionArguments getKernelFunctionArguments(String prompt, ChatHistory chatHistory) {
        return KernelFunctionArguments.builder()
                .withVariable("request", prompt)
                .withVariable("chatHistory", chatHistory)
                .build();
    }

    private static InvocationContext buildInvocationContext(BookRequest request) {
        PromptExecutionSettings executionSettings = PromptExecutionSettings.builder()
                .withJsonSchemaResponseFormat(BookResponse.class)
                .withTemperature(request.getTemperature())
                .withMaxTokens(request.getMaxTokens())
                .build();

        return InvocationContext.builder()
                .withPromptExecutionSettings(executionSettings)
                .build();
    }
}
