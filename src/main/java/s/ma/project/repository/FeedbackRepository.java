package s.ma.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import s.ma.project.model.Feedback;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
}