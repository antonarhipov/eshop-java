package org.example.eshop.service

import org.example.eshop.dto.*
import org.example.eshop.entity.*
import org.example.eshop.repository.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.dao.DataIntegrityViolationException

@Service
@Transactional
class AdminCatalogService(
    private val productRepository: ProductRepository,
    private val variantRepository: VariantRepository,
    private val lotRepository: LotRepository
) {

    // Product operations
    fun createProduct(request: CreateProductRequest): AdminProductResponse {
        // Check if slug already exists
        if (productRepository.findBySlug(request.slug) != null) {
            throw IllegalArgumentException("Product with slug '${request.slug}' already exists")
        }

        val product = Product(
            slug = request.slug,
            title = request.title,
            type = request.type,
            description = request.description,
            status = request.status
        )

        val savedProduct = productRepository.save(product)
        return toAdminProductResponse(savedProduct)
    }

    fun updateProduct(id: Long, request: UpdateProductRequest): AdminProductResponse {
        val product = productRepository.findById(id).orElseThrow {
            NoSuchElementException("Product with id $id not found")
        }

        // Check slug uniqueness if being updated
        request.slug?.let { newSlug ->
            val existingProduct = productRepository.findBySlug(newSlug)
            if (existingProduct != null && existingProduct.id != id) {
                throw IllegalArgumentException("Product with slug '$newSlug' already exists")
            }
        }

        val updatedProduct = product.copy(
            slug = request.slug ?: product.slug,
            title = request.title ?: product.title,
            type = request.type ?: product.type,
            description = request.description ?: product.description,
            status = request.status ?: product.status
        )

        val savedProduct = productRepository.save(updatedProduct)
        return toAdminProductResponse(savedProduct)
    }

    fun deleteProduct(id: Long) {
        val product = productRepository.findById(id).orElseThrow {
            NoSuchElementException("Product with id $id not found")
        }

        // Check if product has variants
        val variants = variantRepository.findByProductId(id)
        if (variants.isNotEmpty()) {
            throw IllegalStateException("Cannot delete product with existing variants. Delete variants first.")
        }

        // Check if product has lots
        val lots = lotRepository.findByProductId(id)
        if (lots.isNotEmpty()) {
            throw IllegalStateException("Cannot delete product with existing lots. Delete lots first.")
        }

        productRepository.delete(product)
    }

    // Variant operations
    fun createVariant(request: CreateVariantRequest): AdminVariantResponse {
        // Verify product exists
        val product = productRepository.findById(request.productId).orElseThrow {
            IllegalArgumentException("Product with id ${request.productId} not found")
        }

        // Check if SKU already exists
        if (variantRepository.findBySku(request.sku) != null) {
            throw IllegalArgumentException("Variant with SKU '${request.sku}' already exists")
        }

        // Verify lot exists if provided
        request.lotId?.let { lotId ->
            val lot = lotRepository.findById(lotId).orElseThrow {
                IllegalArgumentException("Lot with id $lotId not found")
            }
            // Verify lot belongs to the same product
            if (lot.productId != request.productId) {
                throw IllegalArgumentException("Lot $lotId does not belong to product ${request.productId}")
            }
        }

        val variant = Variant(
            productId = request.productId,
            sku = request.sku,
            title = request.title,
            price = request.price,
            weight = request.weight,
            shippingWeight = request.shippingWeight,
            stockQty = request.stockQty,
            lotId = request.lotId
        )

        val savedVariant = variantRepository.save(variant)
        return toAdminVariantResponse(savedVariant)
    }

    fun updateVariant(id: Long, request: UpdateVariantRequest): AdminVariantResponse {
        val variant = variantRepository.findById(id).orElseThrow {
            NoSuchElementException("Variant with id $id not found")
        }

        // Check SKU uniqueness if being updated
        request.sku?.let { newSku ->
            val existingVariant = variantRepository.findBySku(newSku)
            if (existingVariant != null && existingVariant.id != id) {
                throw IllegalArgumentException("Variant with SKU '$newSku' already exists")
            }
        }

        // Verify lot exists and belongs to same product if being updated
        request.lotId?.let { lotId ->
            val lot = lotRepository.findById(lotId).orElseThrow {
                IllegalArgumentException("Lot with id $lotId not found")
            }
            if (lot.productId != variant.productId) {
                throw IllegalArgumentException("Lot $lotId does not belong to product ${variant.productId}")
            }
        }

        val updatedVariant = variant.copy(
            sku = request.sku ?: variant.sku,
            title = request.title ?: variant.title,
            price = request.price ?: variant.price,
            weight = request.weight ?: variant.weight,
            shippingWeight = request.shippingWeight ?: variant.shippingWeight,
            stockQty = request.stockQty ?: variant.stockQty,
            lotId = request.lotId ?: variant.lotId
        )

        val savedVariant = variantRepository.save(updatedVariant)
        return toAdminVariantResponse(savedVariant)
    }

    fun deleteVariant(id: Long) {
        val variant = variantRepository.findById(id).orElseThrow {
            NoSuchElementException("Variant with id $id not found")
        }

        // Check if variant has reserved stock
        if (variant.reservedQty > 0) {
            throw IllegalStateException("Cannot delete variant with reserved stock (${variant.reservedQty} reserved)")
        }

        variantRepository.delete(variant)
    }

    // Lot operations
    fun createLot(request: CreateLotRequest): AdminLotResponse {
        // Verify product exists
        val product = productRepository.findById(request.productId).orElseThrow {
            IllegalArgumentException("Product with id ${request.productId} not found")
        }

        val lot = Lot(
            productId = request.productId,
            harvestYear = request.harvestYear,
            season = request.season,
            storageType = request.storageType,
            pressDate = request.pressDate
        )

        val savedLot = lotRepository.save(lot)
        return toAdminLotResponse(savedLot)
    }

    fun updateLot(id: Long, request: UpdateLotRequest): AdminLotResponse {
        val lot = lotRepository.findById(id).orElseThrow {
            NoSuchElementException("Lot with id $id not found")
        }

        val updatedLot = lot.copy(
            harvestYear = request.harvestYear ?: lot.harvestYear,
            season = request.season ?: lot.season,
            storageType = request.storageType ?: lot.storageType,
            pressDate = request.pressDate ?: lot.pressDate
        )

        val savedLot = lotRepository.save(updatedLot)
        return toAdminLotResponse(savedLot)
    }

    fun deleteLot(id: Long) {
        val lot = lotRepository.findById(id).orElseThrow {
            NoSuchElementException("Lot with id $id not found")
        }

        // Check if lot is referenced by variants
        val variants = variantRepository.findByLotId(id)
        if (variants.isNotEmpty()) {
            throw IllegalStateException("Cannot delete lot referenced by ${variants.size} variant(s)")
        }

        lotRepository.delete(lot)
    }

    // Helper methods to convert entities to response DTOs
    private fun toAdminProductResponse(product: Product): AdminProductResponse {
        val variantCount = variantRepository.findByProductId(product.id).size
        val lotCount = lotRepository.findByProductId(product.id).size
        
        return AdminProductResponse(
            id = product.id,
            slug = product.slug,
            title = product.title,
            type = product.type,
            description = product.description,
            status = product.status,
            variantCount = variantCount,
            lotCount = lotCount
        )
    }

    private fun toAdminVariantResponse(variant: Variant): AdminVariantResponse {
        return AdminVariantResponse(
            id = variant.id,
            productId = variant.productId,
            sku = variant.sku,
            title = variant.title,
            price = variant.price,
            weight = variant.weight,
            shippingWeight = variant.shippingWeight,
            stockQty = variant.stockQty,
            reservedQty = variant.reservedQty,
            availableQty = variant.availableQty(),
            lotId = variant.lotId
        )
    }

    private fun toAdminLotResponse(lot: Lot): AdminLotResponse {
        val variantCount = variantRepository.findByLotId(lot.id).size
        
        return AdminLotResponse(
            id = lot.id,
            productId = lot.productId,
            harvestYear = lot.harvestYear,
            season = lot.season,
            storageType = lot.storageType,
            pressDate = lot.pressDate,
            variantCount = variantCount
        )
    }
}