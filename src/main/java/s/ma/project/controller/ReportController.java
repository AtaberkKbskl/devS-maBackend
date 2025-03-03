package s.ma.project.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import s.ma.project.service.ReportService;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    @Autowired
    private ReportService reportService;

    @GetMapping("/statistics")
    public Map<String, Object> getStatistics() {
        return reportService.generateStatistics();
    }
}