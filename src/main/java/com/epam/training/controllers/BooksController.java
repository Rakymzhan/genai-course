package com.epam.training.controllers;

import com.epam.training.dto.book.BookRequest;
import com.epam.training.dto.book.BookResponseList;
import com.epam.training.service.BooksService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/books")
public class BooksController {

    private final BooksService booksService;

    /**
     * Receives a request {@link BookRequest} and calls service method
     *
     * @param request the request object {@link BookRequest}
     * @return the ChatGPT answer in JSON
     */
    @PostMapping(value = "/topN", consumes = "application/json", produces = "application/json")
    public BookResponseList getTopN(@RequestBody BookRequest request) {
        return booksService.getBooks(request);
    }
}
