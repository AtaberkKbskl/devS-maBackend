package s.ma.project.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class VideoService {

    // Flask projesinin uploads klasörünün mutlak yolu
    private static final String FLASK_UPLOAD_DIR = "/Users/ataberkkabasakal/Desktop/BİTİRME/devSımaAı/uploads";
    private static final String FLASK_VIDEO_URL   = "http://localhost:5000/anonymize/video";
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Video dosyasını Flask'ın uploads klasörüne, tam adıyla input_video.mp4 olarak kaydeder.
     */
    public void saveVideoForFlask(MultipartFile file) throws IOException {
        Path uploadDir = Paths.get(FLASK_UPLOAD_DIR);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        Path target = uploadDir.resolve("input_video.mp4");
        // Eğer multi-part data büyük dosya uyarısı çıkıyorsa Spring ayarlarından max upload size’ı artırman gerekebilir.
        file.transferTo(target.toFile());
    }

    /**
     * Flask endpoint'ine istek atıp anonimleştirilmiş videoyu byte[] olarak döner.
     */
    public byte[] processVideo() {
        return restTemplate.getForObject(FLASK_VIDEO_URL, byte[].class);
    }
}
