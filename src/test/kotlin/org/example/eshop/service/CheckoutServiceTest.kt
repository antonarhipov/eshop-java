package org.example.eshop.service

import org.example.eshop.entity.*
import org.example.eshop.repository.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.ArgumentMatchers
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.util.*

@SpringBootTest
@ActiveProfiles("test")
class CheckoutServiceTest {

    @Mock
    private lateinit var cartRepository: CartRepository
    
    @Mock
    private lateinit var cartItemRepository: CartItemRepository
    
    @Mock
    private lateinit var orderRepository: OrderRepository
    
    @Mock
    private lateinit var orderItemRepository: OrderItemRepository
    
    @Mock
    private lateinit var variantRepository: VariantRepository
    
    @Mock
    private lateinit var notificationService: NotificationService

    private lateinit var checkoutService: CheckoutService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        checkoutService = CheckoutService(
            cartRepository,
            cartItemRepository,
            orderRepository,
            orderItemRepository,
            variantRepository,
            notificationService
        )
    }

    @Test
    fun `submitCheckout should create order successfully with valid input`() {
        // Given
        val cartId = 1L
        val email = "test@example.com"
        val address = "123 Main Street, City, State 12345, Country"
        
        val variant = Variant(
            id = 1L,
            productId = 1L,
            sku = "TEA-001-25G",
            title = "Earl Grey 25g",
            price = BigDecimal("25.00"),
            weight = BigDecimal("25"),
            shippingWeight = BigDecimal("30"),
            stockQty = 10,
            reservedQty = 0,
            lotId = 1L
        )
        
        val cartItem = CartItem(
            id = 1L,
            cartId = cartId,
            variantId = 1L,
            qty = 2,
            priceSnapshot = BigDecimal("25.00")
        )
        
        val cart = Cart(
            id = cartId,
            subtotal = BigDecimal("50.00"),
            vatAmount = BigDecimal("10.00"),
            shippingCost = BigDecimal("5.00"),
            total = BigDecimal("65.00")
        )
        cart.items.add(cartItem)
        
        val savedOrder = Order(
            id = 1L,
            number = "ORD-20231201-1234",
            email = email,
            address = address,
            subtotal = cart.subtotal,
            tax = cart.vatAmount,
            shipping = cart.shippingCost,
            total = cart.total,
            paymentStatus = PaymentStatus.PENDING
        )

        // When
        `when`(cartRepository.findByIdWithItems(cartId)).thenReturn(cart)
        `when`(variantRepository.findById(1L)).thenReturn(Optional.of(variant))
        `when`(orderRepository.save(ArgumentMatchers.any(Order::class.java))).thenReturn(savedOrder)
        `when`(orderItemRepository.save(ArgumentMatchers.any(OrderItem::class.java))).thenReturn(
            OrderItem(
                id = 1L,
                orderId = savedOrder.id,
                variantId = 1L,
                titleSnapshot = variant.title,
                qty = 2,
                priceSnapshot = BigDecimal("25.00")
            )
        )

        val result = checkoutService.submitCheckout(cartId, email, address)

        // Then
        assertNotNull(result)
        assertEquals(email, result.email)
        assertEquals(address, result.address)
        assertEquals(cart.subtotal, result.subtotal)
        assertEquals(cart.vatAmount, result.tax)
        assertEquals(cart.shippingCost, result.shipping)
        assertEquals(cart.total, result.total)
        assertEquals(PaymentStatus.PENDING, result.paymentStatus)
        
        // Verify that the service methods were called
        verify(cartRepository).findByIdWithItems(cartId)
        verify(variantRepository, times(2)).findById(1L) // Called twice: once for validation, once for order item creation
        verify(orderRepository).save(ArgumentMatchers.any(Order::class.java))
        verify(orderItemRepository).save(ArgumentMatchers.any(OrderItem::class.java))
    }

    @Test
    fun `submitCheckout should throw exception for invalid email`() {
        // Given
        val cartId = 1L
        val invalidEmail = "invalid-email"
        val address = "123 Main Street, City, State 12345, Country"

        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            checkoutService.submitCheckout(cartId, invalidEmail, address)
        }
        
        assertEquals("Invalid email format", exception.message)
    }

    @Test
    fun `submitCheckout should throw exception for blank email`() {
        // Given
        val cartId = 1L
        val blankEmail = ""
        val address = "123 Main Street, City, State 12345, Country"

        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            checkoutService.submitCheckout(cartId, blankEmail, address)
        }
        
        assertEquals("Email is required", exception.message)
    }

    @Test
    fun `submitCheckout should throw exception for short address`() {
        // Given
        val cartId = 1L
        val email = "test@example.com"
        val shortAddress = "Short"

        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            checkoutService.submitCheckout(cartId, email, shortAddress)
        }
        
        assertEquals("Address must be at least 10 characters long", exception.message)
    }

    @Test
    fun `submitCheckout should throw exception for non-existent cart`() {
        // Given
        val cartId = 999L
        val email = "test@example.com"
        val address = "123 Main Street, City, State 12345, Country"

        // When
        `when`(cartRepository.findByIdWithItems(cartId)).thenReturn(null)

        // Then
        val exception = assertThrows<IllegalArgumentException> {
            checkoutService.submitCheckout(cartId, email, address)
        }
        
        assertEquals("Cart not found with id: $cartId", exception.message)
    }

    @Test
    fun `submitCheckout should throw exception for empty cart`() {
        // Given
        val cartId = 1L
        val email = "test@example.com"
        val address = "123 Main Street, City, State 12345, Country"
        
        val emptyCart = Cart(id = cartId)

        // When
        `when`(cartRepository.findByIdWithItems(cartId)).thenReturn(emptyCart)

        // Then
        val exception = assertThrows<IllegalArgumentException> {
            checkoutService.submitCheckout(cartId, email, address)
        }
        
        assertEquals("Cannot checkout with empty cart", exception.message)
    }

    @Test
    fun `submitCheckout should throw exception for insufficient stock`() {
        // Given
        val cartId = 1L
        val email = "test@example.com"
        val address = "123 Main Street, City, State 12345, Country"
        
        val variant = Variant(
            id = 1L,
            productId = 1L,
            sku = "TEA-001-25G",
            title = "Earl Grey 25g",
            price = BigDecimal("25.00"),
            weight = BigDecimal("25"),
            shippingWeight = BigDecimal("30"),
            stockQty = 1,
            reservedQty = 0,
            lotId = 1L
        )
        
        val cartItem = CartItem(
            id = 1L,
            cartId = cartId,
            variantId = 1L,
            qty = 5, // More than available stock
            priceSnapshot = BigDecimal("25.00")
        )
        
        val cart = Cart(id = cartId)
        cart.items.add(cartItem)

        // When
        `when`(cartRepository.findByIdWithItems(cartId)).thenReturn(cart)
        `when`(variantRepository.findById(1L)).thenReturn(Optional.of(variant))

        // Then
        val exception = assertThrows<IllegalArgumentException> {
            checkoutService.submitCheckout(cartId, email, address)
        }
        
        assertTrue(exception.message!!.contains("Insufficient stock"))
    }

    @Test
    fun `getOrderByNumber should return order when found`() {
        // Given
        val orderNumber = "ORD-20231201-1234"
        val order = Order(
            id = 1L,
            number = orderNumber,
            email = "test@example.com",
            address = "123 Main Street",
            subtotal = BigDecimal("50.00"),
            tax = BigDecimal("10.00"),
            shipping = BigDecimal("5.00"),
            total = BigDecimal("65.00")
        )

        // When
        `when`(orderRepository.findByNumber(orderNumber)).thenReturn(order)

        val result = checkoutService.getOrderByNumber(orderNumber)

        // Then
        assertNotNull(result)
        assertEquals(orderNumber, result!!.number)
        assertEquals("test@example.com", result.email)
    }

    @Test
    fun `getOrderByNumber should return null when order not found`() {
        // Given
        val orderNumber = "NON-EXISTENT"

        // When
        `when`(orderRepository.findByNumber(orderNumber)).thenReturn(null)

        val result = checkoutService.getOrderByNumber(orderNumber)

        // Then
        assertNull(result)
    }
}