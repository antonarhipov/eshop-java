package org.example.eshop.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
data class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(unique = true, nullable = false)
    val number: String,

    @Column(nullable = false)
    val email: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    val address: String,

    @Column(name = "full_name", nullable = false)
    val fullName: String = "",

    @Column
    val phone: String? = null,

    @Column(nullable = false)
    val street1: String = "",

    @Column
    val street2: String? = null,

    @Column(nullable = false)
    val city: String = "",

    @Column(nullable = false)
    val region: String = "",

    @Column(name = "postal_code", nullable = false)
    val postalCode: String = "",

    @Column(nullable = false)
    val country: String = "",

    @Column(nullable = false, precision = 10, scale = 2)
    val subtotal: BigDecimal,

    @Column(nullable = false, precision = 10, scale = 2)
    val tax: BigDecimal,

    @Column(nullable = false, precision = 10, scale = 2)
    val shipping: BigDecimal,

    @Column(nullable = false, precision = 10, scale = 2)
    val total: BigDecimal,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: OrderStatus = OrderStatus.PENDING,

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    var paymentStatus: PaymentStatus = PaymentStatus.PENDING,

    @Enumerated(EnumType.STRING)
    @Column(name = "fulfillment_status", nullable = false)
    var fulfillmentStatus: FulfillmentStatus = FulfillmentStatus.UNFULFILLED,

    @Column(name = "tracking_url")
    var trackingUrl: String? = null,

    @Version
    val version: Long = 0,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], fetch = FetchType.LAZY, orphanRemoval = true)
    val items: MutableList<OrderItem> = mutableListOf()
) {
    @PreUpdate
    fun preUpdate() {
        updatedAt = LocalDateTime.now()
    }

    fun addItem(orderItem: OrderItem) {
        items.add(orderItem)
    }
}

enum class OrderStatus {
    PENDING,
    CONFIRMED,
    CANCELLED
}

enum class PaymentStatus {
    PENDING,
    PAID,
    FAILED,
    REFUNDED
}

enum class FulfillmentStatus {
    UNFULFILLED,
    FULFILLED,
    PARTIALLY_FULFILLED
}