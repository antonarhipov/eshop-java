package org.example.eshop.service

import org.example.eshop.dto.StockStatus
import org.example.eshop.entity.*
import org.example.eshop.repository.LotRepository
import org.example.eshop.repository.ProductRepository
import org.example.eshop.repository.VariantRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.Mockito.`when`
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import jakarta.persistence.EntityManager
import jakarta.persistence.TypedQuery
import jakarta.persistence.criteria.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@SpringBootTest
class ProductServiceTest {

    @Mock
    private lateinit var productRepository: ProductRepository

    @Mock
    private lateinit var variantRepository: VariantRepository

    @Mock
    private lateinit var lotRepository: LotRepository

    @Mock
    private lateinit var entityManager: EntityManager

    @Mock
    private lateinit var criteriaBuilder: CriteriaBuilder

    @Mock
    private lateinit var criteriaQuery: CriteriaQuery<Product>

    @Mock
    private lateinit var root: Root<Product>

    @Mock
    private lateinit var typedQuery: TypedQuery<Product>

    private lateinit var productService: ProductService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        productService = ProductService(productRepository, variantRepository, lotRepository, entityManager)
    }

    @Test
    fun `findProducts should validate negative minPrice`() {
        val exception = assertThrows<IllegalArgumentException> {
            productService.findProducts(
                type = null,
                region = null,
                harvestYear = null,
                minPrice = BigDecimal("-1.00"),
                maxPrice = null,
                inStock = null
            )
        }
        assertEquals("minPrice cannot be negative", exception.message)
    }

    @Test
    fun `findProducts should validate negative maxPrice`() {
        val exception = assertThrows<IllegalArgumentException> {
            productService.findProducts(
                type = null,
                region = null,
                harvestYear = null,
                minPrice = null,
                maxPrice = BigDecimal("-1.00"),
                inStock = null
            )
        }
        assertEquals("maxPrice cannot be negative", exception.message)
    }

    @Test
    fun `findProducts should validate minPrice greater than maxPrice`() {
        val exception = assertThrows<IllegalArgumentException> {
            productService.findProducts(
                type = null,
                region = null,
                harvestYear = null,
                minPrice = BigDecimal("100.00"),
                maxPrice = BigDecimal("50.00"),
                inStock = null
            )
        }
        assertEquals("minPrice cannot be greater than maxPrice", exception.message)
    }

    @Test
    fun `findProducts should validate invalid harvestYear`() {
        val exception = assertThrows<IllegalArgumentException> {
            productService.findProducts(
                type = null,
                region = null,
                harvestYear = 1800,
                minPrice = null,
                maxPrice = null,
                inStock = null
            )
        }
        assertEquals("harvestYear must be between 1900 and 2030", exception.message)
    }

    @Test
    fun `findProductBySlug should return null for non-existent product`() {
        `when`(productRepository.findBySlug("non-existent")).thenReturn(null)
        
        val result = productService.findProductBySlug("non-existent")
        
        assertNull(result)
    }

    @Test
    fun `findProductBySlug should return null for inactive product`() {
        val inactiveProduct = Product(
            id = 1L,
            slug = "inactive-product",
            title = "Inactive Product",
            type = "Green Tea",
            status = ProductStatus.INACTIVE
        )
        
        `when`(productRepository.findBySlug("inactive-product")).thenReturn(inactiveProduct)
        
        val result = productService.findProductBySlug("inactive-product")
        
        assertNull(result)
    }

    @Test
    fun `findProductBySlug should return product detail for active product`() {
        val product = Product(
            id = 1L,
            slug = "dragon-well",
            title = "Dragon Well Green Tea",
            type = "Green Tea",
            description = "Premium green tea",
            status = ProductStatus.ACTIVE
        )
        
        val lot = Lot(
            id = 1L,
            productId = 1L,
            harvestYear = 2024,
            season = Season.SPRING,
            storageType = StorageType.DRY
        )
        
        val variant = Variant(
            id = 1L,
            productId = 1L,
            sku = "DW-25G",
            title = "Dragon Well - 25g",
            price = BigDecimal("12.99"),
            weight = BigDecimal("0.025"),
            shippingWeight = BigDecimal("0.050"),
            stockQty = 50,
            reservedQty = 0,
            lotId = 1L,
            lot = lot
        )
        
        `when`(productRepository.findBySlug("dragon-well")).thenReturn(product)
        `when`(variantRepository.findByProductId(1L)).thenReturn(listOf(variant))
        
        val result = productService.findProductBySlug("dragon-well")
        
        assertNotNull(result)
        assertEquals("Dragon Well Green Tea", result.title)
        assertEquals("Green Tea", result.type)
        assertEquals(1, result.variants.size)
        assertEquals(2024, result.harvestYear)
        assertEquals("SPRING", result.season)
        assertEquals("DRY", result.storageType)
    }

    @Test
    fun `findVariantById should return null for non-existent variant`() {
        `when`(variantRepository.findById(999L)).thenReturn(Optional.empty())
        
        val result = productService.findVariantById(999L)
        
        assertNull(result)
    }

    @Test
    fun `findVariantById should return variant DTO for existing variant`() {
        val variant = Variant(
            id = 1L,
            productId = 1L,
            sku = "DW-25G",
            title = "Dragon Well - 25g",
            price = BigDecimal("12.99"),
            weight = BigDecimal("0.025"),
            shippingWeight = BigDecimal("0.050"),
            stockQty = 50,
            reservedQty = 0
        )
        
        `when`(variantRepository.findById(1L)).thenReturn(Optional.of(variant))
        
        val result = productService.findVariantById(1L)
        
        assertNotNull(result)
        assertEquals(1L, result.id)
        assertEquals("DW-25G", result.sku)
        assertEquals("Dragon Well - 25g", result.title)
        assertEquals(BigDecimal("12.99"), result.price)
        assertEquals(StockStatus.IN_STOCK, result.stockStatus)
        assertEquals(50, result.availableQty)
        assertEquals(true, result.isInStock)
    }

    @Test
    fun `variant should have correct stock status - in stock`() {
        val variant = Variant(
            id = 1L,
            productId = 1L,
            sku = "TEST-SKU",
            title = "Test Variant",
            price = BigDecimal("10.00"),
            weight = BigDecimal("0.100"),
            shippingWeight = BigDecimal("0.150"),
            stockQty = 20,
            reservedQty = 0
        )
        
        `when`(variantRepository.findById(1L)).thenReturn(Optional.of(variant))
        
        val result = productService.findVariantById(1L)
        
        assertNotNull(result)
        assertEquals(StockStatus.IN_STOCK, result.stockStatus)
    }

    @Test
    fun `variant should have correct stock status - low stock`() {
        val variant = Variant(
            id = 1L,
            productId = 1L,
            sku = "TEST-SKU",
            title = "Test Variant",
            price = BigDecimal("10.00"),
            weight = BigDecimal("0.100"),
            shippingWeight = BigDecimal("0.150"),
            stockQty = 3,
            reservedQty = 0
        )
        
        `when`(variantRepository.findById(1L)).thenReturn(Optional.of(variant))
        
        val result = productService.findVariantById(1L)
        
        assertNotNull(result)
        assertEquals(StockStatus.LOW_STOCK, result.stockStatus)
    }

    @Test
    fun `variant should have correct stock status - out of stock`() {
        val variant = Variant(
            id = 1L,
            productId = 1L,
            sku = "TEST-SKU",
            title = "Test Variant",
            price = BigDecimal("10.00"),
            weight = BigDecimal("0.100"),
            shippingWeight = BigDecimal("0.150"),
            stockQty = 5,
            reservedQty = 5
        )
        
        `when`(variantRepository.findById(1L)).thenReturn(Optional.of(variant))
        
        val result = productService.findVariantById(1L)
        
        assertNotNull(result)
        assertEquals(StockStatus.OUT_OF_STOCK, result.stockStatus)
        assertEquals(false, result.isInStock)
    }
}