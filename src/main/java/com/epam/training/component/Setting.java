package com.epam.training.component;

public record Setting(String modelId, String formattedPrompt, boolean jsonSchemaSupported, int maxTokens) {
}
