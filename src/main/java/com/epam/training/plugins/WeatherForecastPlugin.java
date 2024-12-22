package com.epam.training.plugins;

import com.epam.training.component.HelperComponent;
import com.epam.training.dto.weather.WeatherResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.microsoft.semantickernel.semanticfunctions.annotations.DefineKernelFunction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class WeatherForecastPlugin {

    private final HelperComponent helperComponent;

    @DefineKernelFunction(name = "get_week_weather_forecast", description = "Gets a weather forecast for a week")
    public WeatherResponse getWeekWeatherForecast() throws JsonProcessingException {
        log.info("SemanticKernel is invoking getLamps method of {}", WeatherForecastPlugin.class.getSimpleName());
        return helperComponent.getWeekWeatherForecast();
    }
}
