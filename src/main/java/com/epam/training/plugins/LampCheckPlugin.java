package com.epam.training.plugins;

import com.epam.training.component.HelperComponent;
import com.epam.training.dto.lamp.LampResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.microsoft.semantickernel.semanticfunctions.annotations.DefineKernelFunction;
import com.microsoft.semantickernel.semanticfunctions.annotations.KernelFunctionParameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class LampCheckPlugin {

    private final HelperComponent helperComponent;

    @DefineKernelFunction(name = "get_lamps", description = "Gets a list of lamps, their location and current state")
    public LampResponse getLamps() {
        log.info("SemanticKernel is invoking getLamps method of {}", LampCheckPlugin.class.getSimpleName());
        return helperComponent.getLamps();
    }

    @DefineKernelFunction(name = "change_state", description = "Changes the state of the lamp")
    public String changeState(@KernelFunctionParameter(name = "lamp", description = "The lamp with the new state. Example lamp: {\"location\":\"KITCHEN\",\"state\":\"ON\"}") String lamp) throws JsonProcessingException {
        log.info("SemanticKernel is invoking changeState method of {}", LampCheckPlugin.class.getSimpleName());
        helperComponent.putLamp(lamp);
        return lamp;
    }
}
