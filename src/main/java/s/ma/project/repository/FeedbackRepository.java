package s.ma.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import s.ma.project.model.Feedback;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    /**
     * Belirli bir tipe ait tüm geri bildirimleri listeler.
     * Örnek: type = "video", yalnızca video tipi için yapılmış feedback’leri döner.
     */
    List<Feedback> findByType(String type);

    /**
     * Belirli tipe ait ortalama rating (yıldız) değerini hesaplar.
     * JPQL: SELECT AVG(f.rating) FROM Feedback f WHERE f.type = :type
     */
    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.type = :type")
    Double findAverageRatingByType(@Param("type") String type);

    /**
     * Belirli tipe ait kaç adet feedback kaydı olduğunu döner.
     * JPQL: SELECT COUNT(f) FROM Feedback f WHERE f.type = :type
     */
    @Query("SELECT COUNT(f) FROM Feedback f WHERE f.type = :type")
    Long countByType(@Param("type") String type);
}
