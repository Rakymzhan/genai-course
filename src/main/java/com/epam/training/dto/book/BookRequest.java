package com.epam.training.dto.book;

import lombok.Data;

@Data
public class BookRequest {

    private String prompt;

    private double temperature;

    private int maxTokens;
}
