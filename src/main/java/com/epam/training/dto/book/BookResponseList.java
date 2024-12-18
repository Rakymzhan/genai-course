package com.epam.training.dto.book;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BookResponseList {

    List<BookResponse> responses;
}
