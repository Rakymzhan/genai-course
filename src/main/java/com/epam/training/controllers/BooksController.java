package com.epam.training.controllers;

import com.epam.training.promt.SimplePromptService;
import com.epam.training.service.BooksService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/books")
public class BooksController {

    private final BooksService booksService;

    private final SimplePromptService simplePromptService;

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
}
