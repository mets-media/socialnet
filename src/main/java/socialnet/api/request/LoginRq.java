package socialnet.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "data for login")
public class LoginRq {
    @Schema(description = "email of registered user", example = "fullname@gmail.com")
    private String email;
    @Schema(description = "password of registered user", example = "123qwerty")
    private String password;
}
