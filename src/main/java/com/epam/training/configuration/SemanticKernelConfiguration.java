package com.epam.training.configuration;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.epam.training.component.HelperComponent;
import com.epam.training.plugins.CurrentDateTimePlugin;
import com.epam.training.plugins.LampCheckPlugin;
import com.epam.training.plugins.WeatherForecastPlugin;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.plugin.KernelPlugin;
import com.microsoft.semantickernel.plugin.KernelPluginFactory;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration class for setting up Semantic Kernel components.
 * <p>
 * This configuration provides several beans necessary for the interaction with
 * Azure OpenAI services and the creation of kernel plugins. It defines beans for
 * chat completion services, kernel plugins, kernel instance, invocation context,
 * and prompt execution settings.
 */
@RequiredArgsConstructor
@Configuration
public class SemanticKernelConfiguration {

    private final HelperComponent helperComponent;

    /**
     * Creates a {@link ChatCompletionService} bean for handling chat completions using Azure OpenAI.
     *
     * @param deployments the predefined Azure OpenAI deployments
     * @param openAIAsyncClient the {@link OpenAIAsyncClient} to communicate with Azure OpenAI
     * @return an instance of {@link ChatCompletionService}
     */
    @Bean
    public ChatCompletionService chatCompletionService(@Value("${openai.predefined.deployments}") List<String> deployments,
                                                       OpenAIAsyncClient openAIAsyncClient) {
        return OpenAIChatCompletion.builder()
                .withModelId(deployments.getFirst())
                .withOpenAIAsyncClient(openAIAsyncClient)
                .build();
    }

    /**
     * Creates a {@link KernelPlugin} that helps to get current date and time.
     *
     * @return an instance of {@link KernelPlugin}
     */
    @Bean
    public KernelPlugin currentDateTimePlugin() {
        return KernelPluginFactory.createFromObject(
                new CurrentDateTimePlugin(helperComponent), "CurrentDateTime");
    }

    @Bean
    public KernelPlugin lampCheckPlugin() {
        return KernelPluginFactory.createFromObject(
                new LampCheckPlugin(helperComponent), "LampCheck");
    }

    @Bean
    public KernelPlugin weatherPlugin() {
        return KernelPluginFactory.createFromObject(
                new WeatherForecastPlugin(helperComponent), "WeatherForecast");
    }

    /**
     * Creates a {@link Kernel} bean to manage AI services and plugins.
     *
     * @param chatCompletionService the {@link ChatCompletionService} for handling completions
     * @param currentDateTimePlugin the {@link KernelPlugin} to be used in the kernel
     * @return an instance of {@link Kernel}
     */
    @Bean
    public Kernel defaultKernel(ChatCompletionService chatCompletionService, KernelPlugin currentDateTimePlugin) {
        return Kernel.builder()
                .withAIService(ChatCompletionService.class, chatCompletionService)
                .withPlugin(currentDateTimePlugin)
                .build();
    }
}
