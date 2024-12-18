package com.epam.training.component;

import java.util.Set;

public record PromptSettings(String defaultFormattedPrompt, int defaultMaxTokens, Set<Setting> settings) {
}
