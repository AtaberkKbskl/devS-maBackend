package s.ma.project.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Feedback (geri bildirim) için entity.
 * - id, username, type, comment, rating alanları.
 */
@Entity
@Table(name = "feedback")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String type;
    private String comment;
    private Integer rating;

    // Boş yapıcı (JPA gereksinimi)
    public Feedback() {
    }

    // Tüm alanları alan yapıcı
    public Feedback(Long id, String username, String type, String comment, Integer rating) {
        this.id = id;
        this.username = username;
        this.type = type;
        this.comment = comment;
        this.rating = rating;
    }

    // Getter–Setter metodları
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getRating() {
        return rating;
    }
    public void setRating(Integer rating) {
        this.rating = rating;
    }
}
