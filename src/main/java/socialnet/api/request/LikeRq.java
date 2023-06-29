package socialnet.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "data for put or delete like")
public class LikeRq {

    @JsonProperty("item_id")
    @Schema(description = "item id", example = "1")
    private Integer itemId;

    @Schema(description = "type of item: Post, Comment", example = "Post")
    private String type;
}
