package s.ma.project.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "media_file")
public class MediaFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Yükleyen kullanıcının username’i
    private String ownerUsername;

    // Kullanıcının yüklediği orijinal dosya adı (örn. “foto.png”)
    private String originalFileName;

    // Backend tarafında diske yazdığımız, UUID veya timestamp ile benzersizleştirilmiş ad
    private String storedFileName;

    // Dosyanın fiziksel yolu (örn. "/uploads/images/UUID-abc123.png")
    private String storedPath;

    // “IMAGE” veya “VIDEO” (isteğe bağlı olarak “FRAME” vb. de ekleyebilirsiniz)
    private String mediaType;

    // “PENDING”, “PROCESSING”, “DONE”, “FAILED”
    private String status;

    private LocalDateTime uploadTime;
    private LocalDateTime processedTime;

    // Eğer çıktı yolu ayrıysa
    private String outputPath;

    public MediaFile() { }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getStoredFileName() {
        return storedFileName;
    }

    public void setStoredFileName(String storedFileName) {
        this.storedFileName = storedFileName;
    }

    public String getStoredPath() {
        return storedPath;
    }

    public void setStoredPath(String storedPath) {
        this.storedPath = storedPath;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(LocalDateTime uploadTime) {
        this.uploadTime = uploadTime;
    }

    public LocalDateTime getProcessedTime() {
        return processedTime;
    }

    public void setProcessedTime(LocalDateTime processedTime) {
        this.processedTime = processedTime;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }
}
