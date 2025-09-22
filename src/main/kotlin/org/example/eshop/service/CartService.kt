package org.example.eshop.service

import org.example.eshop.entity.Cart
import org.example.eshop.entity.CartItem
import org.example.eshop.entity.Variant
import org.example.eshop.repository.CartRepository
import org.example.eshop.repository.CartItemRepository
import org.example.eshop.repository.VariantRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
@Transactional
class CartService(
    private val cartRepository: CartRepository,
    private val cartItemRepository: CartItemRepository,
    private val variantRepository: VariantRepository,
    private val vatCalculatorService: VatCalculatorService,
    private val shippingCalculatorService: ShippingCalculatorService
) {

    fun createCart(): Cart {
        val cart = Cart()
        return cartRepository.save(cart)
    }

    fun getCartWithItems(cartId: Long): Cart? {
        return cartRepository.findByIdWithItems(cartId)
    }

    fun addItemToCart(cartId: Long, variantId: Long, quantity: Int): Cart {
        val cart = getCartWithItems(cartId) 
            ?: throw IllegalArgumentException("Cart not found with id: $cartId")
        
        val variant = variantRepository.findById(variantId).orElse(null)
            ?: throw IllegalArgumentException("Variant not found with id: $variantId")

        // Validate quantity
        if (quantity <= 0) {
            throw IllegalArgumentException("Quantity must be positive")
        }

        // Check stock availability
        val availableStock = variant.stockQty - variant.reservedQty
        if (quantity > availableStock) {
            throw IllegalArgumentException("Insufficient stock. Available: $availableStock, requested: $quantity")
        }

        // Check if item already exists in cart
        val existingItem = cartItemRepository.findByCartIdAndVariantId(cartId, variantId)
        
        if (existingItem != null) {
            // Update existing item quantity
            val newQuantity = existingItem.qty + quantity
            if (newQuantity > availableStock) {
                throw IllegalArgumentException("Insufficient stock. Available: $availableStock, total requested: $newQuantity")
            }
            existingItem.qty = newQuantity
            cartItemRepository.save(existingItem)
        } else {
            // Create new cart item
            val cartItem = CartItem(
                cartId = cartId,
                variantId = variantId,
                qty = quantity,
                priceSnapshot = variant.price
            )
            cartItemRepository.save(cartItem)
        }

        // Recalculate totals
        recalculateCartTotals(cart)
        return cartRepository.save(cart)
    }

    fun updateItemQuantity(cartId: Long, variantId: Long, quantity: Int): Cart {
        val cart = getCartWithItems(cartId)
            ?: throw IllegalArgumentException("Cart not found with id: $cartId")

        if (quantity < 0) {
            throw IllegalArgumentException("Quantity cannot be negative")
        }

        val existingItem = cartItemRepository.findByCartIdAndVariantId(cartId, variantId)
            ?: throw IllegalArgumentException("Item not found in cart")

        if (quantity == 0) {
            // Remove item if quantity is 0
            cartItemRepository.delete(existingItem)
        } else {
            // Validate stock availability
            val variant = variantRepository.findById(variantId).orElse(null)
                ?: throw IllegalArgumentException("Variant not found with id: $variantId")
            
            val availableStock = variant.stockQty - variant.reservedQty
            if (quantity > availableStock) {
                throw IllegalArgumentException("Insufficient stock. Available: $availableStock, requested: $quantity")
            }

            existingItem.qty = quantity
            cartItemRepository.save(existingItem)
        }

        // Recalculate totals
        recalculateCartTotals(cart)
        return cartRepository.save(cart)
    }

    fun removeItemFromCart(cartId: Long, variantId: Long): Cart {
        val cart = getCartWithItems(cartId)
            ?: throw IllegalArgumentException("Cart not found with id: $cartId")

        cartItemRepository.deleteByCartIdAndVariantId(cartId, variantId)

        // Recalculate totals
        recalculateCartTotals(cart)
        return cartRepository.save(cart)
    }

    fun clearCart(cartId: Long): Cart {
        val cart = getCartWithItems(cartId)
            ?: throw IllegalArgumentException("Cart not found with id: $cartId")

        cartItemRepository.deleteByCartId(cartId)
        
        // Reset totals
        cart.subtotal = BigDecimal.ZERO
        cart.vatAmount = BigDecimal.ZERO
        cart.shippingCost = BigDecimal.ZERO
        cart.total = BigDecimal.ZERO

        return cartRepository.save(cart)
    }

    private fun recalculateCartTotals(cart: Cart) {
        // Reload cart items to get current state
        val cartItems = cartItemRepository.findByCartId(cart.id)
        
        // Calculate subtotal
        cart.subtotal = cartItems.sumOf { it.lineTotal }
        
        // Calculate VAT (assuming VAT-inclusive pricing)
        cart.vatAmount = vatCalculatorService.extractVatAmount(cart.subtotal)
        
        // Calculate shipping (using default domestic zone for now)
        val totalWeightGrams = cartItems.sumOf { item ->
            val variant = variantRepository.findById(item.variantId).orElse(null)
            variant?.shippingWeight?.multiply(BigDecimal(item.qty))?.toInt() ?: 0
        }
        cart.shippingCost = shippingCalculatorService.calculateShippingCost("domestic", totalWeightGrams) ?: BigDecimal.ZERO
        
        // Calculate total
        cart.total = cart.subtotal + cart.shippingCost
    }
}