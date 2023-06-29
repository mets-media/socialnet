package socialnet.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "data for set or recover email")
public class EmailRq {
    @Schema(description = "string representation of email", example = "fullName@gmail.com")
    private String email;
    private String secret;
}
