package socialnet.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "info about number and which persons i liked")
public class LikeRs {

    @Schema(description = "number of my likes", example = "10")
    private Integer likes;

    @Schema(description = "user ids that I liked", example = "List [ 1, 2, 3 ]")
    private List<Integer> users;
}
