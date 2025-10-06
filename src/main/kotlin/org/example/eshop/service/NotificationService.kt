package org.example.eshop.service

import org.example.eshop.entity.Order
import org.example.eshop.repository.OrderItemRepository
import org.example.eshop.repository.VariantRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class NotificationService(
    private val orderItemRepository: OrderItemRepository,
    private val variantRepository: VariantRepository
) {
    
    private val logger = LoggerFactory.getLogger(NotificationService::class.java)
    
    fun logOrderReceived(order: Order) {
        try {
            val orderItems = orderItemRepository.findByOrderId(order.id)
            
            val itemDetails = orderItems.map { orderItem ->
                "- ${orderItem.titleSnapshot} (Qty: ${orderItem.qty}, Price: ${orderItem.priceSnapshot})"
            }.joinToString("\n")
            
            val logMessage = """
                |ORDER RECEIVED - AWAITING PAYMENT
                |Order Number: ${order.number}
                |Order Date: ${order.createdAt}
                |
                |Items:
                |$itemDetails
                |
                |Financial Summary:
                |Subtotal: ${order.subtotal}
                |VAT Amount: ${order.tax}
                |Shipping Cost: ${order.shipping}
                |Total: ${order.total}
                |
                |Payment Status: ${order.paymentStatus}
                |Order Status: ${order.status}
            """.trimMargin()
            
            logger.info(logMessage)
            
        } catch (e: Exception) {
            // Log the error but don't block order creation
            logger.error("Failed to log order received event for order ${order.number}", e)
        }
    }
    
    fun logPaymentReceived(order: Order) {
        try {
            val logMessage = """
                |PAYMENT RECEIVED
                |Order Number: ${order.number}
                |Customer Email: ${order.email}
                |Payment Date: ${order.updatedAt}
                |Total Paid: ${order.total}
                |Payment Status: ${order.paymentStatus}
            """.trimMargin()
            
            logger.info(logMessage)
            
        } catch (e: Exception) {
            logger.error("Failed to log payment received event for order ${order.number}", e)
        }
    }
    
    fun logOrderShipped(order: Order) {
        try {
            val logMessage = """
                |ORDER SHIPPED
                |Order Number: ${order.number}
                |Customer Email: ${order.email}
                |Ship Date: ${order.updatedAt}
                |Tracking URL: ${order.trackingUrl ?: "Not provided"}
                |Fulfillment Status: ${order.fulfillmentStatus}
            """.trimMargin()
            
            logger.info(logMessage)
            
        } catch (e: Exception) {
            logger.error("Failed to log order shipped event for order ${order.number}", e)
        }
    }
}