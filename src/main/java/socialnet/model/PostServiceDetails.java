package socialnet.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import socialnet.api.response.CommentRs;

import java.util.List;

@Data
@AllArgsConstructor
public class PostServiceDetails {
    private Person author;
    private List<Like> likes;
    private List<String> tags;
    private Long authUserId;
    private List<CommentRs> comments;
}
