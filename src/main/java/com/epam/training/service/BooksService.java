package com.epam.training.service;

import com.epam.training.dto.book.BookRequest;
import com.epam.training.dto.book.BookResponse;

public interface BooksService {

    /**
     * Returns answer plain text
     *
     * @param prompt the prompt that a user entered
     * @return List of texts
     */
    String getBooksSample1(String prompt);

    /**
     * Returns answer in JSON format
     * @param prompt the prompt that a user entered
     * @return the answer in JSON
     */
    String getBooksSample2(String prompt);

    /**
     * Receives a request {@link BookRequest} that contains {@code prompt, temperature and maxTokens}
     * and sends a request to AI using {@code ChatHistory and ChatCompletionService}
     *
     * @param request the request object {@link BookRequest}
     * @return the ChatGPT answer in JSON
     */
    BookResponse getBooksSample3(BookRequest request);

    /**
     * Receives a request {@link BookRequest} that contains {@code prompt, temperature and maxTokens}
     * and sends a request to AI using {@code ChatHistory and KernelFunction}
     *
     * @param request the request object {@link BookRequest}
     * @return the ChatGPT answer in JSON
     */
    BookResponse getBooksSample4(BookRequest request);
}
