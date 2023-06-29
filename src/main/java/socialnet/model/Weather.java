package socialnet.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Weather {
    private BigInteger openWeatherId;
    private String city;
    private String clouds;
    private Float temp;
    private Timestamp date;
}
