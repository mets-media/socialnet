package socialnet.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class BlockHistory {
    private Long id;

    private String action;

    private Timestamp time;

    private Long commentId;

    private Long personId;

    private Long postId;
}
