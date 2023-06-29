package socialnet.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserRq {
    private String about;

    @JsonProperty("birth_date")
    private String birthDate;

    private String city;

    private String country;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("messages_permission")
    private String messagesPermission;

    private String phone;

    @JsonProperty("photo_id")
    private String photoId;
}
