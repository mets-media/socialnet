package socialnet.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class PostComment {
    private Long id;
    private String commentText;
    private Boolean isBlocked;
    private Boolean isDeleted;
    private Timestamp time;
    private Long parentId;
    private Long authorId;
    private Long postId;
}
