package s.ma.project.service;

import org.springframework.stereotype.Service;
import s.ma.project.model.Rating;
import s.ma.project.repository.RatingRepository;

import java.util.List;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;

    // Elle eklenen yapıcı: Spring, RatingRepository bean’ini buraya enjekte eder
    public RatingService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    // Yeni bir oylama ekle
    public Rating saveRating(Rating rating) {
        return ratingRepository.save(rating);
    }

    // Bir tipe göre tüm ratingleri getir
    public List<Rating> getRatingsByType(String type) {
        return ratingRepository.findByType(type);
    }

    // Ortalama puanı getir
    public Double getAverageByType(String type) {
        Double avg = ratingRepository.findAverageByType(type);
        return avg == null ? 0.0 : avg;
    }

    // Toplam oy sayısını getir
    public Long getCountByType(String type) {
        return ratingRepository.countByType(type);
    }
}
