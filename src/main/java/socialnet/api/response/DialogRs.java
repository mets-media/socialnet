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
@Schema(description = "data about dialog")
public class DialogRs {

    @JsonProperty("author_id")
    @Schema(description = "message author id", example = "1")
    private Long authorId;

    @Schema(description = "id of dialog", example = "1")
    private Long id;

    @JsonProperty("last_message")
    private MessageRs lastMessage;

    @JsonProperty("read_status")
    @Schema(description = "read or unread message", example = "read")
    private String readStatus;

    @JsonProperty("recipient_id")
    @Schema(description = "message recipient id", example = "2")
    private Long recipientId;

    @JsonProperty("unread_count")
    @Schema(description = "count of unread message", example = "15")
    private Long unreadCount;
}
