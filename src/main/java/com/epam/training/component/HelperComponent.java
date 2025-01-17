package com.epam.training.component;

import com.epam.training.dto.lamp.Lamp;
import com.epam.training.dto.lamp.LampLocation;
import com.epam.training.dto.lamp.LampResponse;
import com.epam.training.dto.lamp.LampState;
import com.epam.training.dto.weather.DayForecast;
import com.epam.training.dto.weather.Weather;
import com.epam.training.dto.weather.WeatherResponse;
import com.epam.training.exception.PriceListLoadingException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class HelperComponent {

    private static final String DEFAULT_PRICE_LIST_PATH = "data/laptops.csv";

    private static final String DEFAULT_PRICE_LIST_LOAD_ERROR = "Unable to load price list";

    private static final String DEFAULT_NULL_VALUE = "Unspecified";

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

    public List<String> loadPriceList(MultipartFile uploadedFile) {
        try {
            InputStream is = getPriceListInputStream(uploadedFile);
            CsvSchema csvSchema = CsvSchema.emptySchema()
                    .withHeader()
                    .withNullValue(DEFAULT_NULL_VALUE);
            CsvMapper csvMapper = new CsvMapper();
            MappingIterator<Map<String, String>> mappingIterator = csvMapper.reader()
                    .forType(Map.class)
                    .with(csvSchema)
                    .readValues(is);

            List<String> priceList = mappingIterator.readAll().stream()
                    .map(Object::toString)
                    .toList();
            log.info("Price list loaded. Count: {}", priceList.size());

            return priceList;
        } catch (IOException ex) {
            log.error(DEFAULT_PRICE_LIST_LOAD_ERROR, ex);;
            throw new PriceListLoadingException(DEFAULT_PRICE_LIST_LOAD_ERROR, ex);
        }
    }

    private static InputStream getPriceListInputStream(MultipartFile uploadedFile) throws IOException {
        if (Objects.isNull(uploadedFile) || uploadedFile.isEmpty()) {
            log.info("No file uploaded or it's empty. Loading default price list");
            return new ClassPathResource(DEFAULT_PRICE_LIST_PATH).getInputStream();
        }

        log.info("Trying to load price list from upload file: {}", uploadedFile.getName());
        return uploadedFile.getInputStream();
    }
}
