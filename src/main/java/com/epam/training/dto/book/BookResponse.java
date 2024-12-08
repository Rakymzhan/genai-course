package com.epam.training.dto.book;

import com.epam.training.dto.Comparison;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class BookResponse {

    private String modelId;

    private List<Book> books = Collections.emptyList();

    private List<Comparison> comparisons;
}
