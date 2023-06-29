package socialnet.model;

import lombok.Data;

@Data
public class Country {
    private Long id;

    private String name;

    private String fullName;

    private String internationalName;

    private String code2;
}
