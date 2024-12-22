package com.epam.training.component;

import com.epam.training.dto.lamp.Lamp;
import com.epam.training.dto.lamp.LampLocation;
import com.epam.training.dto.lamp.LampResponse;
import com.epam.training.dto.lamp.LampState;
import com.epam.training.dto.weather.DayForecast;
import com.epam.training.dto.weather.Weather;
import com.epam.training.dto.weather.WeatherResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class HelperComponent {

    private final Map<LampLocation, Lamp> lamps = new HashMap<>();

    private final ObjectMapper objectMapper;

    public ZonedDateTime getCurrentDateTime() {
        return ZonedDateTime.of(2002, 8, 21, 0, 0, 0, 0,
                ZoneId.systemDefault());
    }

    public WeatherResponse getWeekWeatherForecast() throws JsonProcessingException {
        List<DayForecast> forecasts = List.of(
                new DayForecast(DayOfWeek.MONDAY, Weather.HOT),
                new DayForecast(DayOfWeek.TUESDAY, Weather.COLD),
                new DayForecast(DayOfWeek.WEDNESDAY, Weather.WARM),
                new DayForecast(DayOfWeek.THURSDAY, Weather.WINDY),
                new DayForecast(DayOfWeek.FRIDAY, Weather.RAINY),
                new DayForecast(DayOfWeek.SATURDAY, Weather.CLEAR),
                new DayForecast(DayOfWeek.SUNDAY, Weather.CLOUDY)
        );

        return new WeatherResponse(forecasts);
    }

    public LampResponse getLamps() {
        if (lamps.isEmpty()) {
            buildLamps();
        }

        return new LampResponse(new ArrayList<>(lamps.values()));
    }

    public void putLamp(String lampJson) throws JsonProcessingException {
        Lamp lamp = objectMapper.readValue(lampJson, Lamp.class);
        lamps.put(lamp.location(), lamp);
    }

    private void buildLamps() {
        lamps.put(LampLocation.BEDROOM, new Lamp(LampLocation.BEDROOM, LampState.ON));
        lamps.put(LampLocation.BATHROOM, new Lamp(LampLocation.BATHROOM, LampState.OFF));
        lamps.put(LampLocation.KITCHEN, new Lamp(LampLocation.KITCHEN, LampState.ON));
    }
}
