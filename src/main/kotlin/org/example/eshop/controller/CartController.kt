package org.example.eshop.controller

import org.example.eshop.dto.*
import org.example.eshop.entity.Cart
import org.example.eshop.entity.CartItem
import org.example.eshop.repository.ProductRepository
import org.example.eshop.repository.VariantRepository
import org.example.eshop.service.CartService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/api/cart")
class CartController(
    private val cartService: CartService,
    private val variantRepository: VariantRepository,
    private val productRepository: ProductRepository
) {

    @PostMapping
    fun createCart(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ResponseEntity<CartOperationResponse> {
        return try {
            // Check if cart already exists in session
            val existingCartId = getCartIdFromCookies(request)
            if (existingCartId != null) {
                val existingCart = cartService.getCartWithItems(existingCartId)
                if (existingCart != null) {
                    return ResponseEntity.ok(
                        CartOperationResponse(
                            success = true,
                            message = "Cart already exists",
                            cart = mapCartToDto(existingCart)
                        )
                    )
                }
            }

            // Create new cart
            val cart = cartService.createCart()
            
            // Set cart ID in cookie
            val cookie = Cookie("cartId", cart.id.toString()).apply {
                maxAge = 7 * 24 * 60 * 60 // 7 days
                path = "/"
                isHttpOnly = true
            }
            response.addCookie(cookie)

            ResponseEntity.status(HttpStatus.CREATED).body(
                CartOperationResponse(
                    success = true,
                    message = "Cart created successfully",
                    cart = mapCartToDto(cart)
                )
            )
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                CartOperationResponse(
                    success = false,
                    message = "Failed to create cart",
                    errors = listOf(e.message ?: "Unknown error")
                )
            )
        }
    }

    @GetMapping("/{cartId}")
    fun getCart(@PathVariable cartId: Long): ResponseEntity<CartOperationResponse> {
        return try {
            val cart = cartService.getCartWithItems(cartId)
            if (cart != null) {
                ResponseEntity.ok(
                    CartOperationResponse(
                        success = true,
                        cart = mapCartToDto(cart)
                    )
                )
            } else {
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    CartOperationResponse(
                        success = false,
                        message = "Cart not found",
                        errors = listOf("Cart with id $cartId not found")
                    )
                )
            }
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                CartOperationResponse(
                    success = false,
                    message = "Failed to retrieve cart",
                    errors = listOf(e.message ?: "Unknown error")
                )
            )
        }
    }

    @PatchMapping("/{cartId}")
    fun updateCart(
        @PathVariable cartId: Long,
        @RequestBody request: UpdateCartItemRequest
    ): ResponseEntity<CartOperationResponse> {
        return try {
            val cart = when {
                request.quantity == 0 -> {
                    // Remove item from cart
                    cartService.removeItemFromCart(cartId, request.variantId)
                }
                request.quantity > 0 -> {
                    // Update item quantity or add new item
                    val existingCart = cartService.getCartWithItems(cartId)
                    if (existingCart?.items?.any { it.variantId == request.variantId } == true) {
                        cartService.updateItemQuantity(cartId, request.variantId, request.quantity)
                    } else {
                        cartService.addItemToCart(cartId, request.variantId, request.quantity)
                    }
                }
                else -> {
                    return ResponseEntity.badRequest().body(
                        CartOperationResponse(
                            success = false,
                            message = "Invalid quantity",
                            errors = listOf("Quantity cannot be negative")
                        )
                    )
                }
            }

            ResponseEntity.ok(
                CartOperationResponse(
                    success = true,
                    message = "Cart updated successfully",
                    cart = mapCartToDto(cart)
                )
            )
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(
                CartOperationResponse(
                    success = false,
                    message = "Invalid request",
                    errors = listOf(e.message ?: "Invalid request")
                )
            )
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                CartOperationResponse(
                    success = false,
                    message = "Failed to update cart",
                    errors = listOf(e.message ?: "Unknown error")
                )
            )
        }
    }

    @PostMapping("/{cartId}/items")
    fun addItemToCart(
        @PathVariable cartId: Long,
        @RequestBody request: AddToCartRequest
    ): ResponseEntity<CartOperationResponse> {
        return try {
            val cart = cartService.addItemToCart(cartId, request.variantId, request.quantity)
            
            ResponseEntity.ok(
                CartOperationResponse(
                    success = true,
                    message = "Item added to cart successfully",
                    cart = mapCartToDto(cart)
                )
            )
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(
                CartOperationResponse(
                    success = false,
                    message = "Invalid request",
                    errors = listOf(e.message ?: "Invalid request")
                )
            )
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                CartOperationResponse(
                    success = false,
                    message = "Failed to add item to cart",
                    errors = listOf(e.message ?: "Unknown error")
                )
            )
        }
    }

    @DeleteMapping("/{cartId}/items/{variantId}")
    fun removeItemFromCart(
        @PathVariable cartId: Long,
        @PathVariable variantId: Long
    ): ResponseEntity<CartOperationResponse> {
        return try {
            val cart = cartService.removeItemFromCart(cartId, variantId)
            
            ResponseEntity.ok(
                CartOperationResponse(
                    success = true,
                    message = "Item removed from cart successfully",
                    cart = mapCartToDto(cart)
                )
            )
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(
                CartOperationResponse(
                    success = false,
                    message = "Invalid request",
                    errors = listOf(e.message ?: "Invalid request")
                )
            )
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                CartOperationResponse(
                    success = false,
                    message = "Failed to remove item from cart",
                    errors = listOf(e.message ?: "Unknown error")
                )
            )
        }
    }

    @DeleteMapping("/{cartId}")
    fun clearCart(@PathVariable cartId: Long): ResponseEntity<CartOperationResponse> {
        return try {
            val cart = cartService.clearCart(cartId)
            
            ResponseEntity.ok(
                CartOperationResponse(
                    success = true,
                    message = "Cart cleared successfully",
                    cart = mapCartToDto(cart)
                )
            )
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(
                CartOperationResponse(
                    success = false,
                    message = "Invalid request",
                    errors = listOf(e.message ?: "Invalid request")
                )
            )
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                CartOperationResponse(
                    success = false,
                    message = "Failed to clear cart",
                    errors = listOf(e.message ?: "Unknown error")
                )
            )
        }
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