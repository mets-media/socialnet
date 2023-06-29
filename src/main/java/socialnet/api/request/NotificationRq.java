package socialnet.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationRq {
    private Boolean enable;
    @JsonProperty("notification_type")
    private String notificationType;
}
