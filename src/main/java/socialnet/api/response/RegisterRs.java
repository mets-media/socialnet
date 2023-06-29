package socialnet.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "response after set, reset, recovery password or set, recovery email")
public class RegisterRs {
    private ComplexRs data;
    @Schema(description = "mail of the user for whom the operations are performed", example = "fullName@gamil.com")
    private String email;
    @Schema(description = "operation time in timestamp", example = "1670773804")
    private Long timestamp;

    public RegisterRs(String email, Long timestamp) {
        this.data = new ComplexRs();
        this.email = email;
        this.timestamp = timestamp;
    }

}
