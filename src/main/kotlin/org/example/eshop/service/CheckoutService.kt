package org.example.eshop.service

import org.example.eshop.entity.*
import org.example.eshop.repository.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.ThreadLocalRandom

@Service
@Transactional
class CheckoutService(
    private val cartRepository: CartRepository,
    private val cartItemRepository: CartItemRepository,
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val variantRepository: VariantRepository,
    private val notificationService: NotificationService
) {

    fun submitCheckout(cartId: Long, email: String, address: String): Order {
        // Validate input
        validateCheckoutInput(email, address)
        
        // Get cart with items
        val cart = cartRepository.findByIdWithItems(cartId)
            ?: throw IllegalArgumentException("Cart not found with id: $cartId")
        
        if (cart.items.isEmpty()) {
            throw IllegalArgumentException("Cannot checkout with empty cart")
        }
        
        // Validate stock availability and reserve inventory
        validateAndReserveInventory(cart)
        
        // Generate order number
        val orderNumber = generateOrderNumber()
        
        // Create order
        val order = Order(
            number = orderNumber,
            email = email,
            address = address,
            subtotal = cart.subtotal,
            tax = cart.vatAmount,
            shipping = cart.shippingCost,
            total = cart.total,
            paymentStatus = PaymentStatus.PENDING
        )
        
        val savedOrder = orderRepository.save(order)
        
        // Create order items
        cart.items.forEach { cartItem ->
            val variant = variantRepository.findById(cartItem.variantId).orElse(null)
                ?: throw IllegalStateException("Variant not found during checkout: ${cartItem.variantId}")
            
            val orderItem = OrderItem(
                orderId = savedOrder.id,
                variantId = cartItem.variantId,
                titleSnapshot = variant.title,
                qty = cartItem.qty,
                priceSnapshot = cartItem.priceSnapshot
            )
            orderItemRepository.save(orderItem)
            savedOrder.addItem(orderItem)
        }
        
        // Log order confirmation event
        notificationService.logOrderReceived(savedOrder)
        
        return savedOrder
    }
    
    fun getOrderByNumber(orderNumber: String): Order? {
        return orderRepository.findByNumber(orderNumber)
    }
    
    private fun validateCheckoutInput(email: String, address: String) {
        if (email.isBlank()) {
            throw IllegalArgumentException("Email is required")
        }
        
        if (!isValidEmail(email)) {
            throw IllegalArgumentException("Invalid email format")
        }
        
        if (address.isBlank()) {
            throw IllegalArgumentException("Address is required")
        }
        
        if (address.length < 10) {
            throw IllegalArgumentException("Address must be at least 10 characters long")
        }
    }
    
    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"
        return email.matches(emailRegex.toRegex())
    }
    
    private fun validateAndReserveInventory(cart: Cart) {
        cart.items.forEach { cartItem ->
            val variant = variantRepository.findById(cartItem.variantId).orElse(null)
                ?: throw IllegalStateException("Variant not found: ${cartItem.variantId}")
            
            // Check stock availability
            val availableStock = variant.stockQty - variant.reservedQty
            if (cartItem.qty > availableStock) {
                throw IllegalArgumentException("Insufficient stock for ${variant.title}. Available: $availableStock, requested: ${cartItem.qty}")
            }
            
            // Reserve inventory
            variant.reservedQty += cartItem.qty
            variantRepository.save(variant)
        }
    }
    
    private fun generateOrderNumber(): String {
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
        val random = ThreadLocalRandom.current().nextInt(1000, 9999)
        return "ORD-$timestamp-$random"
    }
}