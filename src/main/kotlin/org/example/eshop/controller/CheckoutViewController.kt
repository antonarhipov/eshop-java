package org.example.eshop.controller

import jakarta.servlet.http.HttpServletRequest
import org.example.eshop.dto.OrderDto
import org.example.eshop.service.CartService
import org.example.eshop.service.CheckoutService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class CheckoutViewController(
    private val cartService: CartService,
    private val checkoutService: CheckoutService
) {

    @GetMapping("/checkout")
    fun checkoutPage(request: HttpServletRequest, model: Model): String {
        val cartId = request.cookies?.find { it.name == "cartId" }?.value?.toLongOrNull()
        val cart = cartId?.let { cartService.getCartWithItems(it) }
        model.addAttribute("cart", cart)
        return "checkout"
    }

    @GetMapping("/order-confirmation")
    fun orderConfirmationPage(
        @RequestParam orderNumber: String?,
        model: Model
    ): String {
        if (orderNumber.isNullOrBlank()) {
            model.addAttribute("order", null)
            return "order-confirmation"
        }
        val order = checkoutService.getOrderByNumber(orderNumber)
        model.addAttribute("order", order)
        return "order-confirmation"
    }
}