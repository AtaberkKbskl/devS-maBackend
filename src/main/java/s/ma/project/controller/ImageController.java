package s.ma.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import s.ma.project.model.MediaFile;
import s.ma.project.service.ImageService;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/image")
@CrossOrigin(origins = "http://localhost:5173")
public class ImageController {

    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    /**
     * 1) Upload endpoint’i: Dosyayı kaydeder, DB kaydını oluşturur, PENDING statüde döner.
     *    Örnek Response:
     *    { "mediaId": 12, "status": "PENDING" }
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            // 1. Burada token kontrolünden sonra username elde edebilirsiniz. Örneğin:
            // String username = jwtUtil.extractUsername(token);
            // Simpel örnek: Authorization: "Bearer eyJhbGciOi..."
            String token = authHeader.substring(7);
            String ownerUsername = /* JWT’den decode edip kullanıcı adı */ "";

            MediaFile mediaFile = imageService.saveImage(file, ownerUsername);
            return ResponseEntity.ok(
                    Map.of("mediaId", mediaFile.getId(), "status", mediaFile.getStatus())
            );
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Upload failed: " + e.getMessage()));
        }
    }

    /**
     * 2) İşlem başlatma (process) endpoint’i:
     *    GET /api/image/process/{mediaId}
     *    --> DB’de status “PROCESSING” olur, Flask’a istek atılır, iş bitince “DONE” veya “FAILED”
     */
    @GetMapping("/process/{mediaId}")
    public ResponseEntity<?> startImageProcessing(
            @PathVariable Long mediaId
    ) {
        try {
            imageService.startImageProcessing(mediaId);
            return ResponseEntity.ok(Map.of("mediaId", mediaId, "status", "PROCESSING"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Processing error: " + e.getMessage()));
        }
    }

    /**
     * 3) Sonuç (result) endpoint’i:
     *    GET /api/image/result/{mediaId}
     *    --> Status “DONE” ise image bytes döner, aksi halde hata mesajı.
     */
    @GetMapping("/result/{mediaId}")
    public ResponseEntity<byte[]> getImageResult(@PathVariable Long mediaId) {
        try {
            byte[] imageBytes = imageService.getImageResult(mediaId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG); // ya da uygun içerik türü
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
