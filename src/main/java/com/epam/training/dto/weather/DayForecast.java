package com.epam.training.dto.weather;

import java.time.DayOfWeek;

public record DayForecast(DayOfWeek date, Weather weather) {
}
