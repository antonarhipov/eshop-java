package org.example.eshop.service

import org.springframework.stereotype.Service
import java.util.*

@Service
class CorrelationIdService {
    
    companion object {
        private val correlationIdThreadLocal = ThreadLocal<String>()
        const val CORRELATION_ID_HEADER = "X-Correlation-ID"
    }
    
    fun generateCorrelationId(): String {
        return UUID.randomUUID().toString()
    }
    
    fun setCorrelationId(correlationId: String) {
        correlationIdThreadLocal.set(correlationId)
    }
    
    fun getCorrelationId(): String? {
        return correlationIdThreadLocal.get()
    }
    
    fun clearCorrelationId() {
        correlationIdThreadLocal.remove()
    }
    
    fun getOrGenerateCorrelationId(): String {
        return getCorrelationId() ?: generateCorrelationId().also { setCorrelationId(it) }
    }
}