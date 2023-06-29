package socialnet.model;

import lombok.Data;

@Data
public class Post2Tag {
    private Long id;

    private Long postId;

    private Long tagId;

    public Post2Tag() {

    }

    public Post2Tag(Long postId, Long tagId) {
        this.postId = postId;
        this.tagId = tagId;
    }
}
