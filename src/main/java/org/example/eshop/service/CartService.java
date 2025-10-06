package org.example.eshop.service;

import org.example.eshop.entity.Cart;
import org.example.eshop.entity.CartItem;
import org.example.eshop.entity.Variant;
import org.example.eshop.repository.CartItemRepository;
import org.example.eshop.repository.CartRepository;
import org.example.eshop.repository.VariantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final VariantRepository variantRepository;
    private final VatCalculatorService vatCalculatorService;
    private final ShippingCalculatorService shippingCalculatorService;

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            VariantRepository variantRepository,
            VatCalculatorService vatCalculatorService,
            ShippingCalculatorService shippingCalculatorService
    ) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.variantRepository = variantRepository;
        this.vatCalculatorService = vatCalculatorService;
        this.shippingCalculatorService = shippingCalculatorService;
    }

    public Cart createCart() {
        Cart cart = new Cart();
        return cartRepository.save(cart);
    }

    public Cart getCartWithItemsOrNull(Long cartId) {
        return cartRepository.findByIdWithItems(cartId);
    }

    public Cart getCartWithItems(Long cartId) {
        return cartRepository.findByIdWithItems(cartId);
    }


    public Cart addItemToCart(Long cartId, Long variantId, int quantity) {
        Cart cart = Optional.ofNullable(getCartWithItemsOrNull(cartId))
                .orElseThrow(() -> new IllegalArgumentException("Cart not found with id: " + cartId));

        Variant variant = variantRepository.findById(variantId).orElse(null);
        if (variant == null) {
            throw new IllegalArgumentException("Variant not found with id: " + variantId);
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        int availableStock = variant.getStockQty() - variant.getReservedQty();
        if (quantity > availableStock) {
            throw new IllegalArgumentException("Insufficient stock. Available: " + availableStock + ", requested: " + quantity);
        }

        CartItem existingItem = cartItemRepository.findByCartIdAndVariantId(cartId, variantId);
        if (existingItem != null) {
            int newQuantity = existingItem.getQty() + quantity;
            if (newQuantity > availableStock) {
                throw new IllegalArgumentException("Insufficient stock. Available: " + availableStock + ", total requested: " + newQuantity);
            }
            existingItem.setQty(newQuantity);
            cartItemRepository.save(existingItem);
        } else {
            CartItem cartItem = new CartItem(cartId, variantId, quantity, variant.getPrice());
            cartItemRepository.save(cartItem);
        }

        recalculateCartTotals(cart);
        return cartRepository.save(cart);
    }

    public Cart updateItemQuantity(Long cartId, Long variantId, int quantity) {
        Cart cart = Optional.ofNullable(getCartWithItemsOrNull(cartId))
                .orElseThrow(() -> new IllegalArgumentException("Cart not found with id: " + cartId));

        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        CartItem existingItem = Optional.ofNullable(cartItemRepository.findByCartIdAndVariantId(cartId, variantId))
                .orElseThrow(() -> new IllegalArgumentException("Item not found in cart"));

        if (quantity == 0) {
            cartItemRepository.delete(existingItem);
        } else {
            Variant variant = variantRepository.findById(variantId).orElse(null);
            if (variant == null) {
                throw new IllegalArgumentException("Variant not found with id: " + variantId);
            }
            int availableStock = variant.getStockQty() - variant.getReservedQty();
            if (quantity > availableStock) {
                throw new IllegalArgumentException("Insufficient stock. Available: " + availableStock + ", requested: " + quantity);
            }
            existingItem.setQty(quantity);
            cartItemRepository.save(existingItem);
        }

        recalculateCartTotals(cart);
        return cartRepository.save(cart);
    }

    public Cart removeItemFromCart(Long cartId, Long variantId) {
        Cart cart = Optional.ofNullable(getCartWithItemsOrNull(cartId))
                .orElseThrow(() -> new IllegalArgumentException("Cart not found with id: " + cartId));

        cartItemRepository.deleteByCartIdAndVariantId(cartId, variantId);

        recalculateCartTotals(cart);
        return cartRepository.save(cart);
    }

    public Cart clearCart(Long cartId) {
        Cart cart = Optional.ofNullable(getCartWithItemsOrNull(cartId))
                .orElseThrow(() -> new IllegalArgumentException("Cart not found with id: " + cartId));

        cartItemRepository.deleteByCartId(cartId);

        cart.setSubtotal(BigDecimal.ZERO);
        cart.setVatAmount(BigDecimal.ZERO);
        cart.setShippingCost(BigDecimal.ZERO);
        cart.setTotal(BigDecimal.ZERO);

        return cartRepository.save(cart);
    }

    private void recalculateCartTotals(Cart cart) {
        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());

        BigDecimal subtotal = cartItems.stream()
                .map(CartItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setSubtotal(subtotal);

        cart.setVatAmount(vatCalculatorService.extractVatAmount(subtotal));

        int totalWeightGrams = cartItems.stream().mapToInt(item -> {
            Variant variant = variantRepository.findById(item.getVariantId()).orElse(null);
            if (variant == null || variant.getShippingWeight() == null) return 0;
            BigDecimal weight = variant.getShippingWeight().multiply(BigDecimal.valueOf(item.getQty()));
            try {
                return weight.intValueExact();
            } catch (ArithmeticException e) {
                return weight.intValue();
            }
        }).sum();

        BigDecimal shipping = Optional.ofNullable(shippingCalculatorService.calculateShippingCost("domestic", totalWeightGrams))
                .orElse(BigDecimal.ZERO);
        cart.setShippingCost(shipping);

        cart.setTotal(cart.getSubtotal().add(cart.getShippingCost()));
    }
}
