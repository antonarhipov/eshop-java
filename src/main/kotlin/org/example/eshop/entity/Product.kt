package org.example.eshop.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "products")
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(unique = true, nullable = false)
    val slug: String,

    @Column(nullable = false)
    val title: String,

    @Column(nullable = false)
    val type: String,

    @Column(columnDefinition = "TEXT")
    val description: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: ProductStatus = ProductStatus.ACTIVE,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    val variants: List<Variant> = emptyList(),

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    val lots: List<Lot> = emptyList()
) {
    @PreUpdate
    fun preUpdate() {
        updatedAt = LocalDateTime.now()
    }
}

enum class ProductStatus {
    ACTIVE,
    INACTIVE,
    DRAFT
}