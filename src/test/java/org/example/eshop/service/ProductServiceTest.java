package org.example.eshop.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.example.eshop.dto.StockStatus;
import org.example.eshop.entity.*;
import org.example.eshop.repository.LotRepository;
import org.example.eshop.repository.ProductRepository;
import org.example.eshop.repository.VariantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ProductServiceTest {

    @Mock private ProductRepository productRepository;
    @Mock private VariantRepository variantRepository;
    @Mock private LotRepository lotRepository;
    @Mock private EntityManager entityManager;
    @Mock private CriteriaBuilder criteriaBuilder;
    @Mock private CriteriaQuery<Product> criteriaQuery;
    @Mock private Root<Product> root;
    @Mock private TypedQuery<Product> typedQuery;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productService = new ProductService(productRepository, variantRepository, lotRepository, entityManager);
        // Minimal stubbing for criteria path when methods get past validation in other tests (not needed here but safe)
        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Product.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Product.class)).thenReturn(root);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of());
    }

    @Test
    void findProducts_shouldValidateNegativeMinPrice() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                productService.findProducts(null, null, null, new BigDecimal("-1.00"), null, null)
        );
        assertEquals("minPrice cannot be negative", ex.getMessage());
    }

    @Test
    void findProducts_shouldValidateNegativeMaxPrice() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                productService.findProducts(null, null, null, null, new BigDecimal("-1.00"), null)
        );
        assertEquals("maxPrice cannot be negative", ex.getMessage());
    }

    @Test
    void findProducts_shouldValidateMinPriceGreaterThanMaxPrice() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                productService.findProducts(null, null, null, new BigDecimal("100.00"), new BigDecimal("50.00"), null)
        );
        assertEquals("minPrice cannot be greater than maxPrice", ex.getMessage());
    }

    @Test
    void findProducts_shouldValidateInvalidHarvestYear() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                productService.findProducts(null, null, 1800, null, null, null)
        );
        assertEquals("harvestYear must be between 1900 and 2030", ex.getMessage());
    }

    @Test
    void findProductBySlug_shouldReturnNullForNonExistentProduct() {
        when(productRepository.findBySlug("non-existent")).thenReturn(null);
        assertNull(productService.findProductBySlug("non-existent"));
    }

    @Test
    void findProductBySlug_shouldReturnNullForInactiveProduct() {
        Product inactive = new Product("inactive-product", "Inactive Product", "Green Tea", null, ProductStatus.INACTIVE);
        inactive.setId(1L);
        when(productRepository.findBySlug("inactive-product")).thenReturn(inactive);
        assertNull(productService.findProductBySlug("inactive-product"));
    }

    @Test
    void findProductBySlug_shouldReturnProductDetailForActiveProduct() {
        Product product = new Product("dragon-well", "Dragon Well Green Tea", "Green Tea", "Premium green tea", ProductStatus.ACTIVE);
        product.setId(1L);

        Lot lot = new Lot(1L, 2024, Season.SPRING, StorageType.DRY);
        lot.setId(1L);

        Variant variant = new Variant(1L, "DW-25G", "Dragon Well - 25g", new BigDecimal("12.99"), new BigDecimal("0.025"), new BigDecimal("0.050"));
        variant.setId(1L);
        variant.setStockQty(50);
        variant.setReservedQty(0);
        variant.setLotId(1L);
        variant.setLot(lot);

        when(productRepository.findBySlug("dragon-well")).thenReturn(product);
        when(variantRepository.findByProductId(1L)).thenReturn(List.of(variant));

        var result = productService.findProductBySlug("dragon-well");
        assertNotNull(result);
        assertEquals("Dragon Well Green Tea", result.getTitle());
        assertEquals("Green Tea", result.getType());
        assertEquals(1, result.getVariants().size());
        assertEquals(2024, result.getHarvestYear());
        assertEquals("SPRING", result.getSeason());
        assertEquals("DRY", result.getStorageType());
    }

    @Test
    void findVariantById_shouldReturnNullForNonExistentVariant() {
        when(variantRepository.findById(999L)).thenReturn(Optional.empty());
        assertNull(productService.findVariantById(999L));
    }

    @Test
    void findVariantById_shouldReturnVariantDtoForExistingVariant() {
        Variant variant = new Variant(1L, "DW-25G", "Dragon Well - 25g", new BigDecimal("12.99"), new BigDecimal("0.025"), new BigDecimal("0.050"));
        variant.setId(1L);
        variant.setStockQty(50);
        variant.setReservedQty(0);

        when(variantRepository.findById(1L)).thenReturn(Optional.of(variant));

        var result = productService.findVariantById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("DW-25G", result.getSku());
        assertEquals("Dragon Well - 25g", result.getTitle());
        assertEquals(new BigDecimal("12.99"), result.getPrice());
        assertEquals(StockStatus.IN_STOCK, StockStatus.valueOf(result.getStockStatus().name()));
        assertEquals(50, result.getAvailableQty());
        assertTrue(result.isInStock());
    }

    @Test
    void variant_shouldHaveCorrectStockStatus_inStock() {
        Variant variant = new Variant(1L, "TEST-SKU", "Test Variant", new BigDecimal("10.00"), new BigDecimal("0.100"), new BigDecimal("0.150"));
        variant.setStockQty(20);
        variant.setReservedQty(0);
        when(variantRepository.findById(1L)).thenReturn(Optional.of(variant));
        var result = productService.findVariantById(1L);
        assertNotNull(result);
        assertEquals(StockStatus.IN_STOCK, StockStatus.valueOf(result.getStockStatus().name()));
    }

    @Test
    void variant_shouldHaveCorrectStockStatus_lowStock() {
        Variant variant = new Variant(1L, "TEST-SKU", "Test Variant", new BigDecimal("10.00"), new BigDecimal("0.100"), new BigDecimal("0.150"));
        variant.setStockQty(3);
        variant.setReservedQty(0);
        when(variantRepository.findById(1L)).thenReturn(Optional.of(variant));
        var result = productService.findVariantById(1L);
        assertNotNull(result);
        assertEquals(StockStatus.LOW_STOCK, StockStatus.valueOf(result.getStockStatus().name()));
    }

    @Test
    void variant_shouldHaveCorrectStockStatus_outOfStock() {
        Variant variant = new Variant(1L, "TEST-SKU", "Test Variant", new BigDecimal("10.00"), new BigDecimal("0.100"), new BigDecimal("0.150"));
        variant.setStockQty(5);
        variant.setReservedQty(5);
        when(variantRepository.findById(1L)).thenReturn(Optional.of(variant));
        var result = productService.findVariantById(1L);
        assertNotNull(result);
        assertEquals(StockStatus.OUT_OF_STOCK, StockStatus.valueOf(result.getStockStatus().name()));
        assertFalse(result.isInStock());
    }
}
