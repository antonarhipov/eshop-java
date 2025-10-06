package org.example.eshop.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CorrelationIdService {
    private static final ThreadLocal<String> CORRELATION_ID = new ThreadLocal<>();
    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";

    public String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }

    public void setCorrelationId(String correlationId) {
        CORRELATION_ID.set(correlationId);
    }

    public String getCorrelationId() {
        return CORRELATION_ID.get();
    }

    public void clearCorrelationId() {
        CORRELATION_ID.remove();
    }

    public String getOrGenerateCorrelationId() {
        String current = getCorrelationId();
        if (current != null) return current;
        String generated = generateCorrelationId();
        setCorrelationId(generated);
        return generated;
    }
}
