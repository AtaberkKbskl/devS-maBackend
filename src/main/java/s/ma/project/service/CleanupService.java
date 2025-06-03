package s.ma.project.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import s.ma.project.model.MediaFile;
import s.ma.project.repository.MediaFileRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CleanupService {

    private final MediaFileRepository mediaFileRepository;

    public CleanupService(MediaFileRepository mediaFileRepository) {
        this.mediaFileRepository = mediaFileRepository;
    }

    /**
     * Her gece saat 03:00’te çalışır. (Cron ifadesini ihtiyacınıza göre değiştirin.)
     * 7 günden eski “DONE” statülü kayıtları siler:
     * - Diskten inputPath ve outputPath dosyalarını kaldırır.
     * - DB kaydını siler.
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanupOldFiles() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(7);
        List<MediaFile> oldFiles = mediaFileRepository
                .findAllByStatusAndProcessedTimeBefore("DONE", threshold);

        for (MediaFile mf : oldFiles) {
            try {
                // Input dosyası
                Files.deleteIfExists(Paths.get(mf.getStoredPath()));
                // Output dosyası
                if (mf.getOutputPath() != null) {
                    Files.deleteIfExists(Paths.get(mf.getOutputPath()));
                }
            } catch (IOException e) {
                // Loglama (ör. Logger.error(“Dosya silme hatası”, e))
            }
            mediaFileRepository.delete(mf);
        }
    }
}
