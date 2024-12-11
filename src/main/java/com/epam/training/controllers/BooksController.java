package com.epam.training.controllers;

import com.epam.training.dto.book.BookRequest;
import com.epam.training.dto.book.BookResponse;
import com.epam.training.service.BooksService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/books")
public class BooksController {

    private final BooksService booksService;

    /**
     * Returns answer plain text
     *
     * @param input the prompt that a user entered
     * @return plain text
     */
    @GetMapping(value = "/topN/sample1")
    @ResponseBody
    public String getTopNSample1(@RequestParam(value = "input") String input) {
        return booksService.getBooksSample1(input);
    }

    /**
     * Returns answer in JSON format
     * @param input the prompt that a user entered
     * @return the answer in JSON
     */
    @GetMapping(value = "/topN/sample2", produces = "application/json")
    @ResponseBody
    public String getTopNSample2(@RequestParam(value = "input") String input) {
        return booksService.getBooksSample2(input);
    }

    /**
     * Receives a request {@link BookRequest} that contains {@code prompt, temperature and maxTokens}
     * and calls service method that uses {@code ChatHistory and ChatCompletionService}
     *
     * @param request the request object {@link BookRequest}
     * @return the ChatGPT answer in JSON
     */
    @PostMapping(value = "/topN/sample3", consumes = "application/json", produces = "application/json")
    public BookResponse getTopNSample3(@RequestBody BookRequest request) {
        return booksService.getBooksSample3(request);
    }

    /**
     * Receives a request {@link BookRequest} that contains {@code prompt, temperature and maxTokens}
     * and calls service method that uses {@code ChatHistory and KernelFunction}
     *
     * @param request the request object {@link BookRequest}
     * @return the ChatGPT answer in JSON
     */
    @PostMapping(value = "/topN/sample4", consumes = "application/json", produces = "application/json")
    public BookResponse getTopNSample4(@RequestBody BookRequest request) {
        return booksService.getBooksSample4(request);
    }
}
