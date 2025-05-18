package s.ma.project.controller;

import s.ma.project.service.ImageService;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/image")
@CrossOrigin(origins = "http://localhost:5173")
public class ImageController {

    private final ImageService imageService;
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String FLASK_IMAGE_URL = "http://localhost:5000/anonymize/image";

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    /**
     * Upload endpoint: dosyayı ImageService aracılığıyla Flask klasörüne kaydeder
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            imageService.saveImage(file);
            return ResponseEntity.ok("Image uploaded successfully!");
        } catch (IOException e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Upload failed: " + e.getMessage());
        }
    }

    /**
     * Process endpoint: Flask'tan işlenmiş resmi çekip frontend'e JPEG olarak iletir
     */
    @GetMapping("/process")
    public ResponseEntity<byte[]> processImage() {
        ResponseEntity<byte[]> response = restTemplate.getForEntity(FLASK_IMAGE_URL, byte[].class);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(response.getBody(), headers, HttpStatus.OK);
    }
}
