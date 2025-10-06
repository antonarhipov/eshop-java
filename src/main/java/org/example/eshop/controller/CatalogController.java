package org.example.eshop.controller;

import org.example.eshop.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class CatalogController {

    private final ProductService productService;

    public CatalogController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/catalog";
    }

    @GetMapping("/catalog")
    public String catalog(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) Integer harvestYear,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean inStock,
            Model model
    ) {
        try {
            var products = productService.findProducts(
                    type,
                    region,
                    harvestYear,
                    minPrice,
                    maxPrice,
                    inStock
            );

            // Add filter values to model for form state
            model.addAttribute("products", products);
            model.addAttribute("selectedType", type);
            model.addAttribute("selectedRegion", region);
            model.addAttribute("selectedHarvestYear", harvestYear);
            model.addAttribute("selectedMinPrice", minPrice);
            model.addAttribute("selectedMaxPrice", maxPrice);
            model.addAttribute("selectedInStock", inStock);

            // Add available filter options
            model.addAttribute("availableTypes", getAvailableTypes());
            model.addAttribute("availableHarvestYears", getAvailableHarvestYears());

            return "catalog";
        } catch (IllegalArgumentException e) {
            // Handle invalid filter parameters gracefully
            model.addAttribute("error", "Invalid filter parameters: " + e.getMessage());
            model.addAttribute("products", List.of());
            return "catalog";
        }
    }

    @GetMapping("/products/{slug}")
    public String productDetail(@PathVariable String slug, Model model) {
        var product = productService.findProductBySlug(slug);
        if (product != null) {
            model.addAttribute("product", product);
            return "product-detail";
        } else {
            return "redirect:/catalog";
        }
    }

    private List<String> getAvailableTypes() {
        return List.of("Green Tea", "Black Tea", "Pu-erh Tea", "White Tea", "Teaware");
    }

    private List<Integer> getAvailableHarvestYears() {
        return java.util.stream.IntStream.rangeClosed(2018, 2024)
                .boxed()
                .sorted((a, b) -> b - a)
                .toList();
    }
}
