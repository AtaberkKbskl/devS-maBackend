package s.ma.project.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import s.ma.project.model.MediaFile;
import s.ma.project.repository.MediaFileRepository;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ImageService {

    private final MediaFileRepository mediaFileRepository;

    // Uygulama root dizininde uploads klasörü (application.properties içinde de tanımlayabilirsiniz)
    @Value("${app.upload.dir}")
    private String uploadRoot;

    public ImageService(MediaFileRepository mediaFileRepository) {
        this.mediaFileRepository = mediaFileRepository;
    }

    /**
     * 1) Dosyayı benzersiz (UUID) bir adla kaydeder, DB'ye PENDING statüyle MediaFile kaydeder.
     *    2) İşleme başlatma / process aşamasında kullanılacak MediaFile.id değerini döndürür.
     */
    public MediaFile saveImage(MultipartFile file, String ownerUsername) throws IOException {
        // 1. UUID üret
        String uuid = UUID.randomUUID().toString();
        // 2. Dosya uzantısını al
        String ext = Optional.ofNullable(file.getOriginalFilename())
                             .filter(n -> n.contains("."))
                             .map(n -> n.substring(n.lastIndexOf(".")))
                             .orElse("");
        // 3. Benzersiz dosya adı = UUID + ext
        String storedFileName = uuid + ext;

        // 4. Kayıt klasörünü oluştur (örn. {uploadRoot}/images/)
        Path uploadDir = Paths.get(uploadRoot, "images");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // 5. Dosyayı fiziksel olarak kaydet
        Path target = uploadDir.resolve(storedFileName);
        file.transferTo(target.toFile());

        // 6. MediaFile entity’sini oluşturup DB’ye kaydet
        MediaFile mediaFile = new MediaFile();
        mediaFile.setOwnerUsername(ownerUsername);
        mediaFile.setOriginalFileName(file.getOriginalFilename());
        mediaFile.setStoredFileName(storedFileName);
        mediaFile.setStoredPath(target.toString());
        mediaFile.setMediaType("IMAGE");
        mediaFile.setStatus("PENDING");
        mediaFile.setUploadTime(LocalDateTime.now());
        mediaFile = mediaFileRepository.save(mediaFile);

        return mediaFile;
    }

    /**
     * 7) İşleme başlatmak için çağırılacak metot.
     *    - MediaFile.status = “PROCESSING” olarak güncellenir.
     *    - Flask servisine “multipart/form-data” olarak dosya gönderilir.
     *    - İşlem tamamlandığında MediaFile.status = “DONE”, processedTime ve outputPath alanları güncellenir.
     */
    public void startImageProcessing(Long mediaId) {
        MediaFile mediaFile = mediaFileRepository.findById(mediaId)
                .orElseThrow(() -> new IllegalArgumentException("MediaFile not found: " + mediaId));

        // 1. Status’u “PROCESSING” olarak güncelle
        mediaFile.setStatus("PROCESSING");
        mediaFileRepository.save(mediaFile);

        // 2. Flask endpoint URL
        String flaskUrl = "http://localhost:5000/anonymize/image";

        // 3. Dosyayı Flask’a multipart olarak gönder (RestTemplate kullanarak)
        //    NOT: Burada exception handling ekleyin (RestClientException, timeout vb. durumlar için)
        Path imagePath = Paths.get(mediaFile.getStoredPath());
        try {
            // RestTemplate ile multipart isteği oluşturma
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA);

            org.springframework.core.io.Resource resource = 
                new org.springframework.core.io.FileSystemResource(imagePath.toFile());

            org.springframework.util.LinkedMultiValueMap<String, Object> body = 
                new org.springframework.util.LinkedMultiValueMap<>();
            body.add("file", resource);

            org.springframework.http.HttpEntity<org.springframework.util.LinkedMultiValueMap<String, Object>> requestEntity =
                new org.springframework.http.HttpEntity<>(body, headers);

            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
            org.springframework.http.ResponseEntity<byte[]> response = 
                restTemplate.postForEntity(flaskUrl, requestEntity, byte[].class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // 4. Flask’tan gelen byte[] kullanılarak çıktı dosyasını kaydet
                byte[] imageBytes = response.getBody();
                // Çıktı dizinini oluştur (örn. {uploadRoot}/images/output-UUID.png)
                Path outputDir = Paths.get(uploadRoot, "images", "output");
                if (!Files.exists(outputDir)) {
                    Files.createDirectories(outputDir);
                }
                String outputFileName = "output-" + mediaFile.getStoredFileName();
                Path outputPath = outputDir.resolve(outputFileName);
                Files.write(outputPath, imageBytes, StandardOpenOption.CREATE);

                // 5. MediaFile kaydını güncelle
                mediaFile.setStatus("DONE");
                mediaFile.setProcessedTime(LocalDateTime.now());
                mediaFile.setOutputPath(outputPath.toString());
                mediaFileRepository.save(mediaFile);
            } else {
                mediaFile.setStatus("FAILED");
                mediaFileRepository.save(mediaFile);
            }
        } catch (Exception e) {
            // Hata durumunda status = “FAILED” ve log kaydı
            mediaFile.setStatus("FAILED");
            mediaFileRepository.save(mediaFile);
            // Hata loglama (ör. Logger.error(...))
        }
    }

    /**
     * 6) İşlem tamamlandıktan sonra frontend’in çağıracağı metot:
     *    - Eğer status != “DONE” ise hata fırlatır
     *    - outputPath’teki dosyayı byte[] olarak döner
     */
    public byte[] getImageResult(Long mediaId) throws IOException {
        MediaFile mediaFile = mediaFileRepository.findById(mediaId)
                .orElseThrow(() -> new IllegalArgumentException("MediaFile not found: " + mediaId));

        if (!"DONE".equals(mediaFile.getStatus())) {
            throw new IllegalStateException("Image is not processed yet");
        }

        Path outputPath = Paths.get(mediaFile.getOutputPath());
        return Files.readAllBytes(outputPath);
    }
}
