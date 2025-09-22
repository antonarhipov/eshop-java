package org.example.eshop.dto

import org.example.eshop.entity.OrderStatus
import org.example.eshop.entity.PaymentStatus
import org.example.eshop.entity.FulfillmentStatus
import jakarta.validation.constraints.*
import java.math.BigDecimal
import java.time.LocalDateTime

// Request DTOs
data class MarkOrderPaidRequest(
    val notes: String? = null
)

data class ShipOrderRequest(
    @field:Size(max = 500, message = "Tracking URL must not exceed 500 characters")
    val trackingUrl: String? = null,
    
    val notes: String? = null
)

data class CancelOrderRequest(
    val reason: String? = null,
    val notes: String? = null
)

// Response DTOs
data class AdminOrderResponse(
    val id: Long,
    val number: String,
    val email: String,
    val address: String,
    val subtotal: BigDecimal,
    val tax: BigDecimal,
    val shipping: BigDecimal,
    val total: BigDecimal,
    val status: OrderStatus,
    val paymentStatus: PaymentStatus,
    val fulfillmentStatus: FulfillmentStatus,
    val trackingUrl: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val items: List<AdminOrderItemResponse>
)

data class AdminOrderItemResponse(
    val id: Long,
    val variantId: Long,
    val titleSnapshot: String,
    val qty: Int,
    val priceSnapshot: BigDecimal,
    val lineTotal: BigDecimal,
    val sku: String?,
    val productTitle: String?
)

data class AdminOrderListResponse(
    val orders: List<AdminOrderSummaryResponse>,
    val totalElements: Long,
    val totalPages: Int,
    val currentPage: Int,
    val pageSize: Int
)

data class AdminOrderSummaryResponse(
    val id: Long,
    val number: String,
    val email: String,
    val total: BigDecimal,
    val status: OrderStatus,
    val paymentStatus: PaymentStatus,
    val fulfillmentStatus: FulfillmentStatus,
    val createdAt: LocalDateTime,
    val itemCount: Int
)