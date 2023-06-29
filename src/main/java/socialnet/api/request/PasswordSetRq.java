package socialnet.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "object for password reset")
public class PasswordSetRq {
    @Schema(description = "new password", example = "123qwerty")
    private String password;
}
