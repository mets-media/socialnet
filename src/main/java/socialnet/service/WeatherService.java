package socialnet.service;

import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import socialnet.api.response.WeatherRs;
import socialnet.mappers.WeatherMapper;
import socialnet.model.Weather;
import socialnet.repository.CityRepository;
import socialnet.repository.WeatherRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static java.time.LocalDateTime.*;

@Service
@RequiredArgsConstructor
public class WeatherService {
    @Value("${weather.apiKey}")
    private String apiKey;

    private final CityRepository cityRepository;
    private final WeatherMapper weatherMapper;
    private final WeatherRepository weatherRepository;

    public WeatherRs getWeatherByCity(String city) {

        if (city == null) return new WeatherRs();

        if (Boolean.FALSE.equals(cityRepository.containsCity(city))) return new WeatherRs();

        var weatherFromDb = weatherRepository.getWeatherByCity(city);

        //Чтение информации о погоде из базы данных
        if (weatherFromDb != null) {
            int compare = now().compareTo(weatherFromDb
                    .getDate()
                    .toLocalDateTime()
                    .plus(1L, ChronoUnit.HOURS));
            if (compare < 0)
                return weatherMapper.toResponse(weatherFromDb);
        }

        try {
            return loadWeather(city);
        } catch (Exception e) {
            if (weatherFromDb != null)
                return weatherMapper.toResponse(weatherFromDb);
            else
                return new WeatherRs();
        }
    }

    private WeatherRs loadWeather(String city) {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("api.openweathermap.org/data/2.5/find")
                .path("/")
                .query("q={City}&type={Type}&APPID={ApiKey}")
                .buildAndExpand(city, "like", apiKey);


        WebClient webClient = WebClient.builder().build();
        String weatherJson = webClient.get()
                .uri(uriComponents.toUriString())
                .retrieve().bodyToMono(String.class).block();

        JSONObject jsonObject = new JSONObject(weatherJson);

        JSONArray list = jsonObject.getJSONArray("list");

        if (list.length() == 0) return new WeatherRs();

        return getWeatherRs((JSONObject) list.get(0));
    }

    private WeatherRs getWeatherRs(JSONObject jsonObject) {

        JSONObject weather = (JSONObject) jsonObject.getJSONArray("weather").get(0);

        var openWeatherId = weather.getBigInteger("id");
        //запись в базу данных информации о погоде в конкретном городе

        JSONObject main = jsonObject.getJSONObject("main");

        String currentTemp;
        try {
            currentTemp = Integer.toString(Math.round(Float.parseFloat(main.get("temp").toString()) - 273.15F));
        } catch (Exception ex) {
            currentTemp = "?";
        }

        WeatherRs weatherRs = new WeatherRs(
                jsonObject.getString("name"),
                weather.getString("description"),
                LocalDate.now().toString(),
                currentTemp);

        Weather wModel = weatherMapper.toModel(weatherRs);
        wModel.setOpenWeatherId(openWeatherId);
        weatherRepository.saveWeather(wModel);

        return weatherRs;
    }


}
