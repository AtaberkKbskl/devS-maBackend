package s.ma.project.controller;

import java.io.IOException;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import s.ma.project.service.VideoService;

@RestController
@RequestMapping("/api/video")
@CrossOrigin(origins = "http://localhost:5173")
public class VideoController {

    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadVideo(@RequestParam("file") MultipartFile file) {
        try {
            // Dosyayı Flask için uygun klasöre ve adıyla kaydet
            videoService.saveVideoForFlask(file);
            return ResponseEntity.ok("Video başarıyla yüklendi!");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Yükleme başarısız: " + e.getMessage());
        }
    }

    @GetMapping("/process")
    public ResponseEntity<byte[]> processVideo() {
        byte[] anonymized = videoService.processVideo();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("video/mp4"));
        // Tarayıcıda inline oynatmak istersen inline(), indirme için attachment() kullan
        headers.setContentDisposition(ContentDisposition.inline()
                                                  .filename("anonymized_video.mp4")
                                                  .build());
        return new ResponseEntity<>(anonymized, headers, HttpStatus.OK);
    }
}
