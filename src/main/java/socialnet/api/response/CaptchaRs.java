package socialnet.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "captcha response")
public class CaptchaRs {
    @Schema(description = "secret code for decrypt captcha", example = "uuid")
    private String code;
    @Schema(description = "url of captcha image", example = "/some/path")
    private String image;
}
