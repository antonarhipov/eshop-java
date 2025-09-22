package org.example.eshop.controller

import org.example.eshop.dto.CreateProductRequest
import org.example.eshop.dto.CreateVariantRequest
import org.example.eshop.dto.CreateLotRequest
import org.example.eshop.dto.UpdateProductRequest
import org.example.eshop.dto.UpdateVariantRequest
import org.example.eshop.dto.UpdateLotRequest
import org.example.eshop.service.AdminCatalogService
import org.example.eshop.service.AuditLogService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import jakarta.validation.Valid

@RestController
@RequestMapping("/api/admin")
class AdminCatalogController(
    private val adminCatalogService: AdminCatalogService,
    private val auditLogService: AuditLogService
) {

    @PostMapping("/products")
    fun createProduct(@Valid @RequestBody request: CreateProductRequest): ResponseEntity<Any> {
        return try {
            val product = adminCatalogService.createProduct(request)
            auditLogService.logAdminAction("CREATE", "Product", product.id, "slug=${request.slug}")
            ResponseEntity.status(HttpStatus.CREATED).body(product)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Failed to create product"))
        }
    }

    @PostMapping("/variants")
    fun createVariant(@Valid @RequestBody request: CreateVariantRequest): ResponseEntity<Any> {
        return try {
            val variant = adminCatalogService.createVariant(request)
            auditLogService.logAdminAction("CREATE", "Variant", variant.id, "sku=${request.sku}, productId=${request.productId}")
            ResponseEntity.status(HttpStatus.CREATED).body(variant)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Failed to create variant"))
        }
    }

    @PostMapping("/lots")
    fun createLot(@Valid @RequestBody request: CreateLotRequest): ResponseEntity<Any> {
        return try {
            val lot = adminCatalogService.createLot(request)
            auditLogService.logAdminAction("CREATE", "Lot", lot.id, "productId=${request.productId}, harvestYear=${request.harvestYear}")
            ResponseEntity.status(HttpStatus.CREATED).body(lot)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Failed to create lot"))
        }
    }

    @PatchMapping("/products/{id}")
    fun updateProduct(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateProductRequest
    ): ResponseEntity<Any> {
        return try {
            val product = adminCatalogService.updateProduct(id, request)
            auditLogService.logAdminAction("UPDATE", "Product", id, "slug=${product.slug}")
            ResponseEntity.ok(product)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Failed to update product"))
        }
    }

    @PatchMapping("/variants/{id}")
    fun updateVariant(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateVariantRequest
    ): ResponseEntity<Any> {
        return try {
            val variant = adminCatalogService.updateVariant(id, request)
            auditLogService.logAdminAction("UPDATE", "Variant", id, "sku=${variant.sku}")
            ResponseEntity.ok(variant)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Failed to update variant"))
        }
    }

    @PatchMapping("/lots/{id}")
    fun updateLot(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateLotRequest
    ): ResponseEntity<Any> {
        return try {
            val lot = adminCatalogService.updateLot(id, request)
            auditLogService.logAdminAction("UPDATE", "Lot", id, "harvestYear=${lot.harvestYear}")
            ResponseEntity.ok(lot)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Failed to update lot"))
        }
    }

    @DeleteMapping("/products/{id}")
    fun deleteProduct(@PathVariable id: Long): ResponseEntity<Any> {
        return try {
            adminCatalogService.deleteProduct(id)
            auditLogService.logAdminAction("DELETE", "Product", id)
            ResponseEntity.noContent().build()
        } catch (e: IllegalStateException) {
            ResponseEntity.status(HttpStatus.CONFLICT).body(mapOf("error" to e.message))
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Failed to delete product"))
        }
    }

    @DeleteMapping("/variants/{id}")
    fun deleteVariant(@PathVariable id: Long): ResponseEntity<Any> {
        return try {
            adminCatalogService.deleteVariant(id)
            auditLogService.logAdminAction("DELETE", "Variant", id)
            ResponseEntity.noContent().build()
        } catch (e: IllegalStateException) {
            ResponseEntity.status(HttpStatus.CONFLICT).body(mapOf("error" to e.message))
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Failed to delete variant"))
        }
    }

    @DeleteMapping("/lots/{id}")
    fun deleteLot(@PathVariable id: Long): ResponseEntity<Any> {
        return try {
            adminCatalogService.deleteLot(id)
            auditLogService.logAdminAction("DELETE", "Lot", id)
            ResponseEntity.noContent().build()
        } catch (e: IllegalStateException) {
            ResponseEntity.status(HttpStatus.CONFLICT).body(mapOf("error" to e.message))
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Failed to delete lot"))
        }
    }
}