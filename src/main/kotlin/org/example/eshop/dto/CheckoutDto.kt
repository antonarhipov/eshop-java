package org.example.eshop.dto

import org.example.eshop.entity.Order
import org.example.eshop.entity.OrderStatus
import org.example.eshop.entity.PaymentStatus
import org.example.eshop.entity.FulfillmentStatus
import java.math.BigDecimal
import java.time.LocalDateTime

data class CheckoutRequest(
    val email: String,
    val address: String
)

data class OrderDto(
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
    val items: List<OrderItemDto>
) {
    companion object {
        fun fromEntity(order: Order): OrderDto {
            return OrderDto(
                id = order.id,
                number = order.number,
                email = order.email,
                address = order.address,
                subtotal = order.subtotal,
                tax = order.tax,
                shipping = order.shipping,
                total = order.total,
                status = order.status,
                paymentStatus = order.paymentStatus,
                fulfillmentStatus = order.fulfillmentStatus,
                trackingUrl = order.trackingUrl,
                createdAt = order.createdAt,
                updatedAt = order.updatedAt,
                items = order.items.map { OrderItemDto.fromEntity(it) }
            )
        }
    }
}

data class OrderItemDto(
    val id: Long,
    val variantId: Long,
    val titleSnapshot: String,
    val qty: Int,
    val priceSnapshot: BigDecimal,
    val lineTotal: BigDecimal
) {
    companion object {
        fun fromEntity(orderItem: org.example.eshop.entity.OrderItem): OrderItemDto {
            return OrderItemDto(
                id = orderItem.id,
                variantId = orderItem.variantId,
                titleSnapshot = orderItem.titleSnapshot,
                qty = orderItem.qty,
                priceSnapshot = orderItem.priceSnapshot,
                lineTotal = orderItem.lineTotal
            )
        }
    }
}

data class CheckoutResponse(
    val success: Boolean,
    val message: String? = null,
    val order: OrderDto? = null,
    val errors: List<String> = emptyList()
)