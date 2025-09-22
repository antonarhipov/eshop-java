package org.example.eshop.service

import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class AuditLogService(
    private val correlationIdService: CorrelationIdService
) {
    
    private val logger = LoggerFactory.getLogger("AUDIT")
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    
    fun logAdminAction(action: String, entityType: String, entityId: Any?, details: String? = null) {
        val username = getCurrentUsername()
        val timestamp = LocalDateTime.now().format(dateFormatter)
        val correlationId = correlationIdService.getCorrelationId() ?: "UNKNOWN"
        
        val logMessage = buildString {
            append("[AUDIT] ")
            append("correlationId=$correlationId ")
            append("timestamp=$timestamp ")
            append("user=$username ")
            append("action=$action ")
            append("entityType=$entityType ")
            if (entityId != null) {
                append("entityId=$entityId ")
            }
            if (details != null) {
                append("details=$details")
            }
        }
        
        logger.info(logMessage)
    }
    
    fun logAdminLogin(username: String, success: Boolean) {
        val timestamp = LocalDateTime.now().format(dateFormatter)
        val correlationId = correlationIdService.getCorrelationId() ?: "UNKNOWN"
        val status = if (success) "SUCCESS" else "FAILED"
        
        logger.info("[AUDIT] correlationId=$correlationId timestamp=$timestamp user=$username action=LOGIN status=$status")
    }
    
    fun logAdminLogout(username: String) {
        val timestamp = LocalDateTime.now().format(dateFormatter)
        val correlationId = correlationIdService.getCorrelationId() ?: "UNKNOWN"
        
        logger.info("[AUDIT] correlationId=$correlationId timestamp=$timestamp user=$username action=LOGOUT")
    }
    
    private fun getCurrentUsername(): String {
        return try {
            val authentication = SecurityContextHolder.getContext().authentication
            authentication?.name ?: "UNKNOWN"
        } catch (e: Exception) {
            "UNKNOWN"
        }
    }
}