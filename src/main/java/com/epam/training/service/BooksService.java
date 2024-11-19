package com.epam.training.service;

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
}
