package s.ma.project.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import s.ma.project.service.FeedbackService;
import s.ma.project.model.Feedback;
import java.util.List;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {
    @Autowired
    private FeedbackService feedbackService;

    @PostMapping("/submit")
    public String submitFeedback(@RequestBody Feedback feedback) {
        feedbackService.saveFeedback(feedback);
        return "Feedback submitted!";
    }

    @GetMapping("/all")
    public List<Feedback> getAllFeedback() {
        return feedbackService.getAllFeedback();
    }
}