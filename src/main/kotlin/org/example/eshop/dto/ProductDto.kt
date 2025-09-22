package org.example.eshop.dto

import java.math.BigDecimal

data class ProductDto(
    val id: Long,
    val slug: String,
    val title: String,
    val type: String,
    val description: String?,
    val minPrice: BigDecimal,
    val maxPrice: BigDecimal,
    val stockStatus: StockStatus,
    val harvestYear: Int?,
    val variantCount: Int
)

data class ProductDetailDto(
    val id: Long,
    val slug: String,
    val title: String,
    val type: String,
    val description: String?,
    val variants: List<VariantDto>,
    val harvestYear: Int?,
    val season: String?,
    val storageType: String?
)

data class VariantDto(
    val id: Long,
    val sku: String,
    val title: String,
    val price: BigDecimal,
    val weight: BigDecimal,
    val stockStatus: StockStatus,
    val availableQty: Int,
    val isInStock: Boolean
)

enum class StockStatus {
    IN_STOCK,
    LOW_STOCK,
    OUT_OF_STOCK
}