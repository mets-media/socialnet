package socialnet.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ComplexRs {
    private Long count;

    private Integer id;

    private String message;

    @JsonProperty("message_id")
    private Long messageId;

    public ComplexRs(String message) {
        this.message = message;
        this.count = 0L;
        this.id = 0;
        this.messageId = 0L;
    }

}
