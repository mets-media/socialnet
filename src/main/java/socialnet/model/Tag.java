package socialnet.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tag {
    private Long id;
    private String tag;

    public Tag(String tag) {
        this.tag = tag.replace("#", "");
    }

    public void setTag(String tag) {
        this.tag = tag.replace("#", "");
    }
}
