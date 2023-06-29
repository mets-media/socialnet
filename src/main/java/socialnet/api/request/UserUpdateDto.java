package socialnet.api.request;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class UserUpdateDto {
    private String about;
    private Timestamp birthDate;
    private String city;
    private String country;
    private String firstName;
    private String lastName;
    private String messagePermissions;
    private String phone;
    private String photo;

}
