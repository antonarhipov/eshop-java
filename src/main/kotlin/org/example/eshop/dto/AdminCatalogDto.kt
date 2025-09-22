package org.example.eshop.dto

import org.example.eshop.entity.ProductStatus
import org.example.eshop.entity.Season
import org.example.eshop.entity.StorageType
import jakarta.validation.constraints.*
import java.math.BigDecimal
import java.time.LocalDate

// Product DTOs
data class CreateProductRequest(
    @field:NotBlank(message = "Slug is required")
    @field:Pattern(regexp = "^[a-z0-9-]+$", message = "Slug must contain only lowercase letters, numbers, and hyphens")
    val slug: String,

    @field:NotBlank(message = "Title is required")
    @field:Size(max = 255, message = "Title must not exceed 255 characters")
    val title: String,

    @field:NotBlank(message = "Type is required")
    @field:Size(max = 100, message = "Type must not exceed 100 characters")
    val type: String,

    @field:Size(max = 5000, message = "Description must not exceed 5000 characters")
    val description: String? = null,

    val status: ProductStatus = ProductStatus.ACTIVE
)

data class UpdateProductRequest(
    @field:Pattern(regexp = "^[a-z0-9-]+$", message = "Slug must contain only lowercase letters, numbers, and hyphens")
    val slug: String? = null,

    @field:Size(max = 255, message = "Title must not exceed 255 characters")
    val title: String? = null,

    @field:Size(max = 100, message = "Type must not exceed 100 characters")
    val type: String? = null,

    @field:Size(max = 5000, message = "Description must not exceed 5000 characters")
    val description: String? = null,

    val status: ProductStatus? = null
)

// Variant DTOs
data class CreateVariantRequest(
    @field:NotNull(message = "Product ID is required")
    @field:Positive(message = "Product ID must be positive")
    val productId: Long,

    @field:NotBlank(message = "SKU is required")
    @field:Pattern(regexp = "^[A-Z0-9-]+$", message = "SKU must contain only uppercase letters, numbers, and hyphens")
    val sku: String,

    @field:NotBlank(message = "Title is required")
    @field:Size(max = 255, message = "Title must not exceed 255 characters")
    val title: String,

    @field:NotNull(message = "Price is required")
    @field:DecimalMin(value = "0.01", message = "Price must be at least 0.01")
    @field:DecimalMax(value = "99999.99", message = "Price must not exceed 99999.99")
    val price: BigDecimal,

    @field:NotNull(message = "Weight is required")
    @field:DecimalMin(value = "0.001", message = "Weight must be at least 0.001")
    @field:DecimalMax(value = "99999.999", message = "Weight must not exceed 99999.999")
    val weight: BigDecimal,

    @field:NotNull(message = "Shipping weight is required")
    @field:DecimalMin(value = "0.001", message = "Shipping weight must be at least 0.001")
    @field:DecimalMax(value = "99999.999", message = "Shipping weight must not exceed 99999.999")
    val shippingWeight: BigDecimal,

    @field:NotNull(message = "Stock quantity is required")
    @field:Min(value = 0, message = "Stock quantity must not be negative")
    val stockQty: Int,

    val lotId: Long? = null
)

data class UpdateVariantRequest(
    @field:Pattern(regexp = "^[A-Z0-9-]+$", message = "SKU must contain only uppercase letters, numbers, and hyphens")
    val sku: String? = null,

    @field:Size(max = 255, message = "Title must not exceed 255 characters")
    val title: String? = null,

    @field:DecimalMin(value = "0.01", message = "Price must be at least 0.01")
    @field:DecimalMax(value = "99999.99", message = "Price must not exceed 99999.99")
    val price: BigDecimal? = null,

    @field:DecimalMin(value = "0.001", message = "Weight must be at least 0.001")
    @field:DecimalMax(value = "99999.999", message = "Weight must not exceed 99999.999")
    val weight: BigDecimal? = null,

    @field:DecimalMin(value = "0.001", message = "Shipping weight must be at least 0.001")
    @field:DecimalMax(value = "99999.999", message = "Shipping weight must not exceed 99999.999")
    val shippingWeight: BigDecimal? = null,

    @field:Min(value = 0, message = "Stock quantity must not be negative")
    val stockQty: Int? = null,

    val lotId: Long? = null
)

// Lot DTOs
data class CreateLotRequest(
    @field:NotNull(message = "Product ID is required")
    @field:Positive(message = "Product ID must be positive")
    val productId: Long,

    @field:NotNull(message = "Harvest year is required")
    @field:Min(value = 1900, message = "Harvest year must be at least 1900")
    @field:Max(value = 2030, message = "Harvest year must not exceed 2030")
    val harvestYear: Int,

    @field:NotNull(message = "Season is required")
    val season: Season,

    @field:NotNull(message = "Storage type is required")
    val storageType: StorageType,

    val pressDate: LocalDate? = null
)

data class UpdateLotRequest(
    @field:Min(value = 1900, message = "Harvest year must be at least 1900")
    @field:Max(value = 2030, message = "Harvest year must not exceed 2030")
    val harvestYear: Int? = null,

    val season: Season? = null,

    val storageType: StorageType? = null,

    val pressDate: LocalDate? = null
)

// Response DTOs
data class AdminProductResponse(
    val id: Long,
    val slug: String,
    val title: String,
    val type: String,
    val description: String?,
    val status: ProductStatus,
    val variantCount: Int,
    val lotCount: Int
)

data class AdminVariantResponse(
    val id: Long,
    val productId: Long,
    val sku: String,
    val title: String,
    val price: BigDecimal,
    val weight: BigDecimal,
    val shippingWeight: BigDecimal,
    val stockQty: Int,
    val reservedQty: Int,
    val availableQty: Int,
    val lotId: Long?
)

data class AdminLotResponse(
    val id: Long,
    val productId: Long,
    val harvestYear: Int,
    val season: Season,
    val storageType: StorageType,
    val pressDate: LocalDate?,
    val variantCount: Int
)