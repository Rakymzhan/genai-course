package com.epam.training.service;

import com.epam.training.dto.book.BookRequest;
import com.epam.training.dto.book.BookResponseList;

public interface BooksService {

    /**
     * Receives a request {@link BookRequest} and sends a request to AI.
     * NOTE! Formatting of output using {@code JSON} is supported only beginning from gpt-35-turbo and higher versions.
     * Also, same formatting settings using {@code PromptExecutionSettings} don't work for all models.
     * Basically, a model throws an {@code Exception} when it does not support it.
     * So, I found that prompt engineering is the better way for the task when using different models,
     * but there is a no guarantee that we can achieve same result from wide range of models.
     *
     * @param request the request object {@link BookRequest}
     * @return the ChatGPT answer in JSON
     */
    BookResponseList getBooks(BookRequest request);
}
