package s.ma.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import s.ma.project.service.ImageService;

@CrossOrigin(origins = "http://localhost:5173")  // CORS Ayarları
@RestController
@RequestMapping("/api/image")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        imageService.saveImage(file);
        return ResponseEntity.ok("Image uploaded successfully!");
    }

    @GetMapping("/process")
    public ResponseEntity<String> processImage() {
        // AI işlemleri burada yapılacak
        return ResponseEntity.ok("Image processed successfully!");
    }
}
