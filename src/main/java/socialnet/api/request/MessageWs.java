package socialnet.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageWs {

    private Long id;

    @JsonProperty("dialog_id")
    private Long dialogId;

    @JsonProperty("author_id")
    private Long authorId;

    @JsonProperty("recipient_id")
    private Long recipientId;

    @JsonProperty("message_text")
    private String messageText;

    @JsonProperty("read_status")
    private String readStatus;

    private Long time;

    private String token;
}
