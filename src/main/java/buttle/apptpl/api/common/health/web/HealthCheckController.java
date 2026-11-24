package buttle.apptpl.api.common.health.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Health Check Controller
 * Jenkins 빌드 파이프라인에서 서비스 상태를 확인하기 위한 컨트롤러
 */
@RestController
@RequestMapping("/api/health")
public class HealthCheckController {

    /**
     * Health Check Endpoint
     * @return 서비스 상태 정보
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "aicc-admin-backend");
        response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        return ResponseEntity.ok(response);
    }

    /**
     * Simple Health Check (just returns HTTP 200)
     * @return HTTP 200 OK
     */
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }

    /**
     * Detailed Health Check
     * @return 상세 서비스 상태 정보
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "aicc-admin-backend");
        response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        response.put("uptime", getUptime());
        response.put("memoryInfo", getMemoryInfo());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get application uptime
     * @return uptime in milliseconds
     */
    private long getUptime() {
        return System.currentTimeMillis();
    }

    /**
     * Get memory information
     * @return memory info map
     */
    private Map<String, String> getMemoryInfo() {
        Runtime runtime = Runtime.getRuntime();
        Map<String, String> memoryInfo = new HashMap<>();
        
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        memoryInfo.put("maxMemory", formatBytes(maxMemory));
        memoryInfo.put("totalMemory", formatBytes(totalMemory));
        memoryInfo.put("freeMemory", formatBytes(freeMemory));
        memoryInfo.put("usedMemory", formatBytes(usedMemory));
        
        return memoryInfo;
    }

    /**
     * Format bytes to human-readable format
     * @param bytes bytes
     * @return formatted string
     */
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
}
