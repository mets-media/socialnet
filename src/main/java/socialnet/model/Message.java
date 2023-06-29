package socialnet.model;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class Message {
    private Long id;

    private Boolean isDeleted;

    private String messageText;

    private String readStatus;

    private Timestamp time;

    private Long dialogId;

    private Long authorId;

    private Long recipientId;
}
