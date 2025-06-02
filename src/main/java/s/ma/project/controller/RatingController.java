package s.ma.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import s.ma.project.model.Rating;
import s.ma.project.service.RatingService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ratings")
@CrossOrigin(origins = "http://localhost:5173")  // React'ten veya başka frontend’ten erişim için
public class RatingController {

    private final RatingService ratingService;

    @Autowired
    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    /**
     * 1) Yeni rating ekleme
     *    POST /api/ratings
     *    Gövde örneği:
     *    {
     *      "type": "video",
     *      "stars": 4,
     *      "userName": "Ali"
     *    }
     */
    @PostMapping
    public ResponseEntity<Rating> addRating(@RequestBody Rating rating) {
        Rating saved = ratingService.saveRating(rating);
        return ResponseEntity.ok(saved);
    }

    /**
     * 2) Türüne göre ortalama ve toplam oy sayısını verir
     *    GET /api/ratings/stats?type=video
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStatsByType(@RequestParam("type") String type) {
        Double average = ratingService.getAverageByType(type);
        Long count = ratingService.getCountByType(type);

        Map<String, Object> response = new HashMap<>();
        response.put("type", type);
        response.put("average", average);
        response.put("count", count);

        return ResponseEntity.ok(response);
    }

    /**
     * 3) Opsiyonel: Tüm rating’leri listeler
     *    GET /api/ratings
     */
    @GetMapping
    public ResponseEntity<Iterable<Rating>> getAllRatings() {
        // Burada type=null verdiğinizde, repository içinde findByType(null) → JPA null kontrolüne bağlı olarak
        // boş liste dönüyor ya da hata veriyorsa, service katmanında böyle bir durumda tümünü dönecek şekilde yazın.
        return ResponseEntity.ok(ratingService.getRatingsByType(null));
    }
}
