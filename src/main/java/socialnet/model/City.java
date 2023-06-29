package socialnet.model;

import lombok.Data;

@Data
public class City {
    private Long id;

    private String name;

    private Integer gismeteoId;

    private Long countryId;

    private String district;

    private String subDistrict;
}
