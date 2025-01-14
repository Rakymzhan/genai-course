package com.epam.training.controllers;

import com.epam.training.dto.UserRequest;
import com.epam.training.dto.UserResponse;
import com.epam.training.dto.weather.WeatherResponse;
import com.epam.training.service.PluginCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/plugin/check")
public class PluginCheckController {

    private final PluginCheckService pluginCheckService;

    @PostMapping(value = "/time", consumes = "application/json", produces = "application/json")
    public UserResponse currentDateTime(@RequestBody UserRequest request) {
        return pluginCheckService.getCurrentDateTime(request);
    }

    @PostMapping(value = "/lamp", consumes = "application/json", produces = "application/json")
    public UserResponse checkLamp(@RequestBody UserRequest request) {
        return pluginCheckService.checkLamp(request);
    }

    @PostMapping(value = "/weather", consumes = "application/json", produces = "application/json")
    public WeatherResponse getWeather(@RequestBody UserRequest request) {
        return pluginCheckService.getWeather(request);
    }
}
