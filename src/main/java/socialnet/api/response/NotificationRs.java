package socialnet.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "info about notifications")
public class NotificationRs {

    @JsonProperty("entity_author")
    private PersonRs entityAuthor;

    @Schema(description = "id of notification", example = "1")
    private Integer id;

    @Schema(description = "info about notification", example = "some info")
    private String info;

    @JsonProperty("notification_type")
    @Schema(description = "notification type\n" +
            "Enum:\n" +
            "[ COMMENT_COMMENT, FRIEND_BIRTHDAY, FRIEND_REQUEST, MESSAGE, POST, POST_COMMENT, POST_LIKE ]",
            example = "POST")
    private String notificationType;

    @JsonProperty("sent_time")
    @Schema(description = "when notification sent", example = "2022-02-24 06:14:36.000000")
    private Date sentTime;
}
