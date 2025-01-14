package com.epam.training.plugins;

import com.epam.training.component.HelperComponent;
import com.microsoft.semantickernel.semanticfunctions.annotations.DefineKernelFunction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.format.DateTimeFormatter;

@Slf4j
@RequiredArgsConstructor
public class CurrentDateTimePlugin {

    private static final String DATE_TIME_PATTERN = "dd/MM/yyyy HH:mm:ss";

    private final HelperComponent helperComponent;

    @DefineKernelFunction(name = "getCurrentDateTime")
    public String getCurrentDateTime() {
        log.info("SemanticKernel is invoking {}", CurrentDateTimePlugin.class.getSimpleName());
        return DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)
                .format(helperComponent.getCurrentDateTime());
    }
}
