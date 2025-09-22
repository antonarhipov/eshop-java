package org.example.eshop.controller

import org.example.eshop.service.ProductService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

@Controller
class CatalogController(
    private val productService: ProductService
) {

    @GetMapping("/")
    fun home(): String {
        return "redirect:/catalog"
    }

    @GetMapping("/catalog")
    fun catalog(
        @RequestParam(required = false) type: String?,
        @RequestParam(required = false) region: String?,
        @RequestParam(required = false) harvestYear: Int?,
        @RequestParam(required = false) minPrice: BigDecimal?,
        @RequestParam(required = false) maxPrice: BigDecimal?,
        @RequestParam(required = false) inStock: Boolean?,
        model: Model
    ): String {
        return try {
            val products = productService.findProducts(
                type = type,
                region = region,
                harvestYear = harvestYear,
                minPrice = minPrice,
                maxPrice = maxPrice,
                inStock = inStock
            )

            // Add filter values to model for form state
            model.addAttribute("products", products)
            model.addAttribute("selectedType", type)
            model.addAttribute("selectedRegion", region)
            model.addAttribute("selectedHarvestYear", harvestYear)
            model.addAttribute("selectedMinPrice", minPrice)
            model.addAttribute("selectedMaxPrice", maxPrice)
            model.addAttribute("selectedInStock", inStock)
            
            // Add available filter options
            model.addAttribute("availableTypes", getAvailableTypes())
            model.addAttribute("availableHarvestYears", getAvailableHarvestYears())
            
            "catalog"
        } catch (e: IllegalArgumentException) {
            // Handle invalid filter parameters gracefully
            model.addAttribute("error", "Invalid filter parameters: ${e.message}")
            model.addAttribute("products", emptyList<Any>())
            "catalog"
        }
    }

    @GetMapping("/products/{slug}")
    fun productDetail(@PathVariable slug: String, model: Model): String {
        val product = productService.findProductBySlug(slug)
        return if (product != null) {
            model.addAttribute("product", product)
            "product-detail"
        } else {
            "redirect:/catalog"
        }
    }

    private fun getAvailableTypes(): List<String> {
        return listOf("Green Tea", "Black Tea", "Pu-erh Tea", "White Tea", "Teaware")
    }

    private fun getAvailableHarvestYears(): List<Int> {
        return (2018..2024).toList().reversed()
    }
}