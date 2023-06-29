package socialnet.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Post {
    private Long id;

    private Boolean isBlocked;

    private Boolean isDeleted;

    private String postText;

    private Timestamp time;

    private Timestamp timeDelete;

    private String title;

    private Long authorId;
}
