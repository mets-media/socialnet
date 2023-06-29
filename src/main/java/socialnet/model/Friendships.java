package socialnet.model;

import lombok.Data;
import socialnet.model.enums.FriendshipStatusTypes;

import java.sql.Timestamp;

@Data
public class Friendships {
    private Long id;

    private Timestamp sentTime;

    private Long dstPersonId;

    private Long srcPersonId;

    private FriendshipStatusTypes statusName;
}
