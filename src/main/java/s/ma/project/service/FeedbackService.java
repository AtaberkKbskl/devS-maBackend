package s.ma.project.service;

import org.springframework.stereotype.Service;
import s.ma.project.model.Feedback;
import s.ma.project.repository.FeedbackRepository;

import java.util.List;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    // Manuel yapıcı: Spring, FeedbackRepository bean’ini buraya enjekte eder
    public FeedbackService(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    // Yeni bir feedback kaydı ekle
    public Feedback saveFeedback(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }

    // Tüm geri bildirimleri listele
    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAll();
    }

    // Bir tipe ait tüm geri bildirimleri getir
    public List<Feedback> getFeedbacksByType(String type) {
        return feedbackRepository.findByType(type);
    }

    // Bir tipe ait ortalama rating değerini getir (null ise 0.0 döner)
    public Double getAverageRatingByType(String type) {
        Double avg = feedbackRepository.findAverageRatingByType(type);
        return (avg == null ? 0.0 : avg);
    }

    // Bir tipe ait toplam kaç adet feedback var, sayısını getir
    public Long getCountByType(String type) {
        Long count = feedbackRepository.countByType(type);
        return (count == null ? 0L : count);
    }
}
