package socialnet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import socialnet.model.Weather;
import socialnet.utils.Reflection;

@Repository
@RequiredArgsConstructor
public class WeatherRepository {
    private final JdbcTemplate jdbcTemplate;
    private final Reflection reflection;

    public void saveWeather(Weather weather) {
        jdbcTemplate.update("Delete from Weather where city = ?", weather.getCity());

        var fields = reflection.getFieldNames(weather);
        var values = reflection.getStringValues(weather);
        var updateOnConflict = reflection.getUpdateSql(weather, "openWeatherId");
        var sql = "Insert into Weather (" + fields + ") values (" + values + ") " +
                " on conflict on constraint unique_open_weather_id do update set "  +
                updateOnConflict;
        jdbcTemplate.update(sql);
    }

    public Weather getWeatherByCity(String city) {
        return jdbcTemplate.query("select open_weather_id, city, clouds, date, temp from weather where city = ?",
                new Object[] {city},
                new BeanPropertyRowMapper<>(Weather.class)).stream().findAny().orElse(null);
    }
}
