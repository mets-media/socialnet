package socialnet.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "data for edit or create comment")
public class CommentRq {
    @JsonProperty("comment_text")
    @Schema(description = "text of comment", example = "some text")
    private String commentText;

    @JsonProperty("parent_id")
    @Schema(description = "parent comment id", example = "1")
    private Integer parentId;
}
