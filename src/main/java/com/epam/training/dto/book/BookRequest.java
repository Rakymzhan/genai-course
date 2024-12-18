package com.epam.training.dto.book;

import com.epam.training.dto.PromptParam;
import lombok.Data;

@Data
public class BookRequest {

    private String prompt;

    private PromptParam param;
}
