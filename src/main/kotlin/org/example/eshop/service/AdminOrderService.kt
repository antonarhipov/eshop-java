package org.example.eshop.service

import org.example.eshop.dto.*
import org.example.eshop.entity.*
import org.example.eshop.repository.*
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.slf4j.LoggerFactory
import java.math.BigDecimal

@Service
@Transactional
class AdminOrderService(
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val variantRepository: VariantRepository,
    private val productRepository: ProductRepository
) {
    
    private val logger = LoggerFactory.getLogger(AdminOrderService::class.java)

    fun getAllOrders(
        page: Int,
        size: Int,
        status: String?,
        paymentStatus: String?,
        fulfillmentStatus: String?
    ): AdminOrderListResponse {
        // Validate enum values if provided
        val orderStatus = status?.let { 
            try { OrderStatus.valueOf(it.uppercase()) } 
            catch (e: IllegalArgumentException) { 
                throw IllegalArgumentException("Invalid order status: $it") 
            }
        }
        val paymentStatusEnum = paymentStatus?.let { 
            try { PaymentStatus.valueOf(it.uppercase()) } 
            catch (e: IllegalArgumentException) { 
                throw IllegalArgumentException("Invalid payment status: $it") 
            }
        }
        val fulfillmentStatusEnum = fulfillmentStatus?.let { 
            try { FulfillmentStatus.valueOf(it.uppercase()) } 
            catch (e: IllegalArgumentException) { 
                throw IllegalArgumentException("Invalid fulfillment status: $it") 
            }
        }

        val pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        val ordersPage = orderRepository.findAllWithFilters(
            orderStatus, paymentStatusEnum, fulfillmentStatusEnum, pageRequest
        )

        val orderSummaries = ordersPage.content.map { order ->
            AdminOrderSummaryResponse(
                id = order.id,
                number = order.number,
                email = order.email,
                total = order.total,
                status = order.status,
                paymentStatus = order.paymentStatus,
                fulfillmentStatus = order.fulfillmentStatus,
                createdAt = order.createdAt,
                itemCount = order.items.size
            )
        }

        return AdminOrderListResponse(
            orders = orderSummaries,
            totalElements = ordersPage.totalElements,
            totalPages = ordersPage.totalPages,
            currentPage = page,
            pageSize = size
        )
    }

    fun getOrderById(id: Long): AdminOrderResponse {
        val order = orderRepository.findById(id).orElseThrow {
            NoSuchElementException("Order with id $id not found")
        }

        return toAdminOrderResponse(order)
    }

    fun markOrderAsPaid(id: Long): AdminOrderResponse {
        val order = orderRepository.findById(id).orElseThrow {
            NoSuchElementException("Order with id $id not found")
        }

        // Validate order state
        if (order.paymentStatus == PaymentStatus.PAID) {
            throw IllegalStateException("Order ${order.number} is already marked as paid")
        }
        if (order.status == OrderStatus.CANCELLED) {
            throw IllegalStateException("Cannot mark cancelled order ${order.number} as paid")
        }

        // Update payment status
        order.paymentStatus = PaymentStatus.PAID
        order.status = OrderStatus.CONFIRMED

        // Adjust stock quantities: stockQty -= qty, reservedQty -= qty
        order.items.forEach { orderItem ->
            val variant = variantRepository.findById(orderItem.variantId).orElseThrow {
                IllegalStateException("Variant ${orderItem.variantId} not found for order item ${orderItem.id}")
            }

            // Check if we have enough reserved stock
            if (variant.reservedQty < orderItem.qty) {
                throw IllegalStateException(
                    "Insufficient reserved stock for variant ${variant.sku}. " +
                    "Required: ${orderItem.qty}, Reserved: ${variant.reservedQty}"
                )
            }

            // Adjust stock quantities
            val updatedVariant = variant.copy(
                stockQty = variant.stockQty - orderItem.qty,
                reservedQty = variant.reservedQty - orderItem.qty
            )
            variantRepository.save(updatedVariant)

            logger.info("Stock adjusted for variant ${variant.sku}: " +
                "stockQty ${variant.stockQty} -> ${updatedVariant.stockQty}, " +
                "reservedQty ${variant.reservedQty} -> ${updatedVariant.reservedQty}")
        }

        val savedOrder = orderRepository.save(order)

        // Log payment received event
        logger.info("Payment Received - Order: ${order.number}, " +
            "Items: ${order.items.map { "${it.titleSnapshot} (${it.qty})" }}, " +
            "Total: ${order.total}, VAT: ${order.tax}, Shipping: ${order.shipping}")

        return toAdminOrderResponse(savedOrder)
    }

    fun shipOrder(id: Long, trackingUrl: String?): AdminOrderResponse {
        val order = orderRepository.findById(id).orElseThrow {
            NoSuchElementException("Order with id $id not found")
        }

        // Validate order state
        if (order.paymentStatus != PaymentStatus.PAID) {
            throw IllegalStateException("Cannot ship unpaid order ${order.number}")
        }
        if (order.fulfillmentStatus == FulfillmentStatus.FULFILLED) {
            throw IllegalStateException("Order ${order.number} is already shipped")
        }
        if (order.status == OrderStatus.CANCELLED) {
            throw IllegalStateException("Cannot ship cancelled order ${order.number}")
        }

        // Update fulfillment status
        order.fulfillmentStatus = FulfillmentStatus.FULFILLED
        order.trackingUrl = trackingUrl

        val savedOrder = orderRepository.save(order)

        // Log order shipped event
        val trackingInfo = trackingUrl?.let { "Tracking: $it" } ?: "No tracking provided"
        logger.info("Order Shipped - Order: ${order.number}, $trackingInfo")

        return toAdminOrderResponse(savedOrder)
    }

    fun cancelOrder(id: Long): AdminOrderResponse {
        val order = orderRepository.findById(id).orElseThrow {
            NoSuchElementException("Order with id $id not found")
        }

        // Validate order state
        if (order.status == OrderStatus.CANCELLED) {
            throw IllegalStateException("Order ${order.number} is already cancelled")
        }
        if (order.paymentStatus == PaymentStatus.PAID) {
            throw IllegalStateException("Cannot cancel paid order ${order.number}. Refund required.")
        }
        if (order.fulfillmentStatus == FulfillmentStatus.FULFILLED) {
            throw IllegalStateException("Cannot cancel shipped order ${order.number}")
        }

        // Update order status
        order.status = OrderStatus.CANCELLED

        // Release reservations: reservedQty -= qty
        order.items.forEach { orderItem ->
            val variant = variantRepository.findById(orderItem.variantId).orElseThrow {
                IllegalStateException("Variant ${orderItem.variantId} not found for order item ${orderItem.id}")
            }

            // Check if we have enough reserved stock to release
            if (variant.reservedQty < orderItem.qty) {
                logger.warn("Inconsistent reserved stock for variant ${variant.sku}. " +
                    "Expected: ${orderItem.qty}, Reserved: ${variant.reservedQty}")
            }

            // Release reservation
            val updatedVariant = variant.copy(
                reservedQty = maxOf(0, variant.reservedQty - orderItem.qty)
            )
            variantRepository.save(updatedVariant)

            logger.info("Reservation released for variant ${variant.sku}: " +
                "reservedQty ${variant.reservedQty} -> ${updatedVariant.reservedQty}")
        }

        val savedOrder = orderRepository.save(order)

        // Log order cancellation
        logger.info("Order Cancelled - Order: ${order.number}")

        return toAdminOrderResponse(savedOrder)
    }

    private fun toAdminOrderResponse(order: Order): AdminOrderResponse {
        val orderItems = order.items.map { orderItem ->
            val variant = variantRepository.findById(orderItem.variantId).orElse(null)
            val product = variant?.let { productRepository.findById(it.productId).orElse(null) }

            AdminOrderItemResponse(
                id = orderItem.id,
                variantId = orderItem.variantId,
                titleSnapshot = orderItem.titleSnapshot,
                qty = orderItem.qty,
                priceSnapshot = orderItem.priceSnapshot,
                lineTotal = orderItem.priceSnapshot.multiply(BigDecimal(orderItem.qty)),
                sku = variant?.sku,
                productTitle = product?.title
            )
        }

        return AdminOrderResponse(
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
            items = orderItems
        )
    }
}