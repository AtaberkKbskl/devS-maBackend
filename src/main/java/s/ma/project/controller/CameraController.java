package s.ma.project.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/camera")
@CrossOrigin(origins = "http://localhost:5173")
public class CameraController {

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Her frame base64 formatında JSON ile gönderilir:
     * {
     *   "image": "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQ..."
     * }
     * 
     * Flask tarafında base64’ü decode edip işleyecek bir endpoint (POST /anonymize/frame) olmalı.
     */
    @PostMapping("/frame")
    public ResponseEntity<?> processFrame(
            @RequestBody Map<String, String> body
    ) {
        String imageBase64 = body.get("image");
        if (imageBase64 == null || imageBase64.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "No image data provided"));
        }

        // Unique ID üretilip header’a ekleyebilirsiniz (izleme/günlüğe kayıt için)
        String requestId = UUID.randomUUID().toString();

        String flaskUrl = "http://localhost:5000/anonymize/frame";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(
                Map.of("requestId", requestId, "image", imageBase64),
                headers
        );

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(flaskUrl, request, Map.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                // Flask’tan dönen JSON yapısı neyse aynen geri dönebilirsiniz
                return ResponseEntity.ok(response.getBody());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Frame processing failed in Flask"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Frame processing error: " + e.getMessage()));
        }
    }
}
