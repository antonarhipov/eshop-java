package org.example.eshop.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import java.math.BigDecimal

@DisplayName("Shipping Calculator Service Tests")
class ShippingCalculatorServiceTest {

    private lateinit var shippingCalculatorService: ShippingCalculatorService
    private lateinit var shopProperties: ShopProperties

    @BeforeEach
    fun setUp() {
        // Set up with the same configuration as in application.yml
        val domesticBrackets = listOf(
            ShippingBracket(500, BigDecimal("5.00")),
            ShippingBracket(1000, BigDecimal("7.50")),
            ShippingBracket(2000, BigDecimal("10.00"))
        )
        
        val euBrackets = listOf(
            ShippingBracket(500, BigDecimal("12.00")),
            ShippingBracket(1000, BigDecimal("18.00")),
            ShippingBracket(2000, BigDecimal("25.00"))
        )
        
        val rowBrackets = listOf(
            ShippingBracket(500, BigDecimal("20.00")),
            ShippingBracket(1000, BigDecimal("30.00")),
            ShippingBracket(2000, BigDecimal("45.00"))
        )

        val shippingZones = mapOf(
            "domestic" to ShippingZone("Domestic", domesticBrackets),
            "eu" to ShippingZone("European Union", euBrackets),
            "row" to ShippingZone("Rest of World", rowBrackets)
        )

        shopProperties = ShopProperties(
            vatRate = BigDecimal("0.20"),
            shipping = ShippingProperties(shippingZones)
        )
        
        shippingCalculatorService = ShippingCalculatorService(shopProperties)
    }

    @Test
    @DisplayName("Should calculate correct shipping cost for domestic zone - first bracket")
    fun testDomesticShippingFirstBracket() {
        val cost = shippingCalculatorService.calculateShippingCost("domestic", 250)
        assertEquals(BigDecimal("5.00"), cost)
    }

    @Test
    @DisplayName("Should calculate correct shipping cost for domestic zone - second bracket")
    fun testDomesticShippingSecondBracket() {
        val cost = shippingCalculatorService.calculateShippingCost("domestic", 750)
        assertEquals(BigDecimal("7.50"), cost)
    }

    @Test
    @DisplayName("Should calculate correct shipping cost for domestic zone - third bracket")
    fun testDomesticShippingThirdBracket() {
        val cost = shippingCalculatorService.calculateShippingCost("domestic", 1500)
        assertEquals(BigDecimal("10.00"), cost)
    }

    @Test
    @DisplayName("Should calculate correct shipping cost for EU zone")
    fun testEuShipping() {
        val cost = shippingCalculatorService.calculateShippingCost("eu", 250)
        assertEquals(BigDecimal("12.00"), cost)
        
        val cost2 = shippingCalculatorService.calculateShippingCost("eu", 750)
        assertEquals(BigDecimal("18.00"), cost2)
        
        val cost3 = shippingCalculatorService.calculateShippingCost("eu", 1500)
        assertEquals(BigDecimal("25.00"), cost3)
    }

    @Test
    @DisplayName("Should calculate correct shipping cost for ROW zone")
    fun testRowShipping() {
        val cost = shippingCalculatorService.calculateShippingCost("row", 250)
        assertEquals(BigDecimal("20.00"), cost)
        
        val cost2 = shippingCalculatorService.calculateShippingCost("row", 750)
        assertEquals(BigDecimal("30.00"), cost2)
        
        val cost3 = shippingCalculatorService.calculateShippingCost("row", 1500)
        assertEquals(BigDecimal("45.00"), cost3)
    }

    @Test
    @DisplayName("Should handle exact weight bracket boundaries")
    fun testExactWeightBoundaries() {
        // Test exact boundary weights
        val cost500 = shippingCalculatorService.calculateShippingCost("domestic", 500)
        assertEquals(BigDecimal("5.00"), cost500)
        
        val cost1000 = shippingCalculatorService.calculateShippingCost("domestic", 1000)
        assertEquals(BigDecimal("7.50"), cost1000)
        
        val cost2000 = shippingCalculatorService.calculateShippingCost("domestic", 2000)
        assertEquals(BigDecimal("10.00"), cost2000)
    }

    @Test
    @DisplayName("Should return null for invalid shipping zone")
    fun testInvalidShippingZone() {
        val cost = shippingCalculatorService.calculateShippingCost("invalid", 250)
        assertNull(cost)
    }

    @Test
    @DisplayName("Should return null for weight exceeding maximum bracket")
    fun testWeightExceedingMaximum() {
        val cost = shippingCalculatorService.calculateShippingCost("domestic", 3000)
        assertNull(cost)
    }

    @Test
    @DisplayName("Should handle case-insensitive zone names")
    fun testCaseInsensitiveZones() {
        val cost1 = shippingCalculatorService.calculateShippingCost("DOMESTIC", 250)
        val cost2 = shippingCalculatorService.calculateShippingCost("Domestic", 250)
        val cost3 = shippingCalculatorService.calculateShippingCost("domestic", 250)
        
        assertEquals(BigDecimal("5.00"), cost1)
        assertEquals(BigDecimal("5.00"), cost2)
        assertEquals(BigDecimal("5.00"), cost3)
    }

    @Test
    @DisplayName("Should return available zones correctly")
    fun testGetAvailableZones() {
        val zones = shippingCalculatorService.getAvailableZones()
        
        assertEquals(3, zones.size)
        assertEquals("Domestic", zones["domestic"])
        assertEquals("European Union", zones["eu"])
        assertEquals("Rest of World", zones["row"])
    }

    @Test
    @DisplayName("Should return zone brackets correctly")
    fun testGetZoneBrackets() {
        val domesticBrackets = shippingCalculatorService.getZoneBrackets("domestic")
        
        assertEquals(3, domesticBrackets.size)
        assertEquals(500, domesticBrackets[0].weight)
        assertEquals(BigDecimal("5.00"), domesticBrackets[0].cost)
        assertEquals(1000, domesticBrackets[1].weight)
        assertEquals(BigDecimal("7.50"), domesticBrackets[1].cost)
        assertEquals(2000, domesticBrackets[2].weight)
        assertEquals(BigDecimal("10.00"), domesticBrackets[2].cost)
    }

    @Test
    @DisplayName("Should return empty list for invalid zone brackets")
    fun testInvalidZoneBrackets() {
        val brackets = shippingCalculatorService.getZoneBrackets("invalid")
        assertTrue(brackets.isEmpty())
    }

    @Test
    @DisplayName("Should calculate shipping with details - valid case")
    fun testCalculateShippingWithDetailsValid() {
        val result = shippingCalculatorService.calculateShippingWithDetails("domestic", 250)
        
        assertTrue(result.isValid)
        assertEquals("domestic", result.zone)
        assertEquals("Domestic", result.zoneName)
        assertEquals(250, result.weightGrams)
        assertEquals(BigDecimal("5.00"), result.cost)
        assertNotNull(result.bracket)
        assertEquals(500, result.bracket?.weight)
        assertNull(result.error)
    }

    @Test
    @DisplayName("Should calculate shipping with details - invalid zone")
    fun testCalculateShippingWithDetailsInvalidZone() {
        val result = shippingCalculatorService.calculateShippingWithDetails("invalid", 250)
        
        assertFalse(result.isValid)
        assertEquals("invalid", result.zone)
        assertNull(result.zoneName)
        assertEquals(250, result.weightGrams)
        assertNull(result.cost)
        assertNull(result.bracket)
        assertEquals("Invalid shipping zone: invalid", result.error)
    }

    @Test
    @DisplayName("Should calculate shipping with details - weight exceeds maximum")
    fun testCalculateShippingWithDetailsWeightExceeds() {
        val result = shippingCalculatorService.calculateShippingWithDetails("domestic", 3000)
        
        assertFalse(result.isValid)
        assertEquals("domestic", result.zone)
        assertEquals("Domestic", result.zoneName)
        assertEquals(3000, result.weightGrams)
        assertNull(result.cost)
        assertNull(result.bracket)
        assertEquals("Weight 3000g exceeds maximum shipping weight for zone Domestic", result.error)
    }

    @Test
    @DisplayName("Should handle zero weight")
    fun testZeroWeight() {
        val cost = shippingCalculatorService.calculateShippingCost("domestic", 0)
        assertEquals(BigDecimal("5.00"), cost) // Should use first bracket
    }

    @Test
    @DisplayName("Should handle negative weight gracefully")
    fun testNegativeWeight() {
        val cost = shippingCalculatorService.calculateShippingCost("domestic", -100)
        assertEquals(BigDecimal("5.00"), cost) // Should use first bracket
    }

    @Test
    @DisplayName("Should select correct bracket for weight just above boundary")
    fun testWeightJustAboveBoundary() {
        // 501g should use second bracket (1000g, £7.50)
        val cost = shippingCalculatorService.calculateShippingCost("domestic", 501)
        assertEquals(BigDecimal("7.50"), cost)
        
        // 1001g should use third bracket (2000g, £10.00)
        val cost2 = shippingCalculatorService.calculateShippingCost("domestic", 1001)
        assertEquals(BigDecimal("10.00"), cost2)
    }

    @Test
    @DisplayName("Should work with all zones for same weight")
    fun testAllZonesSameWeight() {
        val weight = 250
        
        val domesticCost = shippingCalculatorService.calculateShippingCost("domestic", weight)
        val euCost = shippingCalculatorService.calculateShippingCost("eu", weight)
        val rowCost = shippingCalculatorService.calculateShippingCost("row", weight)
        
        assertEquals(BigDecimal("5.00"), domesticCost)
        assertEquals(BigDecimal("12.00"), euCost)
        assertEquals(BigDecimal("20.00"), rowCost)
        
        // Verify ROW is most expensive, domestic is cheapest
        assertTrue(rowCost!! > euCost!!)
        assertTrue(euCost > domesticCost!!)
    }
}