package socialnet.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageCommonWs {
    @JsonProperty("dialog_id")
    private Long dialogId;

    @JsonProperty("message_ids")
    private List<Long> messageIds;

    @JsonProperty("message_id")
    private Long messageId;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("message_text")
    private String messageText;

    private String token;
}
