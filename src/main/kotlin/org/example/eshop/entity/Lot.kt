package org.example.eshop.entity

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "lots")
data class Lot(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "product_id", nullable = false)
    val productId: Long,

    @Column(name = "harvest_year", nullable = false)
    val harvestYear: Int,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val season: Season,

    @Enumerated(EnumType.STRING)
    @Column(name = "storage_type", nullable = false)
    val storageType: StorageType,

    @Column(name = "press_date")
    val pressDate: LocalDate? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    val product: Product? = null
) {
    @PreUpdate
    fun preUpdate() {
        updatedAt = LocalDateTime.now()
    }
}

enum class Season {
    SPRING,
    SUMMER,
    AUTUMN,
    WINTER
}

enum class StorageType {
    DRY,
    WET,
    TRADITIONAL,
    NATURAL
}