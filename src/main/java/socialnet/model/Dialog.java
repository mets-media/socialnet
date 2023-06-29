package socialnet.model;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class Dialog {
    private Long id;

    private Long firstPersonId;

    private Long secondPersonId;

    private Timestamp lastActiveTime;

    private Long lastMessageId;
}
