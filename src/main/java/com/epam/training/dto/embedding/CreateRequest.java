package com.epam.training.dto.embedding;

import java.util.Set;

public record CreateRequest(Set<String> texts) {
}
