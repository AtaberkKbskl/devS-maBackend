package s.ma.project.service;



import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import s.ma.project.model.ImageFile;
import s.ma.project.repository.ImageRepository;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    private final String uploadDir = "uploads/";

    public void saveImage(MultipartFile file) {
        try {
            // Dosyayı diske kaydet
            Path filePath = Paths.get(uploadDir + file.getOriginalFilename());
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, file.getBytes());

            // Veritabanına kaydet
            ImageFile imageFile = new ImageFile();
            imageFile.setFileName(file.getOriginalFilename());
            imageFile.setFilePath(filePath.toString());
            imageRepository.save(imageFile);
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }
}