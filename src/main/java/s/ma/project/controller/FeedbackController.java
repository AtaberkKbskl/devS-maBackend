package s.ma.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import s.ma.project.model.Feedback;
import s.ma.project.service.FeedbackService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin(origins = "http://localhost:5173")  // Eğer her front-end’den (React vb.) erişecekse “*” kullanılabilir. Credentials yoksa sorun yok.
public class FeedbackController {

    private final FeedbackService feedbackService;

    // -------------------------------------------
    // Manuel constructor + @Autowired (Lombok kaldırıldı)
    // -------------------------------------------
    @Autowired
    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    /**
     * 1) Yeni bir feedback kaydı ekle (rating + comment + username + type)
     *    Örnek request body:
     *    {
     *       "username": "Ali",
     *       "type": "video",
     *       "rating": 5,
     *       "comment": "Video kalitesi çok iyi!"
     *    }
     */
    @PostMapping("/submit")
    public ResponseEntity<Feedback> submitFeedback(@RequestBody Feedback feedback) {
        // rating null veya 1–5 aralığında mı?
        if (feedback.getRating() == null || feedback.getRating() < 1 || feedback.getRating() > 5) {
            return ResponseEntity.badRequest().build();
        }
        // type boş olamaz
        if (feedback.getType() == null || feedback.getType().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Feedback saved = feedbackService.saveFeedback(feedback);
        return ResponseEntity.ok(saved);
    }

    /**
     * 2) Tüm feedback’leri listele
     *    GET /api/feedback/all
     */
    @GetMapping("/all")
    public ResponseEntity<List<Feedback>> getAllFeedback() {
        List<Feedback> list = feedbackService.getAllFeedback();
        return ResponseEntity.ok(list);
    }

    /**
     * 3) Belirli bir type’a ait tüm feedback’leri getir
     *    GET /api/feedback/type/{type}
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Feedback>> getFeedbacksByType(@PathVariable String type) {
        List<Feedback> list = feedbackService.getFeedbacksByType(type);
        return ResponseEntity.ok(list);
    }

    /**
     * 4) Belirli bir type’a ait ortalama rating ve toplam sayısını döner
     *    GET /api/feedback/stats?type=video
     *    Response JSON:
     *    {
     *      "type": "video",
     *      "averageRating": 4.2,
     *      "count": 37
     *    }
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStatsByType(@RequestParam("type") String type) {
        Double avg = feedbackService.getAverageRatingByType(type);
        Long count = feedbackService.getCountByType(type);

        Map<String, Object> response = new HashMap<>();
        response.put("type", type);
        response.put("averageRating", avg);
        response.put("count", count);

        return ResponseEntity.ok(response);
    }

    /**
     * 5) Feedback güncelleme (optional)
     *    PUT /api/feedback/update/{id}
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<Feedback> updateFeedback(
            @PathVariable Long id,
            @RequestBody Feedback incoming) {

        return feedbackService.getAllFeedback().stream()
                .filter(f -> f.getId().equals(id))
                .findFirst()
                .map(existing -> {
                    // comment değiştirme
                    if (incoming.getComment() != null) {
                        existing.setComment(incoming.getComment());
                    }
                    // rating değiştirme (1–5 aralığında kontrol et)
                    if (incoming.getRating() != null) {
                        if (incoming.getRating() < 1 || incoming.getRating() > 5) {
                            return ResponseEntity.badRequest().<Feedback>build();
                        }
                        existing.setRating(incoming.getRating());
                    }
                    // type değiştirme
                    if (incoming.getType() != null && !incoming.getType().isBlank()) {
                        existing.setType(incoming.getType());
                    }
                    // username değiştirme
                    if (incoming.getUsername() != null && !incoming.getUsername().isBlank()) {
                        existing.setUsername(incoming.getUsername());
                    }
                    Feedback updated = feedbackService.saveFeedback(existing);
                    return ResponseEntity.ok(updated);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
