// src/main/java/s/ma/project/dto/MediaFileDto.java
package s.ma.project.dto;

import java.time.LocalDateTime;

public class MediaFileDto {
    private Long id;
    private String originalFileName;
    private LocalDateTime processedTime;

    // Parametresiz constructor
    public MediaFileDto() {}

    // (Opsiyonel) Eğer isterseniz 3 parametreli constructor ekleyin:
    public MediaFileDto(Long id, String originalFileName, LocalDateTime processedTime) {
        this.id = id;
        this.originalFileName = originalFileName;
        this.processedTime = processedTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public LocalDateTime getProcessedTime() {
        return processedTime;
    }

    public void setProcessedTime(LocalDateTime processedTime) {
        this.processedTime = processedTime;
    }
}
