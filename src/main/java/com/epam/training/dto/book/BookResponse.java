package com.epam.training.dto.book;

import lombok.Data;

import java.util.List;

@Data
public class BookResponse {

    private List<Book> books;
}
