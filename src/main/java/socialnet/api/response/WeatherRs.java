package socialnet.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "weather in city")
public class WeatherRs {
    @Schema(description = "Current city name", example = "Paris")
    private String city;

    @Schema(description = "Current city's weather description", example = "clouds")
    private String clouds;

    @Schema(description = "Weather last update time", example = "2022-12-10T12:00:00")
    private String date;

    @Schema(description = "Temperature in current city", example = "9")
    private String temp;
}
