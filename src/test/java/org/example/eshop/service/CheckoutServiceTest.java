package org.example.eshop.service;

import org.example.eshop.entity.*;
import org.example.eshop.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class CheckoutServiceTest {

    @Mock private CartRepository cartRepository;
    @Mock private CartItemRepository cartItemRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private OrderItemRepository orderItemRepository;
    @Mock private VariantRepository variantRepository;
    @Mock private NotificationService notificationService;

    private CheckoutService checkoutService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        checkoutService = new CheckoutService(
                cartRepository,
                cartItemRepository,
                orderRepository,
                orderItemRepository,
                variantRepository,
                notificationService
        );
    }

    @Test
    void submitCheckout_shouldCreateOrderSuccessfullyWithValidInput() {
        long cartId = 1L;
        String email = "test@example.com";
        String address = "123 Main Street, City, State 12345, Country";

        Variant variant = new Variant(1L, "TEA-001-25G", "Earl Grey 25g", new BigDecimal("25.00"), new BigDecimal("25"), new BigDecimal("30"));
        variant.setStockQty(10);
        variant.setReservedQty(0);
        variant.setLotId(1L);

        CartItem cartItem = new CartItem(cartId, 1L, 2, new BigDecimal("25.00"));

        Cart cart = new Cart();
        cart.setSubtotal(new BigDecimal("50.00"));
        cart.setVatAmount(new BigDecimal("10.00"));
        cart.setShippingCost(new BigDecimal("5.00"));
        cart.setTotal(new BigDecimal("65.00"));
        cart.getItems().add(cartItem);

        Order savedOrder = new Order("ORD-20231201-1234", email, address, cart.getSubtotal(), cart.getVatAmount(), cart.getShippingCost(), cart.getTotal());
        savedOrder.setId(1L);

        when(cartRepository.findByIdWithItems(cartId)).thenReturn(cart);
        when(variantRepository.findById(1L)).thenReturn(Optional.of(variant));
        when(orderRepository.save(ArgumentMatchers.any(Order.class))).thenReturn(savedOrder);
        when(orderItemRepository.save(ArgumentMatchers.any(OrderItem.class))).thenReturn(
                new OrderItem(savedOrder.getId(), 1L, variant.getTitle(), 2, new BigDecimal("25.00"))
        );

        Order result = checkoutService.submitCheckout(cartId, email, address);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
        assertEquals(address, result.getAddress());
        assertEquals(cart.getSubtotal(), result.getSubtotal());
        assertEquals(cart.getVatAmount(), result.getTax());
        assertEquals(cart.getShippingCost(), result.getShipping());
        assertEquals(cart.getTotal(), result.getTotal());
        assertEquals(PaymentStatus.PENDING, result.getPaymentStatus());

        verify(cartRepository).findByIdWithItems(cartId);
        verify(variantRepository, times(2)).findById(1L);
        verify(orderRepository).save(ArgumentMatchers.any(Order.class));
        verify(orderItemRepository).save(ArgumentMatchers.any(OrderItem.class));
        verify(notificationService).logOrderReceived(result);
    }

    @Test
    void submitCheckout_shouldThrowExceptionForInvalidEmail() {
        long cartId = 1L;
        String invalidEmail = "invalid-email";
        String address = "123 Main Street, City, State 12345, Country";

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                checkoutService.submitCheckout(cartId, invalidEmail, address)
        );
        assertEquals("Invalid email format", ex.getMessage());
    }

    @Test
    void submitCheckout_shouldThrowExceptionForBlankEmail() {
        long cartId = 1L;
        String blankEmail = "";
        String address = "123 Main Street, City, State 12345, Country";

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                checkoutService.submitCheckout(cartId, blankEmail, address)
        );
        assertEquals("Email is required", ex.getMessage());
    }

    @Test
    void submitCheckout_shouldThrowExceptionForShortAddress() {
        long cartId = 1L;
        String email = "test@example.com";
        String shortAddress = "Short";

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                checkoutService.submitCheckout(cartId, email, shortAddress)
        );
        assertEquals("Address must be at least 10 characters long", ex.getMessage());
    }

    @Test
    void submitCheckout_shouldThrowExceptionForNonExistentCart() {
        long cartId = 999L;
        String email = "test@example.com";
        String address = "123 Main Street, City, State 12345, Country";

        when(cartRepository.findByIdWithItems(cartId)).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                checkoutService.submitCheckout(cartId, email, address)
        );
        assertEquals("Cart not found with id: " + cartId, ex.getMessage());
    }

    @Test
    void submitCheckout_shouldThrowExceptionForEmptyCart() {
        long cartId = 1L;
        String email = "test@example.com";
        String address = "123 Main Street, City, State 12345, Country";

        Cart emptyCart = new Cart();
        when(cartRepository.findByIdWithItems(cartId)).thenReturn(emptyCart);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                checkoutService.submitCheckout(cartId, email, address)
        );
        assertEquals("Cannot checkout with empty cart", ex.getMessage());
    }

    @Test
    void submitCheckout_shouldThrowExceptionForInsufficientStock() {
        long cartId = 1L;
        String email = "test@example.com";
        String address = "123 Main Street, City, State 12345, Country";

        Variant variant = new Variant(1L, "TEA-001-25G", "Earl Grey 25g", new BigDecimal("25.00"), new BigDecimal("25"), new BigDecimal("30"));
        variant.setStockQty(1);
        variant.setReservedQty(0);
        variant.setLotId(1L);

        CartItem cartItem = new CartItem(cartId, 1L, 5, new BigDecimal("25.00"));
        Cart cart = new Cart();
        cart.getItems().add(cartItem);

        when(cartRepository.findByIdWithItems(cartId)).thenReturn(cart);
        when(variantRepository.findById(1L)).thenReturn(Optional.of(variant));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                checkoutService.submitCheckout(cartId, email, address)
        );
        assertTrue(ex.getMessage().contains("Insufficient stock"));
    }

    @Test
    void getOrderByNumber_shouldReturnOrderWhenFound() {
        String orderNumber = "ORD-20231201-1234";
        Order order = new Order(orderNumber, "test@example.com", "123 Main Street",
                new BigDecimal("50.00"), new BigDecimal("10.00"), new BigDecimal("5.00"), new BigDecimal("65.00"));
        order.setId(1L);

        when(orderRepository.findByNumber(orderNumber)).thenReturn(order);

        Order result = checkoutService.getOrderByNumber(orderNumber);
        assertNotNull(result);
        assertEquals(orderNumber, result.getNumber());
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void getOrderByNumber_shouldReturnNullWhenNotFound() {
        String orderNumber = "NON-EXISTENT";
        when(orderRepository.findByNumber(orderNumber)).thenReturn(null);
        assertNull(checkoutService.getOrderByNumber(orderNumber));
    }
}
