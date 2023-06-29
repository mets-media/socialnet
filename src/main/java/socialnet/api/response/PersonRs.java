package socialnet.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.sql.Timestamp;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "default user representation")
public class PersonRs {
    @Schema(description = "info about user", example = "I'm from Paris")
    private String about;

    @JsonProperty("birth_date")
    @Schema(description = "user's birthday", example = "2022-02-24 06:14:36.000000")
    private Timestamp birthDate;

    @Schema(description = "city of user", example = "Paris")
    private String city;

    @Schema(description = "country of user", example = "France")
    private String country;

    private CurrencyRs currency;

    @Schema(description = "user email", example = "fullName@gmail.com")
    private String email;

    @JsonProperty("first_name")
    @Schema(description = "first name of user", example = "Максим")
    private String firstName;

    @JsonProperty("friend_status")
    @Schema(description = "relationship of user to current user " +
            "Enum:" +
            "[ BLOCKED, FRIEND, RECEIVED_REQUEST, REQUEST, UNKNOWN ]", example = "FRIEND")
    private String friendStatus;

    @Schema(description = "user id", example = "1")
    private Long id;

    @JsonProperty("is_blocked")
    @Schema(description = "whether the user is globally locked out", example = "false")
    private Boolean isBlocked;

    @JsonProperty("is_blocked_by_current_user")
    @Schema(description = "whether the user is locked out for the current user", example = "false")
    private Boolean isBlockedByCurrentUser;

    @JsonProperty("last_name")
    @Schema(description = "last name of user", example = "Иванов")
    private String lastName;

    @JsonProperty("last_online_time")
    @Schema(description = "the last time the user was online", example = "2022-02-24 06:14:36.000000")
    private Timestamp lastOnlineTime;

    @JsonProperty("messages_permission")
    @Schema(description = "who can write Enum:[ ALL, FRIENDS ]", example = "ALL")
    private String messagesPermission;

    @Schema(description = "user is online?", example = "true")
    private Boolean online;

    @Schema(description = "user phone", example = "+7 (982) 281-15-23")
    private String phone;

    @Schema(description = "url of user photo", example = "/some/path")
    private String photo;

    @JsonProperty("reg_date")
    @Schema(description = "user's registration date", example = "2022-02-24 06:14:36.000000")
    private Timestamp regDate;

    @Schema(description = "delete this field", example = "DELETE THIS FIELD")
    private String token;

    @JsonProperty("user_deleted")
    @Schema(description = "is the user deleted", example = "false")
    private Boolean userDeleted;

    private WeatherRs weather;

}
