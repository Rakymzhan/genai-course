package com.epam.training.controllers;

import com.epam.training.dto.embedding.SearchRequest;
import com.epam.training.dto.laptop.AddLaptopsResponse;
import com.epam.training.service.LaptopStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/laptops")
public class LaptopStoreController {

    private final LaptopStoreService laptopStoreService;

    /**
     * Receives a file {@link MultipartFile} containing price list and calls service to handle.
     *
     * @param file the {@code CSV} file containing price list.
     *             The format of the file should be same as one in {@code /resources/data/laptops.csv}.
     *             If the file is null then sample file will be used
     * @return the result of adding price list {@link AddLaptopsResponse}
     */
    @PostMapping(value = "/add")
    public AddLaptopsResponse add(@RequestParam(value = "file", required = false) MultipartFile file) {
        return laptopStoreService.add(file);
    }

    /**
     * Receives a request {@link SearchRequest} and calls service method to find laptops.
     *
     * @param request the request with the prompt {@link SearchRequest}
     * @return the result from AI
     */
    @PostMapping(value = "/find", consumes = "application/json")
    public ResponseEntity<String> find(@RequestBody SearchRequest request) {
        return ResponseEntity.ok(laptopStoreService.find(request));
    }
}
