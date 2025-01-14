package com.epam.training.dto.weather;

import java.util.List;

public record WeatherResponse(List<DayForecast> forecasts) {
}
