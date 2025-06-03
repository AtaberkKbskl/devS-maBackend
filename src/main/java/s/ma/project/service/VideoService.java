package s.ma.project.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import s.ma.project.model.MediaFile;
import s.ma.project.repository.MediaFileRepository;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class VideoService {

    private final MediaFileRepository mediaFileRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${app.upload.dir}")
    private String uploadRoot;

    public VideoService(MediaFileRepository mediaFileRepository) {
        this.mediaFileRepository = mediaFileRepository;
    }

    /**
     * 1) Video dosyasını UUID tabanlı kaydet, DB kaydı oluştur (status=PENDING), dönen medyaId’yi return et.
     */
    public MediaFile saveVideoForFlask(MultipartFile file, String ownerUsername) throws IOException {
        String uuid = UUID.randomUUID().toString();
        String ext = Optional.ofNullable(file.getOriginalFilename())
                             .filter(n -> n.contains("."))
                             .map(n -> n.substring(n.lastIndexOf(".")))
                             .orElse(".mp4");

        String storedFileName = uuid + ext;
        Path uploadDir = Paths.get(uploadRoot, "videos");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        Path target = uploadDir.resolve(storedFileName);
        file.transferTo(target.toFile());

        MediaFile mediaFile = new MediaFile();
        mediaFile.setOwnerUsername(ownerUsername);
        mediaFile.setOriginalFileName(file.getOriginalFilename());
        mediaFile.setStoredFileName(storedFileName);
        mediaFile.setStoredPath(target.toString());
        mediaFile.setMediaType("VIDEO");
        mediaFile.setStatus("PENDING");
        mediaFile.setUploadTime(LocalDateTime.now());
        mediaFile = mediaFileRepository.save(mediaFile);
        return mediaFile;
    }

    /**
     * 2) İşleme başlatma: MediaFile.status = “PROCESSING”, Flask’a dosyayı multipart olarak gönder,
     *    çıktı byte[] geldiyse diske yaz, status=“DONE”, processedTime ve outputPath güncellenir.
     */
    public void startVideoProcessing(Long mediaId) {
        MediaFile mediaFile = mediaFileRepository.findById(mediaId)
                .orElseThrow(() -> new IllegalArgumentException("MediaFile not found: " + mediaId));

        mediaFile.setStatus("PROCESSING");
        mediaFileRepository.save(mediaFile);

        String flaskUrl = "http://localhost:5000/anonymize/video";
        Path videoPath = Paths.get(mediaFile.getStoredPath());

        try {
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA);

            org.springframework.core.io.Resource resource = new org.springframework.core.io.FileSystemResource(videoPath.toFile());
            org.springframework.util.LinkedMultiValueMap<String, Object> body = new org.springframework.util.LinkedMultiValueMap<>();
            body.add("file", resource);

            org.springframework.http.HttpEntity<org.springframework.util.LinkedMultiValueMap<String, Object>> requestEntity =
                    new org.springframework.http.HttpEntity<>(body, headers);

            org.springframework.http.ResponseEntity<byte[]> response =
                    restTemplate.postForEntity(flaskUrl, requestEntity, byte[].class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                byte[] videoBytes = response.getBody();
                Path outputDir = Paths.get(uploadRoot, "videos", "output");
                if (!Files.exists(outputDir)) {
                    Files.createDirectories(outputDir);
                }
                String outputFileName = "output-" + mediaFile.getStoredFileName();
                Path outputPath = outputDir.resolve(outputFileName);
                Files.write(outputPath, videoBytes, StandardOpenOption.CREATE);

                mediaFile.setStatus("DONE");
                mediaFile.setProcessedTime(LocalDateTime.now());
                mediaFile.setOutputPath(outputPath.toString());
                mediaFileRepository.save(mediaFile);
            } else {
                mediaFile.setStatus("FAILED");
                mediaFileRepository.save(mediaFile);
            }
        } catch (Exception e) {
            mediaFile.setStatus("FAILED");
            mediaFileRepository.save(mediaFile);
            // Loglama
        }
    }

    /**
     * 3) İşlem tamamlandıktan sonra sonucu çekmek için metot:
     *    - Eğer status != “DONE” ise istisna veya null döndür
     *    - outputPath’teki MP4 dosyasını byte[] olarak döner
     */
    public byte[] getVideoResult(Long mediaId) throws IOException {
        MediaFile mediaFile = mediaFileRepository.findById(mediaId)
                .orElseThrow(() -> new IllegalArgumentException("MediaFile not found: " + mediaId));

        if (!"DONE".equals(mediaFile.getStatus())) {
            throw new IllegalStateException("Video is not processed yet");
        }

        Path outputPath = Paths.get(mediaFile.getOutputPath());
        return Files.readAllBytes(outputPath);
    }
}
