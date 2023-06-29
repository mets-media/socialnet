package socialnet.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Captcha {
    private Long id;

    private String code;

    private String secretCode;

    private Timestamp time;
}
