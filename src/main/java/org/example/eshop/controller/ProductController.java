package org.example.eshop.controller;

import org.example.eshop.dto.ProductDetailDto;
import org.example.eshop.dto.ProductDto;
import org.example.eshop.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> getProducts(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) Integer harvestYear,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean inStock
    ) {
        try {
            List<ProductDto> products = productService.findProducts(
                    type,
                    region,
                    harvestYear,
                    minPrice,
                    maxPrice,
                    inStock
            );
            return ResponseEntity.ok(products);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ProductDetailDto> getProductBySlug(@PathVariable String slug) {
        ProductDetailDto product = productService.findProductBySlug(slug);
        if (product != null) {
            return ResponseEntity.ok(product);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/variant-info")
    public ResponseEntity<String> getVariantInfo(@RequestParam Long variantId) {
        try {
            var variant = productService.findVariantById(variantId);
            if (variant != null) {
                String html = """
                    <div class=\"selected-price\">
                        <span class=\"price-label\">Price:</span>
                        <span class=\"price-value\">$%s</span>
                    </div>
                    <div class=\"selected-stock\">
                        <span class=\"stock-label\">Availability:</span>
                        <span class=\"stock-value %s\">%s</span>
                    </div>
                """.formatted(
                        String.format("%.2f", variant.getPrice()),
                        variant.getStockStatus().name().toLowerCase(),
                        switch (variant.getStockStatus().name()) {
                            case "IN_STOCK" -> "In Stock (" + variant.getAvailableQty() + " available)";
                            case "LOW_STOCK" -> "Low Stock (" + variant.getAvailableQty() + " available)";
                            case "OUT_OF_STOCK" -> "Out of Stock";
                            default -> "Unknown";
                        }
                );
                return ResponseEntity.ok(html);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
