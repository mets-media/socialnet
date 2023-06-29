package socialnet.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "data for user registration")
public class RegisterRq {
    @NotBlank(message = "Field 'code' is empty")
    @Schema(description = "entered captcha", example = "zhcyuo")
    private String code;

    @NotBlank(message = "Field 'code secret' is empty")
    @Schema(description = "captcha decryption secret", example = "uuid")
    private String codeSecret;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Field 'email' is empty")
    @Schema(description = "user registration email", example = "fullName@gamil.com")
    private String email;

    @NotBlank(message = "Field 'first name' is empty")
    @Schema(description = "first name of new user", example = "Максим", pattern = "[А-Яа-яA-Za-z]")
    private String firstName;

    @NotBlank(message = "Field 'last name' is empty")
    @Schema(description = "last name of new user", example = "Иванов", pattern = "[А-Яа-яA-Za-z]")
    private String lastName;

    @NotBlank(message = "Field 'password' is empty")
    @Schema(description = "first password to compare", example = "123qwerty")
    private String passwd1;

    @NotBlank(message = "Field 'password confirm' is empty")
    @Schema(description = "second password to compare", example = "123qwerty")
    private String passwd2;
}
