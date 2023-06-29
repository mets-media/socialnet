package socialnet.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class Storage {
    private Long id;
    private Long ownerId;
    private String fileName;
    private Long fileSize;
    private String fileType;
    private LocalDateTime createdAt;

    public Storage(Long ownerId, String fileName, Long fileSize, String fileType, LocalDateTime createdAt) {
        this.ownerId = ownerId;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.createdAt = createdAt;
    }
}
