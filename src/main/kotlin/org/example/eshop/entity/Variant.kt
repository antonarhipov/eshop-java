package org.example.eshop.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "variants")
data class Variant(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "product_id", nullable = false)
    val productId: Long,

    @Column(unique = true, nullable = false)
    val sku: String,

    @Column(nullable = false)
    val title: String,

    @Column(nullable = false, precision = 10, scale = 2)
    val price: BigDecimal,

    @Column(nullable = false, precision = 8, scale = 3)
    val weight: BigDecimal,

    @Column(name = "shipping_weight", nullable = false, precision = 8, scale = 3)
    val shippingWeight: BigDecimal,

    @Column(name = "stock_qty", nullable = false)
    var stockQty: Int = 0,

    @Column(name = "reserved_qty", nullable = false)
    var reservedQty: Int = 0,

    @Column(name = "lot_id")
    val lotId: Long? = null,

    @Version
    val version: Long = 0,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    val product: Product? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_id", insertable = false, updatable = false)
    val lot: Lot? = null
) {
    @PreUpdate
    fun preUpdate() {
        updatedAt = LocalDateTime.now()
    }

    fun availableQty(): Int = stockQty - reservedQty
}