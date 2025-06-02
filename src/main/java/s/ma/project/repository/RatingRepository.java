package s.ma.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import s.ma.project.model.Rating;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    // Tipe göre listeleme
    List<Rating> findByType(String type);

    // Ortalama puanı, entity’deki “value” alanına göre hesaplayın
    @Query("SELECT AVG(r.value) FROM Rating r WHERE r.type = :type")
    Double findAverageByType(@Param("type") String type);

    // Tipe göre kayıt sayısı
    @Query("SELECT COUNT(r) FROM Rating r WHERE r.type = :type")
    Long countByType(@Param("type") String type);
}
