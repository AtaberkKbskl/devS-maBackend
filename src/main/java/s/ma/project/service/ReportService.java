package s.ma.project.service;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class ReportService {
    public Map<String, Object> generateStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", 100);
        stats.put("processedImages", 500);
        return stats;
    }
}