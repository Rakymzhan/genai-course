package com.epam.training.dto.embedding;

import java.util.Set;

public record SearchResponse(Set<SearchResult> results) {
}
