package socialnet.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "data to update the post")
public class PostRq {

    @NotBlank(message = "Post text can not be empty")
    @JsonProperty("post_text")
    @Schema(description = "new post text", example = "some new text")
    private String postText;

    @Schema(description = "new post tags", example = "List [ winter, boring ]")
    private List<String> tags = new ArrayList<>();

    @NotBlank(message = "Post title can not be empty")
    @Schema(description = "new post title", example = "NEW TITLE")
    private String title;
}
