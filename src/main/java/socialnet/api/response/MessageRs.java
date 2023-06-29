package socialnet.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "info about message")
public class MessageRs {

    @JsonProperty("author_id")
    @Schema(description = "message author id", example = "1")
    private Long authorId;

    @Schema(description = "message id", example = "1")
    private Long id;

    @Schema(description = "is my message", example = "true")
    private Boolean isSentByMe;

    @JsonProperty("message_text")
    @Schema(description = "message text", example = "some text")
    private String messageText;

    @JsonProperty("read_status")
    @Schema(description = "read or unread message", example = "read")
    private String readStatus;

    private PersonRs recipient;

    @JsonProperty("recipient_id")
    @Schema(description = "message recipient id", example = "2")
    private Long recipientId;

    @Schema(description = "message time", example = "2022-02-24 06:14:36.000000")
    private String time;
}
