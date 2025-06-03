package s.ma.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import s.ma.project.model.MediaFile;

import java.time.LocalDateTime;
import java.util.List;

public interface MediaFileRepository extends JpaRepository<MediaFile, Long> {

    // Belirli durumdaki (status) ve işlem zamanı belirli tarihten önce olan kayıtları bulmak için
    List<MediaFile> findAllByStatusAndProcessedTimeBefore(String status, LocalDateTime before);

    // Örnek olarak, “MEDIA_TYPE” + “STATUS” + “UPLOAD_TIME” aralığına göre sayma
    long countByMediaTypeAndStatusAndUploadTimeBetween(
        String mediaType, String status, LocalDateTime start, LocalDateTime end
    );

    // “STATUS” + “PROCESSED_TIME” aralığına göre listeleme
    List<MediaFile> findAllByStatusAndProcessedTimeBetween(
        String status, LocalDateTime start, LocalDateTime end
    );
}
