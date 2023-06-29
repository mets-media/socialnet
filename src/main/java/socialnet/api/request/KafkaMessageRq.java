package socialnet.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KafkaMessageRq {
    private long toTgId;
    private String from;
    private String type;
}
