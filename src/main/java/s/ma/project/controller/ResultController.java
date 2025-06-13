// src/main/java/s/ma/project/controller/ResultController.java
package s.ma.project.controller;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import s.ma.project.dto.MediaFileDto;
import s.ma.project.model.MediaFile;
import s.ma.project.repository.MediaFileRepository;
import s.ma.project.service.ImageService;
import s.ma.project.service.VideoService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/result")
@CrossOrigin(origins = "*")
public class ResultController {

    private final ImageService imageService;
    private final VideoService videoService;
    private final MediaFileRepository mediaFileRepository;

    // Lombok olmadan elle constructor
    public ResultController(ImageService imageService,
                            VideoService videoService,
                            MediaFileRepository mediaFileRepository) {
        this.imageService = imageService;
        this.videoService = videoService;
        this.mediaFileRepository = mediaFileRepository;
    }

    /**
     * 1) İşlenmiş FOTOĞRAFLARIN listesi
     *    GET /api/result/images
     */
    @GetMapping("/images")
    public ResponseEntity<List<MediaFileDto>> listProcessedImages() {
        // "mediaType" ve "status" alanlarına göre sorgulama
        List<MediaFile> images = mediaFileRepository.findByMediaTypeAndStatus("IMAGE", "DONE");

        // DTO’ya dönüştür (MediaFileDto’da parametresiz constructor + setter’lar olduğu varsayılıyor)
        List<MediaFileDto> dtos = images.stream()
            .map(img -> {
                MediaFileDto dto = new MediaFileDto();
                dto.setId(img.getId());
                dto.setOriginalFileName(img.getOriginalFileName());
                dto.setProcessedTime(img.getProcessedTime());
                return dto;
            })
            .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    /**
     * 2) İşlenmiş VİDEOLARI listesi
     *    GET /api/result/videos
     */
    @GetMapping("/videos")
    public ResponseEntity<List<MediaFileDto>> listProcessedVideos() {
        List<MediaFile> videos = mediaFileRepository.findByMediaTypeAndStatus("VIDEO", "DONE");

        List<MediaFileDto> dtos = videos.stream()
            .map(vd -> {
                MediaFileDto dto = new MediaFileDto();
                dto.setId(vd.getId());
                dto.setOriginalFileName(vd.getOriginalFileName());
                dto.setProcessedTime(vd.getProcessedTime());
                return dto;
            })
            .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    /**
     * 3) Tek bir işlenmiş FOTOĞRAFI getirme
     *    GET /api/result/image/{id}
     *    -> İşlenmiş dosyayı byte[] olarak döner. (Content-Type: image/jpeg)
     */
    @GetMapping("/image/{id}")
    public ResponseEntity<Resource> getProcessedImage(@PathVariable Long id) {
        try {
            byte[] imageBytes = imageService.getImageResult(id);
            if (imageBytes == null || imageBytes.length == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            headers.setContentDisposition(
                ContentDisposition.inline().filename("result-" + id + ".jpg").build()
            );

            Resource resource = new ByteArrayResource(imageBytes);
            return new ResponseEntity<>(resource, headers, HttpStatus.OK);

        } catch (IllegalStateException | IllegalArgumentException e) {
            // Örneğin: henüz işleme tamamlanmamış veya ID bulunamadı
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 4) Tek bir işlenmiş VİDEOYU getirme
     *    GET /api/result/video/{id}
     *    -> İşlenmiş videoyu byte[] olarak döner. (Content-Type: video/mp4)
     */
    @GetMapping("/video/{id}")
    public ResponseEntity<Resource> getProcessedVideo(@PathVariable Long id) {
        try {
            byte[] videoBytes = videoService.getVideoResult(id);
            if (videoBytes == null || videoBytes.length == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf("video/mp4"));
            headers.setContentDisposition(
                ContentDisposition.inline().filename("result-" + id + ".mp4").build()
            );

            Resource resource = new ByteArrayResource(videoBytes);
            return new ResponseEntity<>(resource, headers, HttpStatus.OK);

        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
