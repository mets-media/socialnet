package socialnet.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "Cities and countries response")
public class GeolocationRs {
    @Schema(description = "The name of geolocation object", example = "Россия")
    private String title;
}
