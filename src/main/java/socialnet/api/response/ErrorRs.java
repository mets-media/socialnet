package socialnet.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "common error response")
public class ErrorRs {
    @Schema(description = "name of error", example = "EmptyEmailException")
    private String error;

    @JsonProperty("error_description")
    @Schema(description = "description of error", example = "Field 'email' is empty")
    private String errorDescription;
    @Schema(description = "error time in timestamp", example = "12432857239")
    private Long timestamp;

    public ErrorRs(String error, String errorDescription) {
        this.error = error;
        this.errorDescription = errorDescription;
        this.timestamp = System.currentTimeMillis();
    }

}
