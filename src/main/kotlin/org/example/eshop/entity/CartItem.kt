package org.example.eshop.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "cart_items")
data class CartItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "cart_id", nullable = false)
    val cartId: Long,

    @Column(name = "variant_id", nullable = false)
    val variantId: Long,

    @Column(nullable = false)
    var qty: Int,

    @Column(name = "price_snapshot", nullable = false, precision = 10, scale = 2)
    val priceSnapshot: BigDecimal,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", insertable = false, updatable = false)
    val cart: Cart? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", insertable = false, updatable = false)
    val variant: Variant? = null
) {
    @PreUpdate
    fun preUpdate() {
        updatedAt = LocalDateTime.now()
    }

    val lineTotal: BigDecimal
        get() = priceSnapshot.multiply(BigDecimal(qty))
}