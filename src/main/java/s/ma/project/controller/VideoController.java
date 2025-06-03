package s.ma.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import s.ma.project.model.MediaFile;
import s.ma.project.service.VideoService;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/video")
@CrossOrigin(origins = "http://localhost:5173")
public class VideoController {

    private final VideoService videoService;

    @Autowired
    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    /**
     * 1) Upload endpoint: multipart/form-data ile video alır,
     *    DB’ye kaydeder (status=PENDING) ve mediaId döner.
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadVideo(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            // Burada JWT’den kullanıcıyı elde edebilirsiniz (Auth katmanı zaten hazır)
            String token = authHeader.substring(7);
            String ownerUsername = /* JWT’den decode edilerek username */ "";

            MediaFile mediaFile = videoService.saveVideoForFlask(file, ownerUsername);
            return ResponseEntity.ok(
                    Map.of("mediaId", mediaFile.getId(), "status", mediaFile.getStatus())
            );
        } catch (IOException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Upload failed: " + e.getMessage()));
        }
    }

    /**
     * 2) İşlem başlatma endpoint’i:
     *    GET /api/video/process/{mediaId}
     */
    @GetMapping("/process/{mediaId}")
    public ResponseEntity<?> startVideoProcessing(@PathVariable Long mediaId) {
        try {
            videoService.startVideoProcessing(mediaId);
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
     *    GET /api/video/result/{mediaId}
     */
    @GetMapping("/result/{mediaId}")
    public ResponseEntity<byte[]> getVideoResult(@PathVariable Long mediaId) {
        try {
            byte[] videoBytes = videoService.getVideoResult(mediaId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf("video/mp4"));
            headers.setContentDisposition(ContentDisposition.inline()
                    .filename("anonymized-" + mediaId + ".mp4")
                    .build());
            return new ResponseEntity<>(videoBytes, headers, HttpStatus.OK);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
