package s.ma.project.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * /api/result/images veya /api/result/videos isteği sonucunda dönecek
 * her bir medya kaydının minimal gösterim verileri.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MediaFileDto {
    private Long id;
    private String originalFileName;
    private LocalDateTime processedTime;
    // Eğer isterseniz, outputPath alanını da ekleyebilirsiniz
    // private String outputPath;
}