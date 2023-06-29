package socialnet.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "default response from server")
public class CommentRs {
    @Schema(description = "default user representation")
    private PersonRs author;

    @JsonProperty("comment_text")
    @Schema(description = "text od comment", example = "Some text")
    private String commentText;

    @Schema(description = "comment id", example = "1")
    private Long id;

    @JsonProperty("is_blocked")
    @Schema(description = "comment is blocked by current user", example = "false")
    private Boolean isBlocked;

    @JsonProperty("is_deleted")
    @Schema(description = "comment is deleted by current user", example = "false")
    private Boolean isDeleted;

    @Schema(description = "number of likes", example = "35")
    private Integer likes;

    @JsonProperty("my_like")
    @Schema(description = "whether the current user liked it", example = "false")
    private Boolean myLike;

    @JsonProperty("parent_id")
    @Schema(description = "id of parent comment", example = "1")
    private Long parentId;

    @JsonProperty("post_id")
    @Schema(description = "post id for this comment", example = "2")
    private Long postId;

    @JsonProperty("sub_comments")
    @Schema(description = "list of embedded comments")
    private List<CommentRs> subComments;

    @Schema(description = "comment creation time", example = "2022-02-24 06:14:36.000000")
    private String time;
}
