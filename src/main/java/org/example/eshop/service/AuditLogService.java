package org.example.eshop.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class AuditLogService {
    private final CorrelationIdService correlationIdService;
    private final Logger logger = LoggerFactory.getLogger("AUDIT");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public AuditLogService(CorrelationIdService correlationIdService) {
        this.correlationIdService = correlationIdService;
    }

    public void logAdminAction(String action, String entityType, Object entityId, String details) {
        String username = getCurrentUsername();
        String timestamp = LocalDateTime.now().format(dateFormatter);
        String correlationId = correlationIdService.getCorrelationId();
        if (correlationId == null) correlationId = "UNKNOWN";

        StringBuilder sb = new StringBuilder();
        sb.append("[AUDIT] ")
          .append("correlationId=").append(correlationId).append(' ')
          .append("timestamp=").append(timestamp).append(' ')
          .append("user=").append(username).append(' ')
          .append("action=").append(action).append(' ')
          .append("entityType=").append(entityType).append(' ');
        if (entityId != null) {
            sb.append("entityId=").append(entityId).append(' ');
        }
        if (details != null) {
            sb.append("details=").append(details);
        }
        logger.info(sb.toString());
    }

    public void logAdminLogin(String username, boolean success) {
        String timestamp = LocalDateTime.now().format(dateFormatter);
        String correlationId = correlationIdService.getCorrelationId();
        if (correlationId == null) correlationId = "UNKNOWN";
        String status = success ? "SUCCESS" : "FAILED";
        logger.info("[AUDIT] correlationId={} timestamp={} user={} action=LOGIN status={}", correlationId, timestamp, username, status);
    }

    public void logAdminLogout(String username) {
        String timestamp = LocalDateTime.now().format(dateFormatter);
        String correlationId = correlationIdService.getCorrelationId();
        if (correlationId == null) correlationId = "UNKNOWN";
        logger.info("[AUDIT] correlationId={} timestamp={} user={} action=LOGOUT", correlationId, timestamp, username);
    }

    private String getCurrentUsername() {
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            return authentication != null ? authentication.getName() : "UNKNOWN";
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }
}
