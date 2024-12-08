package com.epam.training.component;

import com.epam.training.dto.book.BookRequest;
import com.microsoft.semantickernel.orchestration.InvocationContext;
import com.microsoft.semantickernel.orchestration.PromptExecutionSettings;
import com.microsoft.semantickernel.orchestration.responseformat.JsonSchemaResponseFormat;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class RequestEnricher {

    private final SettingsFactory settingsFactory;

    public InvocationContext enrich(BookRequest request, String modelId) {
        PromptSettings promptSettings = settingsFactory.createPromptSettings();
        Optional<Setting> modelSetting = promptSettings.settings().stream()
                .filter(setting -> setting.modelId().equals(modelId))
                .findAny();

        PromptExecutionSettings.Builder executionSettingsBuilder = PromptExecutionSettings.builder()
                .withTemperature(request.getParam().temperature())
                .withMaxTokens(promptSettings.defaultMaxTokens());

        modelSetting.ifPresent(setting -> {
            if (setting.maxTokens() > 0) {
                executionSettingsBuilder.withMaxTokens(setting.maxTokens());
            }

            if (setting.jsonSchemaSupported()) {
                JsonSchemaResponseFormat responseFormat = settingsFactory.createJsonSchemaResponseFormat();
                executionSettingsBuilder.withResponseFormat(responseFormat);
            } else {
                String formattedPrompt = StringUtils.isNotBlank(setting.formattedPrompt())
                        ? setting.formattedPrompt()
                        : promptSettings.defaultFormattedPrompt();
                request.setPrompt(String.format(formattedPrompt, request.getPrompt()));
            }
        });

        return InvocationContext.builder()
                .withPromptExecutionSettings(executionSettingsBuilder.build())
                .build();
    }
}
