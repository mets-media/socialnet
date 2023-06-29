package socialnet.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PersonSettingsRq {
    @Schema(description = "Turning on or turning off the notification", example = "true")
    private Boolean enable;

    @JsonProperty("notification_type")
    @Schema(description = "Type of notification object", example = "POST_COMMENT")
    private String notificationType;
}