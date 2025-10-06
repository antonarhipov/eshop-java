package org.example.eshop.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.eshop.service.CorrelationIdService;

/**
 * Utilities that replace potential Kotlin extension-style helpers
 * for working with correlation IDs on Servlet requests/responses.
 */
public final class CorrelationIds {

    private CorrelationIds() {}

    public static String headerName() {
        return CorrelationIdService.CORRELATION_ID_HEADER;
    }

    public static String getOrGenerate(HttpServletRequest request, CorrelationIdService service) {
        String id = request.getHeader(CorrelationIdService.CORRELATION_ID_HEADER);
        if (id == null || id.isBlank()) {
            id = service.generateCorrelationId();
        }
        service.setCorrelationId(id);
        return id;
    }

    public static void write(HttpServletResponse response, String id) {
        response.setHeader(CorrelationIdService.CORRELATION_ID_HEADER, id);
    }

    public static void clear(CorrelationIdService service) {
        service.clearCorrelationId();
    }
}
