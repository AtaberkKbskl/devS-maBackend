package s.ma.project.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import s.ma.project.model.ImageFile;
import s.ma.project.repository.ImageRepository;

import java.io.File;
import java.io.IOException;

@Service
public class ImageService {

    private final ImageRepository imageRepository;

    // Flask uygulamasının beklediği tam dosya yolu
    private static final String FLASK_UPLOAD_PATH = "/Users/ataberkkabasakal/Desktop/BİTİRME/devSımaAı/uploads/input.jpg";

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    /**
     * Yüklenen dosyayı Flask'in beklediği konuma "input.jpg" olarak yazar
     * ve veritabanına kaydeder.
     */
    public void saveImage(MultipartFile file) throws IOException {
        // Flask için gerekli input.jpg dosyasını oluştur
        File flaskFile = new File(FLASK_UPLOAD_PATH);
        flaskFile.getParentFile().mkdirs();
        file.transferTo(flaskFile);

        // İsteğe bağlı: veritabanına kayıt
        ImageFile imageFile = new ImageFile();
        imageFile.setFileName("input.jpg");
        imageFile.setFilePath(FLASK_UPLOAD_PATH);
        imageRepository.save(imageFile);
    }
}