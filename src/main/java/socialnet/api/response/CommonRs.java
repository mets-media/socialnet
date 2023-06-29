package socialnet.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "default response from server")
public class CommonRs<T> {
    @Schema(description = "The data we are looking for", example = "Collection of objects or just object any type")
    private T data;
    @Schema(description = "number of elements per page", example = "20")
    private Integer itemPerPage;
    @Schema(description = "page number", example = "0")
    private Integer offset;
    @Schema(description = "number of elements per page", example = "20")
    private Integer perPage;
    @Schema(description = "operation time in timestamp", example = "1670773804")
    private Long timestamp;
    @Schema(description = "total number of items found", example = "500")
    private Long total;

    public CommonRs(T data, Long timestamp) {
        this.data = data;
        this.timestamp = timestamp;
    }

    public CommonRs(T data) {

        this.data = data;
        this.itemPerPage = 0;
        this.offset = 0;
        this.perPage = 0;
        this.timestamp = System.currentTimeMillis();
        this.total = 0L;
    }

}
