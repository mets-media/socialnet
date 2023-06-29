package socialnet.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import socialnet.api.response.WeatherRs;
import socialnet.model.Weather;

import java.sql.Timestamp;

@Mapper(componentModel = "spring")
public interface WeatherMapper {

    @Mapping(expression = "java(currentTime())", target = "date")
    Weather toModel(WeatherRs weatherRs);

    @Mapping(target = "date", dateFormat = "yyyy-MM-dd'T'HH:mm:ss")
    WeatherRs toResponse(Weather weather);

    default Timestamp currentTime() {
        return new Timestamp(System.currentTimeMillis());
    }

}
