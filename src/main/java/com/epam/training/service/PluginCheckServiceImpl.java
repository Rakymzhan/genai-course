package com.epam.training.service;

import com.epam.training.dto.UserRequest;
import com.epam.training.dto.UserResponse;
import com.epam.training.dto.weather.WeatherResponse;
import com.epam.training.exception.SemanticKernelConfigException;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.orchestration.FunctionResult;
import com.microsoft.semantickernel.orchestration.InvocationContext;
import com.microsoft.semantickernel.orchestration.PromptExecutionSettings;
import com.microsoft.semantickernel.orchestration.ToolCallBehavior;
import com.microsoft.semantickernel.plugin.KernelPlugin;
import com.microsoft.semantickernel.semanticfunctions.KernelFunction;
import com.microsoft.semantickernel.semanticfunctions.KernelFunctionFromPrompt;
import com.microsoft.semantickernel.services.ServiceNotFoundException;
import com.microsoft.semantickernel.services.chatcompletion.AuthorRole;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.microsoft.semantickernel.services.chatcompletion.ChatMessageContent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Slf4j
@RequiredArgsConstructor
@Service
public class PluginCheckServiceImpl implements PluginCheckService {

    private static final String UNABLE_TO_HELP_RESPONSE = "Sorry, I cannot help you";

    private static final Predicate<ChatMessageContent<?>> RESPONSE_PREDICATE =
            response -> response.getAuthorRole().equals(AuthorRole.ASSISTANT) &&
                    StringUtils.isNotBlank(response.getContent());

    /**
     * This is an instance of {@link Kernel} that was configured with {@code CurrentDateTimePlugin}
     */
    private final Kernel defaultKernel;

    private final KernelPlugin lampCheckPlugin;

    private final KernelPlugin weatherPlugin;

    private final ChatCompletionService chatCompletionService;

    @Override
    public UserResponse getCurrentDateTime(UserRequest request) {
        try {
            ChatCompletionService chatCompletionService = defaultKernel.getService(ChatCompletionService.class);

            List<ChatMessageContent<?>> responseMessages = chatCompletionService.getChatMessageContentsAsync(
                    request.prompt(), defaultKernel, buildInvocationContext())
                    .block();

            String response = responseMessages.stream()
                    .filter(RESPONSE_PREDICATE)
                    .findAny()
                    .map(ChatMessageContent::getContent)
                    .orElse(UNABLE_TO_HELP_RESPONSE);
            log.info("AI response is\n {}", response);
            return new UserResponse(response);
        } catch (ServiceNotFoundException ex) {
            throw new SemanticKernelConfigException("Check your SemanticKernel configuration", ex);
        }
    }

    @Override
    public UserResponse checkLamp(UserRequest request) {
        Kernel kernel = Kernel.builder()
                .withAIService(ChatCompletionService.class, chatCompletionService)
                .withPlugin(lampCheckPlugin)
                .build();

        KernelFunction<String> prompt = KernelFunctionFromPrompt.<String>createFromPrompt(request.prompt())
                .build();

        FunctionResult<String> result = prompt.invokeAsync(kernel)
                .withInvocationContext(buildInvocationContext())
                .block();
        return new UserResponse(result.getResult());
    }

    @Override
    public WeatherResponse getWeather(UserRequest request) {
        Kernel kernel = Kernel.builder()
                .withAIService(ChatCompletionService.class, chatCompletionService)
                .withPlugin(weatherPlugin)
                .build();

        KernelFunction<WeatherResponse> prompt = KernelFunctionFromPrompt.<WeatherResponse>createFromPrompt(
                        request + "{{WeatherForecast.get_week_weather_forecast}}")
                .build();

        FunctionResult<WeatherResponse> result = prompt.invokeAsync(kernel)
                .withPromptExecutionSettings(PromptExecutionSettings.builder()
                        .withJsonSchemaResponseFormat(WeatherResponse.class)
                        .build())
                .withResultTypeAutoConversion(WeatherResponse.class)
                .block();

        return Optional.ofNullable(result)
                .map(FunctionResult::getResult)
                .orElse(new WeatherResponse(Collections.emptyList()));
    }

    private static InvocationContext buildInvocationContext() {
        return InvocationContext.builder()
                .withToolCallBehavior(ToolCallBehavior.allowAllKernelFunctions(true))
                .build();
    }
}
