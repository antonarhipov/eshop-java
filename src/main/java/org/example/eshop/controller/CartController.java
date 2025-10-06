package org.example.eshop.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.eshop.dto.AddToCartRequest;
import org.example.eshop.dto.CartDto;
import org.example.eshop.dto.CartOperationResponse;
import org.example.eshop.dto.UpdateCartItemRequest;
import org.example.eshop.dto.CartItemDto;
import org.example.eshop.dto.VariantSummaryDto;
import org.example.eshop.entity.Cart;
import org.example.eshop.entity.CartItem;
import org.example.eshop.entity.Product;
import org.example.eshop.entity.Variant;
import org.example.eshop.repository.ProductRepository;
import org.example.eshop.repository.VariantRepository;
import org.example.eshop.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final VariantRepository variantRepository;
    private final ProductRepository productRepository;

    public CartController(CartService cartService,
                          VariantRepository variantRepository,
                          ProductRepository productRepository) {
        this.cartService = Objects.requireNonNull(cartService);
        this.variantRepository = Objects.requireNonNull(variantRepository);
        this.productRepository = Objects.requireNonNull(productRepository);
    }

    @PostMapping
    public ResponseEntity<CartOperationResponse> createCart(HttpServletRequest request,
                                                            HttpServletResponse response) {
        try {
            Long existingCartId = getCartIdFromCookies(request);
            if (existingCartId != null) {
                Cart existingCart = cartService.getCartWithItems(existingCartId);
                if (existingCart != null) {
                    return ResponseEntity.ok(
                            new CartOperationResponse(true, "Cart already exists", mapCartToDto(existingCart), null)
                    );
                }
            }

            Cart cart = cartService.createCart();

            Cookie cookie = new Cookie("cartId", String.valueOf(cart.getId()));
            cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            response.addCookie(cookie);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new CartOperationResponse(true, "Cart created successfully", mapCartToDto(cart), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CartOperationResponse(false, "Failed to create cart", null, List.of(e.getMessage() != null ? e.getMessage() : "Unknown error")));
        }
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<CartOperationResponse> getCart(@PathVariable long cartId) {
        try {
            Cart cart = cartService.getCartWithItems(cartId);
            if (cart != null) {
                return ResponseEntity.ok(new CartOperationResponse(true, null, mapCartToDto(cart), null));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new CartOperationResponse(false, "Cart not found", null, List.of("Cart with id " + cartId + " not found")));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CartOperationResponse(false, "Failed to retrieve cart", null, List.of(e.getMessage() != null ? e.getMessage() : "Unknown error")));
        }
    }

    @PatchMapping("/{cartId}")
    public ResponseEntity<CartOperationResponse> updateCart(@PathVariable long cartId,
                                                            @RequestBody UpdateCartItemRequest request) {
        try {
            Cart cart;
            if (request.getQuantity() == 0) {
                cart = cartService.removeItemFromCart(cartId, request.getVariantId());
            } else if (request.getQuantity() > 0) {
                Cart existingCart = cartService.getCartWithItems(cartId);
                boolean itemExists = existingCart != null && existingCart.getItems() != null &&
                        existingCart.getItems().stream().anyMatch(i -> Objects.equals(i.getVariantId(), request.getVariantId()));
                if (itemExists) {
                    cart = cartService.updateItemQuantity(cartId, request.getVariantId(), request.getQuantity());
                } else {
                    cart = cartService.addItemToCart(cartId, request.getVariantId(), request.getQuantity());
                }
            } else {
                return ResponseEntity.badRequest()
                        .body(new CartOperationResponse(false, "Invalid quantity", null, List.of("Quantity cannot be negative")));
            }

            return ResponseEntity.ok(new CartOperationResponse(true, "Cart updated successfully", mapCartToDto(cart), null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new CartOperationResponse(false, "Invalid request", null, List.of(e.getMessage() != null ? e.getMessage() : "Invalid request")));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CartOperationResponse(false, "Failed to update cart", null, List.of(e.getMessage() != null ? e.getMessage() : "Unknown error")));
        }
    }

    @PostMapping("/{cartId}/items")
    public ResponseEntity<CartOperationResponse> addItemToCart(@PathVariable long cartId,
                                                                @RequestBody AddToCartRequest request) {
        try {
            Cart cart = cartService.addItemToCart(cartId, request.getVariantId(), request.getQuantity());
            return ResponseEntity.ok(new CartOperationResponse(true, "Item added to cart successfully", mapCartToDto(cart), null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new CartOperationResponse(false, "Invalid request", null, List.of(e.getMessage() != null ? e.getMessage() : "Invalid request")));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CartOperationResponse(false, "Failed to add item to cart", null, List.of(e.getMessage() != null ? e.getMessage() : "Unknown error")));
        }
    }

    @DeleteMapping("/{cartId}/items/{variantId}")
    public ResponseEntity<CartOperationResponse> removeItemFromCart(@PathVariable long cartId,
                                                                     @PathVariable long variantId) {
        try {
            Cart cart = cartService.removeItemFromCart(cartId, variantId);
            return ResponseEntity.ok(new CartOperationResponse(true, "Item removed from cart successfully", mapCartToDto(cart), null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new CartOperationResponse(false, "Invalid request", null, List.of(e.getMessage() != null ? e.getMessage() : "Invalid request")));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CartOperationResponse(false, "Failed to remove item from cart", null, List.of(e.getMessage() != null ? e.getMessage() : "Unknown error")));
        }
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<CartOperationResponse> clearCart(@PathVariable long cartId) {
        try {
            Cart cart = cartService.clearCart(cartId);
            return ResponseEntity.ok(new CartOperationResponse(true, "Cart cleared successfully", mapCartToDto(cart), null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new CartOperationResponse(false, "Invalid request", null, List.of(e.getMessage() != null ? e.getMessage() : "Invalid request")));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CartOperationResponse(false, "Failed to clear cart", null, List.of(e.getMessage() != null ? e.getMessage() : "Unknown error")));
        }
    }

    private Long getCartIdFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie c : request.getCookies()) {
            if ("cartId".equals(c.getName())) {
                try {
                    return Long.parseLong(c.getValue());
                } catch (NumberFormatException ignored) {
                    return null;
                }
            }
        }
        return null;
    }

    private CartDto mapCartToDto(Cart cart) {
        List<CartItemDto> itemDtos = cart.getItems().stream()
                .map(this::mapCartItemToDto)
                .collect(Collectors.toList());

        return new CartDto(
                cart.getId(),
                cart.getSubtotal(),
                cart.getVatAmount(),
                cart.getShippingCost(),
                cart.getTotal(),
                cart.getCreatedAt(),
                cart.getUpdatedAt(),
                itemDtos
        );
    }

    private CartItemDto mapCartItemToDto(CartItem cartItem) {
        VariantSummaryDto variantSummary = null;
        Optional<Variant> variantOpt = variantRepository.findById(cartItem.getVariantId());
        if (variantOpt.isPresent()) {
            Variant v = variantOpt.get();
            String productTitle = productRepository.findById(v.getProductId())
                    .map(Product::getTitle)
                    .orElse("Unknown Product");
            variantSummary = new VariantSummaryDto(
                    v.getId(),
                    v.getSku(),
                    v.getTitle(),
                    v.getPrice(),
                    v.getStockQty(),
                    v.getReservedQty(),
                    productTitle
            );
        }

        return new CartItemDto(
                cartItem.getId(),
                cartItem.getVariantId(),
                cartItem.getQty(),
                cartItem.getPriceSnapshot(),
                cartItem.getLineTotal(),
                variantSummary
        );
    }
}
