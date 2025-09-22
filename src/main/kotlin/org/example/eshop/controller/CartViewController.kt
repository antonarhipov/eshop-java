package org.example.eshop.controller

import org.example.eshop.dto.CartDto
import org.example.eshop.dto.CartItemDto
import org.example.eshop.dto.VariantSummaryDto
import org.example.eshop.entity.Cart
import org.example.eshop.entity.CartItem
import org.example.eshop.repository.ProductRepository
import org.example.eshop.repository.VariantRepository
import org.example.eshop.service.CartService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import jakarta.servlet.http.HttpServletRequest

@Controller
class CartViewController(
    private val cartService: CartService,
    private val variantRepository: VariantRepository,
    private val productRepository: ProductRepository
) {

    @GetMapping("/cart")
    fun showCart(
        request: HttpServletRequest,
        model: Model,
        @RequestParam(required = false) error: String?
    ): String {
        try {
            val cartId = getCartIdFromCookies(request)
            
            if (cartId != null) {
                val cart = cartService.getCartWithItems(cartId)
                if (cart != null) {
                    model.addAttribute("cart", mapCartToDto(cart))
                } else {
                    model.addAttribute("cart", null)
                }
            } else {
                model.addAttribute("cart", null)
            }
            
            if (error != null) {
                model.addAttribute("error", error)
            }
            
        } catch (e: Exception) {
            model.addAttribute("error", "Failed to load cart: ${e.message}")
            model.addAttribute("cart", null)
        }
        
        return "cart"
    }

    @GetMapping("/cart/{cartId}")
    fun showCartById(
        @PathVariable cartId: Long,
        model: Model,
        @RequestParam(required = false) error: String?
    ): String {
        try {
            val cart = cartService.getCartWithItems(cartId)
            if (cart != null) {
                model.addAttribute("cart", mapCartToDto(cart))
            } else {
                model.addAttribute("error", "Cart not found")
                model.addAttribute("cart", null)
            }
            
            if (error != null) {
                model.addAttribute("error", error)
            }
            
        } catch (e: Exception) {
            model.addAttribute("error", "Failed to load cart: ${e.message}")
            model.addAttribute("cart", null)
        }
        
        return "cart"
    }

    private fun getCartIdFromCookies(request: HttpServletRequest): Long? {
        return request.cookies?.find { it.name == "cartId" }?.value?.toLongOrNull()
    }

    private fun mapCartToDto(cart: Cart): CartDto {
        return CartDto(
            id = cart.id,
            subtotal = cart.subtotal,
            vatAmount = cart.vatAmount,
            shippingCost = cart.shippingCost,
            total = cart.total,
            createdAt = cart.createdAt,
            updatedAt = cart.updatedAt,
            items = cart.items.map { mapCartItemToDto(it) }
        )
    }

    private fun mapCartItemToDto(cartItem: CartItem): CartItemDto {
        val variant = variantRepository.findById(cartItem.variantId).orElse(null)
        val product = variant?.let { productRepository.findById(it.productId).orElse(null) }
        
        return CartItemDto(
            id = cartItem.id,
            variantId = cartItem.variantId,
            qty = cartItem.qty,
            priceSnapshot = cartItem.priceSnapshot,
            lineTotal = cartItem.lineTotal,
            variant = variant?.let { v ->
                VariantSummaryDto(
                    id = v.id,
                    sku = v.sku,
                    title = v.title,
                    price = v.price,
                    stockQty = v.stockQty,
                    reservedQty = v.reservedQty,
                    productTitle = product?.title ?: "Unknown Product"
                )
            }
        )
    }
}