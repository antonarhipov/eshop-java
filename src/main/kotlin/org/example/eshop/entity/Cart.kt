package org.example.eshop.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "carts")
data class Cart(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, precision = 10, scale = 2)
    var subtotal: BigDecimal = BigDecimal.ZERO,

    @Column(nullable = false, precision = 10, scale = 2)
    var vatAmount: BigDecimal = BigDecimal.ZERO,

    @Column(nullable = false, precision = 10, scale = 2)
    var shippingCost: BigDecimal = BigDecimal.ZERO,

    @Column(nullable = false, precision = 10, scale = 2)
    var total: BigDecimal = BigDecimal.ZERO,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "cart", cascade = [CascadeType.ALL], fetch = FetchType.LAZY, orphanRemoval = true)
    val items: MutableList<CartItem> = mutableListOf()
) {
    @PreUpdate
    fun preUpdate() {
        updatedAt = LocalDateTime.now()
    }

    fun addItem(cartItem: CartItem) {
        items.add(cartItem)
    }

    fun removeItem(cartItem: CartItem) {
        items.remove(cartItem)
    }

    fun clearItems() {
        items.clear()
    }

    fun calculateTotals() {
        subtotal = items.sumOf { it.lineTotal }
        // VAT and shipping calculations will be handled by service layer
        total = subtotal + vatAmount + shippingCost
    }
}