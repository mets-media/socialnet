package socialnet.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Currency {
    private Long id;

    private String name;

    private String price;

    private Timestamp updateTime;
}
