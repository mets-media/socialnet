package socialnet.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegionStatisticsRs {

    private Integer countUsers;
    private String region;

}
