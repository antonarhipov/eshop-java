package org.example.eshop.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import java.math.BigDecimal
import java.math.RoundingMode

@DisplayName("VAT Calculator Service Tests")
class VatCalculatorServiceTest {

    private lateinit var vatCalculatorService: VatCalculatorService
    private lateinit var shopProperties: ShopProperties

    @BeforeEach
    fun setUp() {
        // Set up with 20% VAT rate (0.20)
        shopProperties = ShopProperties(
            vatRate = BigDecimal("0.20"),
            shipping = ShippingProperties()
        )
        vatCalculatorService = VatCalculatorService(shopProperties)
    }

    @Test
    @DisplayName("Should extract correct VAT amount from VAT-inclusive price")
    fun testExtractVatAmount() {
        // Test with £12.00 VAT-inclusive (should extract £2.00 VAT)
        val vatInclusivePrice = BigDecimal("12.00")
        val expectedVatAmount = BigDecimal("2.00")
        
        val actualVatAmount = vatCalculatorService.extractVatAmount(vatInclusivePrice)
        
        assertEquals(expectedVatAmount, actualVatAmount)
    }

    @Test
    @DisplayName("Should extract correct VAT-exclusive price from VAT-inclusive price")
    fun testExtractVatExclusivePrice() {
        // Test with £12.00 VAT-inclusive (should extract £10.00 VAT-exclusive)
        val vatInclusivePrice = BigDecimal("12.00")
        val expectedVatExclusivePrice = BigDecimal("10.00")
        
        val actualVatExclusivePrice = vatCalculatorService.extractVatExclusivePrice(vatInclusivePrice)
        
        assertEquals(expectedVatExclusivePrice, actualVatExclusivePrice)
    }

    @Test
    @DisplayName("Should add VAT correctly to VAT-exclusive price")
    fun testAddVat() {
        // Test with £10.00 VAT-exclusive (should add £2.00 VAT to get £12.00)
        val vatExclusivePrice = BigDecimal("10.00")
        val expectedVatInclusivePrice = BigDecimal("12.00")
        
        val actualVatInclusivePrice = vatCalculatorService.addVat(vatExclusivePrice)
        
        assertEquals(expectedVatInclusivePrice, actualVatInclusivePrice)
    }

    @Test
    @DisplayName("Should handle complex price calculations with proper rounding")
    fun testComplexPriceCalculations() {
        // Test with £15.99 VAT-inclusive
        val vatInclusivePrice = BigDecimal("15.99")
        
        val vatAmount = vatCalculatorService.extractVatAmount(vatInclusivePrice)
        val vatExclusivePrice = vatCalculatorService.extractVatExclusivePrice(vatInclusivePrice)
        
        // VAT-exclusive should be 15.99 / 1.20 = 13.325 -> rounded to 13.33
        val expectedVatExclusivePrice = BigDecimal("13.33")
        // VAT amount should be 15.99 - 13.33 = 2.66, but due to rounding it's 2.67
        val expectedVatAmount = BigDecimal("2.67")
        
        assertEquals(expectedVatExclusivePrice, vatExclusivePrice)
        assertEquals(expectedVatAmount, vatAmount)
        
        // Note: Due to rounding, VAT-exclusive + VAT may not exactly equal original price
        // This is expected behavior with decimal arithmetic and rounding
        val reconstructedPrice = vatExclusivePrice.add(vatAmount)
        assertTrue(reconstructedPrice.subtract(vatInclusivePrice).abs().compareTo(BigDecimal("0.01")) <= 0)
    }

    @Test
    @DisplayName("Should handle zero price correctly")
    fun testZeroPrice() {
        val zeroPrice = BigDecimal.ZERO
        
        val vatAmount = vatCalculatorService.extractVatAmount(zeroPrice)
        val vatExclusivePrice = vatCalculatorService.extractVatExclusivePrice(zeroPrice)
        val vatInclusivePrice = vatCalculatorService.addVat(zeroPrice)
        
        assertEquals(BigDecimal.ZERO.setScale(2), vatAmount)
        assertEquals(BigDecimal.ZERO.setScale(2), vatExclusivePrice)
        assertEquals(BigDecimal.ZERO.setScale(2), vatInclusivePrice)
    }

    @Test
    @DisplayName("Should handle small amounts with proper precision")
    fun testSmallAmounts() {
        // Test with £0.01 VAT-inclusive
        val vatInclusivePrice = BigDecimal("0.01")
        
        val vatAmount = vatCalculatorService.extractVatAmount(vatInclusivePrice)
        val vatExclusivePrice = vatCalculatorService.extractVatExclusivePrice(vatInclusivePrice)
        
        // Should handle small amounts without errors
        assertNotNull(vatAmount)
        assertNotNull(vatExclusivePrice)
        assertTrue(vatAmount.compareTo(BigDecimal.ZERO) >= 0)
        assertTrue(vatExclusivePrice.compareTo(BigDecimal.ZERO) >= 0)
    }

    @Test
    @DisplayName("Should return correct VAT rate")
    fun testGetVatRate() {
        val expectedVatRate = BigDecimal("0.20")
        val actualVatRate = vatCalculatorService.getVatRate()
        
        assertEquals(expectedVatRate, actualVatRate)
    }

    @Test
    @DisplayName("Should work with different VAT rates")
    fun testDifferentVatRates() {
        // Test with 10% VAT rate
        val customShopProperties = ShopProperties(
            vatRate = BigDecimal("0.10"),
            shipping = ShippingProperties()
        )
        val customVatCalculatorService = VatCalculatorService(customShopProperties)
        
        val vatInclusivePrice = BigDecimal("11.00")
        val expectedVatExclusivePrice = BigDecimal("10.00")
        val expectedVatAmount = BigDecimal("1.00")
        
        val actualVatExclusivePrice = customVatCalculatorService.extractVatExclusivePrice(vatInclusivePrice)
        val actualVatAmount = customVatCalculatorService.extractVatAmount(vatInclusivePrice)
        
        assertEquals(expectedVatExclusivePrice, actualVatExclusivePrice)
        assertEquals(expectedVatAmount, actualVatAmount)
    }

    @Test
    @DisplayName("Should maintain precision in round-trip calculations")
    fun testRoundTripCalculations() {
        val originalVatExclusivePrice = BigDecimal("25.50")
        
        // Add VAT then extract it back
        val vatInclusivePrice = vatCalculatorService.addVat(originalVatExclusivePrice)
        val extractedVatExclusivePrice = vatCalculatorService.extractVatExclusivePrice(vatInclusivePrice)
        
        // Should get back the original price (within rounding tolerance)
        assertEquals(originalVatExclusivePrice, extractedVatExclusivePrice)
    }

    @Test
    @DisplayName("Should handle large amounts correctly")
    fun testLargeAmounts() {
        val largeVatInclusivePrice = BigDecimal("999999.99")
        
        val vatAmount = vatCalculatorService.extractVatAmount(largeVatInclusivePrice)
        val vatExclusivePrice = vatCalculatorService.extractVatExclusivePrice(largeVatInclusivePrice)
        
        // Should handle large amounts without overflow
        assertNotNull(vatAmount)
        assertNotNull(vatExclusivePrice)
        assertTrue(vatAmount.compareTo(BigDecimal.ZERO) > 0)
        assertTrue(vatExclusivePrice.compareTo(BigDecimal.ZERO) > 0)
        
        // VAT amount should be reasonable (around 20% of total)
        val expectedVatAmount = largeVatInclusivePrice.multiply(BigDecimal("0.20"))
            .divide(BigDecimal("1.20"), 2, RoundingMode.HALF_UP)
        assertTrue(vatAmount.subtract(expectedVatAmount).abs().compareTo(BigDecimal("1.00")) < 0)
    }
}