package org.example.eshop.dto

import org.example.eshop.entity.Order
import org.example.eshop.entity.OrderStatus
import org.example.eshop.entity.PaymentStatus
import org.example.eshop.entity.FulfillmentStatus
import jakarta.validation.constraints.*
import java.math.BigDecimal
import java.time.LocalDateTime

data class CheckoutRequest(
    @field:NotBlank(message = "Full name is required")
    @field:Size(max = 255, message = "Full name must not exceed 255 characters")
    val fullName: String,

    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email must be valid")
    @field:Size(max = 255, message = "Email must not exceed 255 characters")
    val email: String,

    @field:Size(max = 50, message = "Phone must not exceed 50 characters")
    val phone: String? = null,

    @field:NotBlank(message = "Street address is required")
    @field:Size(max = 255, message = "Street address must not exceed 255 characters")
    val street1: String,

    @field:Size(max = 255, message = "Street address line 2 must not exceed 255 characters")
    val street2: String? = null,

    @field:NotBlank(message = "City is required")
    @field:Size(max = 255, message = "City must not exceed 255 characters")
    val city: String,

    @field:NotBlank(message = "Region/State is required")
    @field:Size(max = 255, message = "Region/State must not exceed 255 characters")
    val region: String,

    @field:NotBlank(message = "Postal code is required")
    @field:Size(max = 32, message = "Postal code must not exceed 32 characters")
    val postalCode: String,

    @field:NotBlank(message = "Country is required")
    @field:Size(max = 255, message = "Country must not exceed 255 characters")
    val country: String
)

data class OrderDto(
    val id: Long,
    val number: String,
    val email: String,
    val fullName: String,
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
                fullName = order.fullName,
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