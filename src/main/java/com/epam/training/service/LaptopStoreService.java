package com.epam.training.service;

import com.epam.training.dto.embedding.SearchRequest;
import com.epam.training.dto.laptop.AddLaptopsResponse;
import org.springframework.web.multipart.MultipartFile;

public interface LaptopStoreService {

    /**
     * Parses the laptop price list from file and stores it in {@code Qdrant} for caching
     *
     * @param file the {@code CSV} file containing price list.
     *             The format of the file should be same as one in {@code /resources/data/laptops.csv}.
     *             If the file is null or empty then sample file will be used
     * @return the result of adding price list {@link AddLaptopsResponse}
     */
    AddLaptopsResponse add(MultipartFile file);

    /**
     * Searches a laptop based on user prompt and the context
     *
     * @param request the request with the prompt
     * @return the result from AI based on the context
     */
    String find(SearchRequest request);
}
