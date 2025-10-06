package org.example.eshop.service;

import org.example.eshop.dto.CheckoutRequest;
import org.example.eshop.entity.Cart;
import org.example.eshop.entity.Order;
import org.example.eshop.entity.OrderItem;
import org.example.eshop.entity.Variant;
import org.example.eshop.repository.CartItemRepository;
import org.example.eshop.repository.CartRepository;
import org.example.eshop.repository.OrderItemRepository;
import org.example.eshop.repository.OrderRepository;
import org.example.eshop.repository.VariantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Transactional
public class CheckoutService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final VariantRepository variantRepository;
    private final NotificationService notificationService;

    public CheckoutService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            VariantRepository variantRepository,
            NotificationService notificationService
    ) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.variantRepository = variantRepository;
        this.notificationService = notificationService;
    }

    public Order submitCheckout(Long cartId, String email, String address) {
        validateCheckoutInput(email, address);

        Cart cart = cartRepository.findByIdWithItems(cartId);
        if (cart == null) {
            throw new IllegalArgumentException("Cart not found with id: " + cartId);
        }
        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cannot checkout with empty cart");
        }

        validateAndReserveInventory(cart);

        String orderNumber = generateOrderNumber();

        Order order = new Order(orderNumber, email, address,
                cart.getSubtotal(), cart.getVatAmount(), cart.getShippingCost(), cart.getTotal());

        Order savedOrder = orderRepository.save(order);

        cart.getItems().forEach(cartItem -> {
            Variant variant = variantRepository.findById(cartItem.getVariantId()).orElse(null);
            if (variant == null) {
                throw new IllegalStateException("Variant not found during checkout: " + cartItem.getVariantId());
            }
            OrderItem orderItem = new OrderItem(savedOrder.getId(), cartItem.getVariantId(),
                    variant.getTitle(), cartItem.getQty(), cartItem.getPriceSnapshot());
            orderItemRepository.save(orderItem);
            savedOrder.addItem(orderItem);
        });

        notificationService.logOrderReceived(savedOrder);

        cart.clearItems();
        cartRepository.save(cart);

        return savedOrder;
    }

    public Order submitCheckout(Long cartId, CheckoutRequest request) {
        validateCheckoutRequest(request);

        Cart cart = cartRepository.findByIdWithItems(cartId);
        if (cart == null) {
            throw new IllegalArgumentException("Cart not found with id: " + cartId);
        }
        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cannot checkout with empty cart");
        }

        validateAndReserveInventory(cart);

        String orderNumber = generateOrderNumber();
        String addressSummary = formatAddress(request);

        Order order = new Order(orderNumber, request.getEmail(), addressSummary,
                cart.getSubtotal(), cart.getVatAmount(), cart.getShippingCost(), cart.getTotal());
        order.setFullName(request.getFullName());
        order.setPhone(request.getPhone());
        order.setStreet1(request.getStreet1());
        order.setStreet2(request.getStreet2());
        order.setCity(request.getCity());
        order.setRegion(request.getRegion());
        order.setPostalCode(request.getPostalCode());
        order.setCountry(request.getCountry());

        Order savedOrder = orderRepository.save(order);

        cart.getItems().forEach(cartItem -> {
            Variant variant = variantRepository.findById(cartItem.getVariantId()).orElse(null);
            if (variant == null) {
                throw new IllegalStateException("Variant not found during checkout: " + cartItem.getVariantId());
            }
            OrderItem orderItem = new OrderItem(savedOrder.getId(), cartItem.getVariantId(),
                    variant.getTitle(), cartItem.getQty(), cartItem.getPriceSnapshot());
            orderItemRepository.save(orderItem);
            savedOrder.addItem(orderItem);
        });

        notificationService.logOrderReceived(savedOrder);

        cart.clearItems();
        cartRepository.save(cart);

        return savedOrder;
    }

    public Order getOrderByNumber(String orderNumber) {
        return orderRepository.findByNumber(orderNumber);
    }

    private void validateCheckoutInput(String email, String address) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Address is required");
        }
        if (address.length() < 10) {
            throw new IllegalArgumentException("Address must be at least 10 characters long");
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$";
        return email.matches(emailRegex);
    }

    private void validateAndReserveInventory(Cart cart) {
        cart.getItems().forEach(cartItem -> {
            Variant variant = variantRepository.findById(cartItem.getVariantId()).orElse(null);
            if (variant == null) {
                throw new IllegalStateException("Variant not found: " + cartItem.getVariantId());
            }
            int availableStock = variant.getStockQty() - variant.getReservedQty();
            if (cartItem.getQty() > availableStock) {
                throw new IllegalArgumentException("Insufficient stock for " + variant.getTitle() + ". Available: " + availableStock + ", requested: " + cartItem.getQty());
            }
            variant.setReservedQty(variant.getReservedQty() + cartItem.getQty());
            variantRepository.save(variant);
        });
    }

    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "ORD-" + timestamp + '-' + random;
    }

    private void validateCheckoutRequest(CheckoutRequest request) {
        if (request.getFullName() == null || request.getFullName().isBlank()) throw new IllegalArgumentException("Full name is required");
        if (request.getEmail() == null || request.getEmail().isBlank()) throw new IllegalArgumentException("Email is required");
        if (!isValidEmail(request.getEmail())) throw new IllegalArgumentException("Invalid email format");
        if (request.getStreet1() == null || request.getStreet1().isBlank()) throw new IllegalArgumentException("Street address is required");
        if (request.getCity() == null || request.getCity().isBlank()) throw new IllegalArgumentException("City is required");
        if (request.getRegion() == null || request.getRegion().isBlank()) throw new IllegalArgumentException("Region/State is required");
        if (request.getPostalCode() == null || request.getPostalCode().isBlank()) throw new IllegalArgumentException("Postal code is required");
        if (request.getCountry() == null || request.getCountry().isBlank()) throw new IllegalArgumentException("Country is required");
    }

    private String formatAddress(CheckoutRequest request) {
        String street = (request.getStreet2() != null && !request.getStreet2().isBlank())
                ? request.getStreet1() + ", " + request.getStreet2()
                : request.getStreet1();
        return street + ", " + request.getCity() + ", " + request.getRegion() + ' ' + request.getPostalCode() + ", " + request.getCountry();
    }
}
