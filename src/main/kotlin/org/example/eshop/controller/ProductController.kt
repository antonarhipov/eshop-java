package org.example.eshop.controller

import org.example.eshop.dto.ProductDto
import org.example.eshop.dto.ProductDetailDto
import org.example.eshop.service.ProductService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

@RestController
@RequestMapping("/api/products")
class ProductController(
    private val productService: ProductService
) {

    @GetMapping
    fun getProducts(
        @RequestParam(required = false) type: String?,
        @RequestParam(required = false) region: String?,
        @RequestParam(required = false) harvestYear: Int?,
        @RequestParam(required = false) minPrice: BigDecimal?,
        @RequestParam(required = false) maxPrice: BigDecimal?,
        @RequestParam(required = false) inStock: Boolean?
    ): ResponseEntity<List<ProductDto>> {
        return try {
            val products = productService.findProducts(
                type = type,
                region = region,
                harvestYear = harvestYear,
                minPrice = minPrice,
                maxPrice = maxPrice,
                inStock = inStock
            )
            ResponseEntity.ok(products)
        } catch (e: IllegalArgumentException) {
            // Handle invalid filter parameters gracefully
            ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/{slug}")
    fun getProductBySlug(@PathVariable slug: String): ResponseEntity<ProductDetailDto> {
        return productService.findProductBySlug(slug)?.let { product ->
            ResponseEntity.ok(product)
        } ?: ResponseEntity.notFound().build()
    }

    @GetMapping("/variant-info")
    fun getVariantInfo(@RequestParam variantId: Long): ResponseEntity<String> {
        return try {
            val variant = productService.findVariantById(variantId)
            if (variant != null) {
                val html = """
                    <div class="selected-price">
                        <span class="price-label">Price:</span>
                        <span class="price-value">$${String.format("%.2f", variant.price)}</span>
                    </div>
                    <div class="selected-stock">
                        <span class="stock-label">Availability:</span>
                        <span class="stock-value ${variant.stockStatus.name.lowercase()}">
                            ${when (variant.stockStatus.name) {
                                "IN_STOCK" -> "In Stock (${variant.availableQty} available)"
                                "LOW_STOCK" -> "Low Stock (${variant.availableQty} available)"
                                "OUT_OF_STOCK" -> "Out of Stock"
                                else -> "Unknown"
                            }}
                        </span>
                    </div>
                """.trimIndent()
                ResponseEntity.ok(html)
            } else {
                ResponseEntity.notFound().build()
            }
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }
}