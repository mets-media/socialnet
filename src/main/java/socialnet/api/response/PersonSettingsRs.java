package socialnet.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonSettingsRs {

    @Schema(description = "Turning on or turning off the notification", example = "true")
    private Boolean enable;

    @Schema(description = "Type of object for notification", example = "POST_COMMENT")
    private String type;
}
