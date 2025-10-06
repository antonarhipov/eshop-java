package org.example.eshop.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.example.eshop.dto.CartDto;
import org.example.eshop.dto.CartItemDto;
import org.example.eshop.dto.VariantSummaryDto;
import org.example.eshop.entity.Cart;
import org.example.eshop.entity.CartItem;
import org.example.eshop.repository.ProductRepository;
import org.example.eshop.repository.VariantRepository;
import org.example.eshop.service.CartService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CartViewController {

    private final CartService cartService;
    private final VariantRepository variantRepository;
    private final ProductRepository productRepository;

    public CartViewController(CartService cartService, VariantRepository variantRepository, ProductRepository productRepository) {
        this.cartService = cartService;
        this.variantRepository = variantRepository;
        this.productRepository = productRepository;
    }

    @GetMapping("/cart")
    public String showCart(HttpServletRequest request, Model model, @RequestParam(required = false) String error) {
        try {
            Long cartId = getCartIdFromCookies(request);
            if (cartId != null) {
                Cart cart = cartService.getCartWithItemsOrNull(cartId);
                if (cart != null) {
                    model.addAttribute("cart", mapCartToDto(cart));
                } else {
                    model.addAttribute("cart", null);
                }
            } else {
                model.addAttribute("cart", null);
            }

            if (error != null) {
                model.addAttribute("error", error);
            }
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load cart: " + e.getMessage());
            model.addAttribute("cart", null);
        }
        return "cart";
    }

    @GetMapping("/cart/{cartId}")
    public String showCartById(@PathVariable Long cartId, Model model, @RequestParam(required = false) String error) {
        try {
            Cart cart = cartService.getCartWithItemsOrNull(cartId);
            if (cart != null) {
                model.addAttribute("cart", mapCartToDto(cart));
            } else {
                model.addAttribute("error", "Cart not found");
                model.addAttribute("cart", null);
            }

            if (error != null) {
                model.addAttribute("error", error);
            }
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load cart: " + e.getMessage());
            model.addAttribute("cart", null);
        }
        return "cart";
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

    private CartDto mapCartToDto(Cart cart) {
        return new CartDto(
                cart.getId(),
                cart.getSubtotal(),
                cart.getVatAmount(),
                cart.getShippingCost(),
                cart.getTotal(),
                cart.getCreatedAt(),
                cart.getUpdatedAt(),
                cart.getItems().stream().map(this::mapCartItemToDto).toList()
        );
    }

    private CartItemDto mapCartItemToDto(CartItem cartItem) {
        var variant = variantRepository.findById(cartItem.getVariantId()).orElse(null);
        var product = variant != null ? productRepository.findById(variant.getProductId()).orElse(null) : null;

        return new CartItemDto(
                cartItem.getId(),
                cartItem.getVariantId(),
                cartItem.getQty(),
                cartItem.getPriceSnapshot(),
                cartItem.getLineTotal(),
                variant == null ? null : new VariantSummaryDto(
                        variant.getId(),
                        variant.getSku(),
                        variant.getTitle(),
                        variant.getPrice(),
                        variant.getStockQty(),
                        variant.getReservedQty(),
                        product != null ? product.getTitle() : "Unknown Product"
                )
        );
    }
}
