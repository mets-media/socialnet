package socialnet.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Like {
    private Long id;

    private String type;

    private Long entityId;

    private Timestamp time;

    private Long personId;
}
