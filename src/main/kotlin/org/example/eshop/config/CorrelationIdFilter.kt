package org.example.eshop.config

import org.example.eshop.service.CorrelationIdService
import org.springframework.stereotype.Component
import jakarta.servlet.*
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

@Component
class CorrelationIdFilter(
    private val correlationIdService: CorrelationIdService
) : Filter {

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        val httpResponse = response as HttpServletResponse
        
        try {
            // Get correlation ID from header or generate new one
            val correlationId = httpRequest.getHeader(CorrelationIdService.CORRELATION_ID_HEADER)
                ?: correlationIdService.generateCorrelationId()
            
            // Set correlation ID in thread local
            correlationIdService.setCorrelationId(correlationId)
            
            // Add correlation ID to response header
            httpResponse.setHeader(CorrelationIdService.CORRELATION_ID_HEADER, correlationId)
            
            // Continue with the request
            chain.doFilter(request, response)
        } finally {
            // Clean up thread local to prevent memory leaks
            correlationIdService.clearCorrelationId()
        }
    }
}