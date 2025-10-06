package org.example.eshop.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.example.eshop.service.CartService;
import org.example.eshop.service.CheckoutService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CheckoutViewController {

    private final CartService cartService;
    private final CheckoutService checkoutService;

    public CheckoutViewController(CartService cartService, CheckoutService checkoutService) {
        this.cartService = cartService;
        this.checkoutService = checkoutService;
    }

    @GetMapping("/checkout")
    public String checkoutPage(HttpServletRequest request, Model model) {
        Long cartId = getCartIdFromCookies(request);
        var cart = cartId != null ? cartService.getCartWithItemsOrNull(cartId) : null;
        model.addAttribute("cart", cart);
        return "checkout";
    }

    @GetMapping("/order-confirmation")
    public String orderConfirmationPage(@RequestParam(required = false) String orderNumber, Model model) {
        if (orderNumber == null || orderNumber.isBlank()) {
            model.addAttribute("order", null);
            return "order-confirmation";
        }
        var order = checkoutService.getOrderByNumber(orderNumber);
        model.addAttribute("order", order);
        return "order-confirmation";
    }

    private Long getCartIdFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if ("cartId".equals(c.getName())) {
                try {
                    return Long.valueOf(c.getValue());
                } catch (NumberFormatException ignored) {
                    return null;
                }
            }
        }
        return null;
    }
}
