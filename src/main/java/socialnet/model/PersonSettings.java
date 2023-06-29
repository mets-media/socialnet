package socialnet.model;

import lombok.Data;

@Data
public class PersonSettings {
    private Long id;
    private Boolean commentComment;
    private Boolean friendBirthday;
    private Boolean friendRequest;
    private Boolean postLike;
    private Boolean message;
    private Boolean postComment;
    private Boolean post;
}
