package socialnet.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Comment {
    private Long id;

    private String commentText;

    private Boolean isBlocked;

    private Boolean isDeleted;

    private Timestamp time;

    private Long parentId;

    private Long authorId;

    private Long postId;
}
