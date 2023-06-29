package socialnet.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "info about post")
public class PostRs {

    private PersonRs author;

    private List<CommentRs> comments;

    @Schema(description = "post id", example = "1")
    private Long id;

    @JsonProperty("is_blocked")
    @Schema(description = "is blocked by current user", example = "false")
    private Boolean isBlocked;

    @Schema(description = "number of likes", example = "10")
    private Integer likes;

    @JsonProperty("my_like")
    @Schema(description = "did i like the post", example = "true")
    private Boolean myLike;

    @JsonProperty("post_text")
    @Schema(description = "post text", example = "some text")
    private String postText;

    @Schema(description = "post tags", example = "List [ funny, summer ]")
    private List<String> tags;

    @Schema(description = "when the post was created", example = "2022-02-24 06:14:36.000000")
    private String time;

    @Schema(description = "post title", example = "TITLE")
    private String title;

    @Schema(description = "post type Enum: [ DELETED, POSTED, QUEUED ]", example = "POSTED")
    private String type;
}
