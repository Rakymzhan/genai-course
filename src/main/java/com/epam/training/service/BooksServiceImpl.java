package com.epam.training.service;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.epam.training.component.RequestEnricher;
import com.epam.training.component.ResponseParser;
import com.epam.training.dto.Comparison;
import com.epam.training.dto.book.Book;
import com.epam.training.dto.book.BookRequest;
import com.epam.training.dto.book.BookResponse;
import com.epam.training.dto.book.BookResponseList;
import com.epam.training.dto.deployment.DeploymentResponse;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.orchestration.InvocationContext;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import com.microsoft.semantickernel.services.chatcompletion.ChatMessageContent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class BooksServiceImpl implements BooksService {

    private static final String SYSTEM_MESSAGE = "You are a scientist of World History and you can help users to find the best books of history.";

    private final OpenAIAsyncClient aiAsyncClient;

    private final DialApiProxy dialApiProxy;

    private final RequestEnricher requestEnricher;

    private final ResponseParser responseParser;

    @Override
    public BookResponseList getBooks(BookRequest request) {
        DeploymentResponse deploymentResponse = dialApiProxy.getDeployments();

        List<BookResponse> responses = new ArrayList<>(deploymentResponse.data().size());
        deploymentResponse.data().forEach(deployment -> {
            log.info("Trying to retrieve data from model: {}", deployment.id());

            InvocationContext context = requestEnricher.enrich(request, deployment.id());

            ChatHistory chatHistory = new ChatHistory();
            chatHistory.addSystemMessage(SYSTEM_MESSAGE);
            chatHistory.addUserMessage(request.getPrompt());

            ChatCompletionService chatCompletionService = OpenAIChatCompletion.builder()
                    .withOpenAIAsyncClient(aiAsyncClient)
                    .withModelId(deployment.id())
                    .build();

            List<ChatMessageContent<?>> contents = chatCompletionService.getChatMessageContentsAsync(
                    chatHistory, null, context)
                    .block();

            String responseContent = contents.stream()
                    .map(ChatMessageContent::getContent)
                    .toList()
                    .getFirst();

            log.info("Response received from model: {}", deployment.id());
            log.info("Response received: {}", responseContent);

            BookResponse bookResponse = responseParser.mapToBookResponse(deployment.id(), responseContent);
            if (!bookResponse.getBooks().isEmpty()) {
                responses.add(bookResponse);
            }
        });

        compareResponses(responses);

        return new BookResponseList(responses);
    }

    private static void compareResponses(List<BookResponse> responses) {
        responses.forEach(bookResponse -> {
            List<BookResponse> others = responses.stream()
                    .filter(resp -> !resp.equals(bookResponse))
                    .toList();
            List<Comparison> comparisons = new ArrayList<>(others.size());
            others.forEach(otherResponse -> {
                comparisons.add(new Comparison(otherResponse.getModelId(), compare(bookResponse, otherResponse)));
            });

            bookResponse.setComparisons(comparisons);
        });
    }

    private static double compare(BookResponse first, BookResponse second) {
        int matchCount = 0;
        int bookCount = first.getBooks().size();
        for (Book book : first.getBooks()) {
            if (second.getBooks().contains(book)) {
                matchCount++;
            }
        }

        return ((double) matchCount / bookCount) * 100;
    }
}
