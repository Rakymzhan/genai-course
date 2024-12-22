package com.epam.training.service;

import com.epam.training.dto.UserRequest;
import com.epam.training.dto.UserResponse;
import com.epam.training.dto.weather.WeatherResponse;

public interface PluginCheckService {

    UserResponse getCurrentDateTime(UserRequest request);

    UserResponse checkLamp(UserRequest request);

    WeatherResponse getWeather(UserRequest request);
}
