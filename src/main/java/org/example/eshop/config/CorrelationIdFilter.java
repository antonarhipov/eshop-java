package org.example.eshop.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.eshop.service.CorrelationIdService;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CorrelationIdFilter implements Filter {

    private final CorrelationIdService correlationIdService;

    public CorrelationIdFilter(CorrelationIdService correlationIdService) {
        this.correlationIdService = correlationIdService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        try {
            String correlationId = httpRequest.getHeader(CorrelationIdService.CORRELATION_ID_HEADER);
            if (correlationId == null || correlationId.isBlank()) {
                correlationId = correlationIdService.generateCorrelationId();
            }
            correlationIdService.setCorrelationId(correlationId);
            httpResponse.setHeader(CorrelationIdService.CORRELATION_ID_HEADER, correlationId);
            chain.doFilter(request, response);
        } finally {
            correlationIdService.clearCorrelationId();
        }
    }
}
