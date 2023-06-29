package socialnet.model;

import lombok.Data;

@Data
public class PostFile {
    private Long id;

    private String name;

    private String path;

    private Long postId;
}
