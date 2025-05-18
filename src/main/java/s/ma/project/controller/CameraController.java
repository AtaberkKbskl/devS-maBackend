package s.ma.project.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@RestController
@RequestMapping("/api/camera")
@CrossOrigin(origins = "http://localhost:5173")
public class CameraController {

    @PostMapping("/frame")
    public ResponseEntity<Map<String, String>> processFrame(@RequestBody Map<String, String> body) {
        String imageBase64 = body.get("image");

        // Flask'a ilet
        RestTemplate restTemplate = new RestTemplate();
        String flaskUrl = "http://localhost:5000/anonymize/frame";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(Map.of("image", imageBase64), headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(flaskUrl, request, Map.class);

        return ResponseEntity.ok(response.getBody());
    }
}
