package org.example.eshop.controller;

import jakarta.validation.Valid;
import org.example.eshop.dto.CreateLotRequest;
import org.example.eshop.dto.CreateProductRequest;
import org.example.eshop.dto.CreateVariantRequest;
import org.example.eshop.dto.UpdateLotRequest;
import org.example.eshop.dto.UpdateProductRequest;
import org.example.eshop.dto.UpdateVariantRequest;
import org.example.eshop.service.AdminCatalogService;
import org.example.eshop.service.AuditLogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminCatalogController {

    private final AdminCatalogService adminCatalogService;
    private final AuditLogService auditLogService;

    public AdminCatalogController(AdminCatalogService adminCatalogService, AuditLogService auditLogService) {
        this.adminCatalogService = adminCatalogService;
        this.auditLogService = auditLogService;
    }

    @PostMapping("/products")
    public ResponseEntity<?> createProduct(@Valid @RequestBody CreateProductRequest request) {
        try {
            var product = adminCatalogService.createProduct(request);
            auditLogService.logAdminAction("CREATE", "Product", product.getId(), "slug=" + request.getSlug());
            return ResponseEntity.status(HttpStatus.CREATED).body(product);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(java.util.Map.of("error", "Failed to create product"));
        }
    }

    @PostMapping("/variants")
    public ResponseEntity<?> createVariant(@Valid @RequestBody CreateVariantRequest request) {
        try {
            var variant = adminCatalogService.createVariant(request);
            auditLogService.logAdminAction("CREATE", "Variant", variant.getId(), "sku=" + request.getSku() + ", productId=" + request.getProductId());
            return ResponseEntity.status(HttpStatus.CREATED).body(variant);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(java.util.Map.of("error", "Failed to create variant"));
        }
    }

    @PostMapping("/lots")
    public ResponseEntity<?> createLot(@Valid @RequestBody CreateLotRequest request) {
        try {
            var lot = adminCatalogService.createLot(request);
            auditLogService.logAdminAction("CREATE", "Lot", lot.getId(), "productId=" + request.getProductId() + ", harvestYear=" + request.getHarvestYear());
            return ResponseEntity.status(HttpStatus.CREATED).body(lot);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(java.util.Map.of("error", "Failed to create lot"));
        }
    }

    @PatchMapping("/products/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request
    ) {
        try {
            var product = adminCatalogService.updateProduct(id, request);
            auditLogService.logAdminAction("UPDATE", "Product", id, "slug=" + product.getSlug());
            return ResponseEntity.ok(product);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        } catch (java.util.NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(java.util.Map.of("error", "Failed to update product"));
        }
    }

    @PatchMapping("/variants/{id}")
    public ResponseEntity<?> updateVariant(
            @PathVariable Long id,
            @Valid @RequestBody UpdateVariantRequest request
    ) {
        try {
            var variant = adminCatalogService.updateVariant(id, request);
            auditLogService.logAdminAction("UPDATE", "Variant", id, "sku=" + variant.getSku());
            return ResponseEntity.ok(variant);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        } catch (java.util.NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(java.util.Map.of("error", "Failed to update variant"));
        }
    }

    @PatchMapping("/lots/{id}")
    public ResponseEntity<?> updateLot(
            @PathVariable Long id,
            @Valid @RequestBody UpdateLotRequest request
    ) {
        try {
            var lot = adminCatalogService.updateLot(id, request);
            auditLogService.logAdminAction("UPDATE", "Lot", id, "harvestYear=" + lot.getHarvestYear());
            return ResponseEntity.ok(lot);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        } catch (java.util.NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(java.util.Map.of("error", "Failed to update lot"));
        }
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            adminCatalogService.deleteProduct(id);
            auditLogService.logAdminAction("DELETE", "Product", id, null);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(java.util.Map.of("error", e.getMessage()));
        } catch (java.util.NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(java.util.Map.of("error", "Failed to delete product"));
        }
    }

    @DeleteMapping("/variants/{id}")
    public ResponseEntity<?> deleteVariant(@PathVariable Long id) {
        try {
            adminCatalogService.deleteVariant(id);
            auditLogService.logAdminAction("DELETE", "Variant", id, null);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(java.util.Map.of("error", e.getMessage()));
        } catch (java.util.NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(java.util.Map.of("error", "Failed to delete variant"));
        }
    }

    @DeleteMapping("/lots/{id}")
    public ResponseEntity<?> deleteLot(@PathVariable Long id) {
        try {
            adminCatalogService.deleteLot(id);
            auditLogService.logAdminAction("DELETE", "Lot", id, null);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(java.util.Map.of("error", e.getMessage()));
        } catch (java.util.NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(java.util.Map.of("error", "Failed to delete lot"));
        }
    }
}
