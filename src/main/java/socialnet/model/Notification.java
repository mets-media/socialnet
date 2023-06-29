package socialnet.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Notification {

    private Long id;
    private String contact;
    private String notificationType;
    private Long entityId;
    private Boolean isRead;
    private Timestamp sentTime;
    private Long personId;
}
