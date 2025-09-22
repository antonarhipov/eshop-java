package org.example.eshop.service

import org.example.eshop.dto.ProductDto
import org.example.eshop.dto.ProductDetailDto
import org.example.eshop.dto.VariantDto
import org.example.eshop.dto.StockStatus
import org.example.eshop.entity.Product
import org.example.eshop.entity.ProductStatus
import org.example.eshop.entity.Variant
import org.example.eshop.repository.ProductRepository
import org.example.eshop.repository.VariantRepository
import org.example.eshop.repository.LotRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.*

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val variantRepository: VariantRepository,
    private val lotRepository: LotRepository,
    private val entityManager: EntityManager
) {

    fun findProducts(
        type: String?,
        region: String?, // Note: region is not in our current data model, treating as null for now
        harvestYear: Int?,
        minPrice: BigDecimal?,
        maxPrice: BigDecimal?,
        inStock: Boolean?
    ): List<ProductDto> {
        // Validate parameters
        if (minPrice != null && minPrice < BigDecimal.ZERO) {
            throw IllegalArgumentException("minPrice cannot be negative")
        }
        if (maxPrice != null && maxPrice < BigDecimal.ZERO) {
            throw IllegalArgumentException("maxPrice cannot be negative")
        }
        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            throw IllegalArgumentException("minPrice cannot be greater than maxPrice")
        }
        if (harvestYear != null && (harvestYear < 1900 || harvestYear > 2030)) {
            throw IllegalArgumentException("harvestYear must be between 1900 and 2030")
        }

        val cb = entityManager.criteriaBuilder
        val query = cb.createQuery(Product::class.java)
        val root = query.from(Product::class.java)
        val variantJoin = root.join<Product, Variant>("variants", JoinType.LEFT)
        val lotJoin = variantJoin.join<Variant, Any>("lot", JoinType.LEFT)

        val predicates = mutableListOf<Predicate>()

        // Always filter for active products
        predicates.add(cb.equal(root.get<ProductStatus>("status"), ProductStatus.ACTIVE))

        // Type filter
        type?.let { predicates.add(cb.equal(root.get<String>("type"), it)) }

        // Harvest year filter (through lot)
        harvestYear?.let { 
            predicates.add(cb.equal(lotJoin.get<Int>("harvestYear"), it))
        }

        // Price filters (through variants)
        minPrice?.let {
            predicates.add(cb.greaterThanOrEqualTo(variantJoin.get<BigDecimal>("price"), it))
        }
        maxPrice?.let {
            predicates.add(cb.lessThanOrEqualTo(variantJoin.get<BigDecimal>("price"), it))
        }

        // Stock filter (through variants)
        inStock?.let { stockFilter ->
            if (stockFilter) {
                predicates.add(cb.greaterThan(
                    cb.diff(variantJoin.get<Int>("stockQty"), variantJoin.get<Int>("reservedQty")), 
                    0
                ))
            }
        }

        query.select(root).distinct(true).where(*predicates.toTypedArray())

        val products = entityManager.createQuery(query).resultList

        return products.map { product ->
            val variants = variantRepository.findByProductId(product.id)
            val stockStatus = calculateProductStockStatus(variants)
            val prices = variants.map { it.price }
            val harvestYearFromLot = variants.mapNotNull { it.lot?.harvestYear }.firstOrNull()

            ProductDto(
                id = product.id,
                slug = product.slug,
                title = product.title,
                type = product.type,
                description = product.description,
                minPrice = prices.minOrNull() ?: BigDecimal.ZERO,
                maxPrice = prices.maxOrNull() ?: BigDecimal.ZERO,
                stockStatus = stockStatus,
                harvestYear = harvestYearFromLot,
                variantCount = variants.size
            )
        }
    }

    fun findProductBySlug(slug: String): ProductDetailDto? {
        val product = productRepository.findBySlug(slug) ?: return null
        if (product.status != ProductStatus.ACTIVE) return null

        val variants = variantRepository.findByProductId(product.id)
        val variantDtos = variants.map { variant ->
            VariantDto(
                id = variant.id,
                sku = variant.sku,
                title = variant.title,
                price = variant.price,
                weight = variant.weight,
                stockStatus = calculateVariantStockStatus(variant),
                availableQty = variant.availableQty(),
                isInStock = variant.availableQty() > 0
            )
        }

        // Get lot information from the first variant that has a lot
        val firstLot = variants.mapNotNull { it.lot }.firstOrNull()

        return ProductDetailDto(
            id = product.id,
            slug = product.slug,
            title = product.title,
            type = product.type,
            description = product.description,
            variants = variantDtos,
            harvestYear = firstLot?.harvestYear,
            season = firstLot?.season?.name,
            storageType = firstLot?.storageType?.name
        )
    }

    private fun calculateProductStockStatus(variants: List<Variant>): StockStatus {
        val inStockVariants = variants.filter { it.availableQty() > 0 }
        val lowStockVariants = variants.filter { it.availableQty() in 1..5 }

        return when {
            inStockVariants.isEmpty() -> StockStatus.OUT_OF_STOCK
            lowStockVariants.isNotEmpty() -> StockStatus.LOW_STOCK
            else -> StockStatus.IN_STOCK
        }
    }

    fun findVariantById(variantId: Long): VariantDto? {
        val variant = variantRepository.findById(variantId).orElse(null) ?: return null
        return VariantDto(
            id = variant.id,
            sku = variant.sku,
            title = variant.title,
            price = variant.price,
            weight = variant.weight,
            stockStatus = calculateVariantStockStatus(variant),
            availableQty = variant.availableQty(),
            isInStock = variant.availableQty() > 0
        )
    }

    private fun calculateVariantStockStatus(variant: Variant): StockStatus {
        val available = variant.availableQty()
        return when {
            available <= 0 -> StockStatus.OUT_OF_STOCK
            available <= 5 -> StockStatus.LOW_STOCK
            else -> StockStatus.IN_STOCK
        }
    }
}