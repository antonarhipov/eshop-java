package org.example.eshop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Shipping Calculator Service Tests")
class ShippingCalculatorServiceTest {

    private ShippingCalculatorService shippingCalculatorService;
    private ShopProperties shopProperties;

    @BeforeEach
    void setUp() {
        List<ShippingBracket> domesticBrackets = List.of(
                new ShippingBracket(500, new BigDecimal("5.00")),
                new ShippingBracket(1000, new BigDecimal("7.50")),
                new ShippingBracket(2000, new BigDecimal("10.00"))
        );

        List<ShippingBracket> euBrackets = List.of(
                new ShippingBracket(500, new BigDecimal("12.00")),
                new ShippingBracket(1000, new BigDecimal("18.00")),
                new ShippingBracket(2000, new BigDecimal("25.00"))
        );

        List<ShippingBracket> rowBrackets = List.of(
                new ShippingBracket(500, new BigDecimal("20.00")),
                new ShippingBracket(1000, new BigDecimal("30.00")),
                new ShippingBracket(2000, new BigDecimal("45.00"))
        );

        Map<String, ShippingZone> shippingZones = new LinkedHashMap<>();
        shippingZones.put("domestic", new ShippingZone("Domestic", domesticBrackets));
        shippingZones.put("eu", new ShippingZone("European Union", euBrackets));
        shippingZones.put("row", new ShippingZone("Rest of World", rowBrackets));

        shopProperties = new ShopProperties(new BigDecimal("0.20"), new ShippingProperties(shippingZones));
        shippingCalculatorService = new ShippingCalculatorService(shopProperties);
    }

    @Test
    @DisplayName("Should calculate correct shipping cost for domestic zone - first bracket")
    void testDomesticShippingFirstBracket() {
        BigDecimal cost = shippingCalculatorService.calculateShippingCost("domestic", 250);
        assertEquals(new BigDecimal("5.00"), cost);
    }

    @Test
    @DisplayName("Should calculate correct shipping cost for domestic zone - second bracket")
    void testDomesticShippingSecondBracket() {
        BigDecimal cost = shippingCalculatorService.calculateShippingCost("domestic", 750);
        assertEquals(new BigDecimal("7.50"), cost);
    }

    @Test
    @DisplayName("Should calculate correct shipping cost for domestic zone - third bracket")
    void testDomesticShippingThirdBracket() {
        BigDecimal cost = shippingCalculatorService.calculateShippingCost("domestic", 1500);
        assertEquals(new BigDecimal("10.00"), cost);
    }

    @Test
    @DisplayName("Should calculate correct shipping cost for EU zone")
    void testEuShipping() {
        BigDecimal cost = shippingCalculatorService.calculateShippingCost("eu", 250);
        assertEquals(new BigDecimal("12.00"), cost);

        BigDecimal cost2 = shippingCalculatorService.calculateShippingCost("eu", 750);
        assertEquals(new BigDecimal("18.00"), cost2);

        BigDecimal cost3 = shippingCalculatorService.calculateShippingCost("eu", 1500);
        assertEquals(new BigDecimal("25.00"), cost3);
    }

    @Test
    @DisplayName("Should calculate correct shipping cost for ROW zone")
    void testRowShipping() {
        BigDecimal cost = shippingCalculatorService.calculateShippingCost("row", 250);
        assertEquals(new BigDecimal("20.00"), cost);

        BigDecimal cost2 = shippingCalculatorService.calculateShippingCost("row", 750);
        assertEquals(new BigDecimal("30.00"), cost2);

        BigDecimal cost3 = shippingCalculatorService.calculateShippingCost("row", 1500);
        assertEquals(new BigDecimal("45.00"), cost3);
    }

    @Test
    @DisplayName("Should handle exact weight bracket boundaries")
    void testExactWeightBoundaries() {
        BigDecimal cost500 = shippingCalculatorService.calculateShippingCost("domestic", 500);
        assertEquals(new BigDecimal("5.00"), cost500);

        BigDecimal cost1000 = shippingCalculatorService.calculateShippingCost("domestic", 1000);
        assertEquals(new BigDecimal("7.50"), cost1000);

        BigDecimal cost2000 = shippingCalculatorService.calculateShippingCost("domestic", 2000);
        assertEquals(new BigDecimal("10.00"), cost2000);
    }

    @Test
    @DisplayName("Should return null for invalid shipping zone")
    void testInvalidShippingZone() {
        BigDecimal cost = shippingCalculatorService.calculateShippingCost("invalid", 250);
        assertNull(cost);
    }

    @Test
    @DisplayName("Should return null for weight exceeding maximum bracket")
    void testWeightExceedingMaximum() {
        BigDecimal cost = shippingCalculatorService.calculateShippingCost("domestic", 3000);
        assertNull(cost);
    }

    @Test
    @DisplayName("Should handle case-insensitive zone names")
    void testCaseInsensitiveZones() {
        BigDecimal cost1 = shippingCalculatorService.calculateShippingCost("DOMESTIC", 250);
        BigDecimal cost2 = shippingCalculatorService.calculateShippingCost("Domestic", 250);
        BigDecimal cost3 = shippingCalculatorService.calculateShippingCost("domestic", 250);

        assertEquals(new BigDecimal("5.00"), cost1);
        assertEquals(new BigDecimal("5.00"), cost2);
        assertEquals(new BigDecimal("5.00"), cost3);
    }

    @Test
    @DisplayName("Should return available zones correctly")
    void testGetAvailableZones() {
        Map<String, String> zones = shippingCalculatorService.getAvailableZones();

        assertEquals(3, zones.size());
        assertEquals("Domestic", zones.get("domestic"));
        assertEquals("European Union", zones.get("eu"));
        assertEquals("Rest of World", zones.get("row"));
    }

    @Test
    @DisplayName("Should return zone brackets correctly")
    void testGetZoneBrackets() {
        List<ShippingBracket> domesticBrackets = shippingCalculatorService.getZoneBrackets("domestic");

        assertEquals(3, domesticBrackets.size());
        assertEquals(500, domesticBrackets.get(0).getWeight());
        assertEquals(new BigDecimal("5.00"), domesticBrackets.get(0).getCost());
        assertEquals(1000, domesticBrackets.get(1).getWeight());
        assertEquals(new BigDecimal("7.50"), domesticBrackets.get(1).getCost());
        assertEquals(2000, domesticBrackets.get(2).getWeight());
        assertEquals(new BigDecimal("10.00"), domesticBrackets.get(2).getCost());
    }

    @Test
    @DisplayName("Should return empty list for invalid zone brackets")
    void testInvalidZoneBrackets() {
        assertTrue(shippingCalculatorService.getZoneBrackets("invalid").isEmpty());
    }

    @Test
    @DisplayName("Should calculate shipping with details - valid case")
    void testCalculateShippingWithDetailsValid() {
        ShippingCalculationResult result = shippingCalculatorService.calculateShippingWithDetails("domestic", 250);

        assertTrue(result.isValid());
        assertEquals("domestic", result.getZone());
        assertEquals("Domestic", result.getZoneName());
        assertEquals(250, result.getWeightGrams());
        assertEquals(new BigDecimal("5.00"), result.getCost());
        assertNotNull(result.getBracket());
        assertEquals(500, result.getBracket().getWeight());
        assertNull(result.getError());
    }

    @Test
    @DisplayName("Should calculate shipping with details - invalid zone")
    void testCalculateShippingWithDetailsInvalidZone() {
        ShippingCalculationResult result = shippingCalculatorService.calculateShippingWithDetails("invalid", 250);

        assertFalse(result.isValid());
        assertEquals("invalid", result.getZone());
        assertNull(result.getZoneName());
        assertEquals(250, result.getWeightGrams());
        assertNull(result.getCost());
        assertNull(result.getBracket());
        assertEquals("Invalid shipping zone: invalid", result.getError());
    }

    @Test
    @DisplayName("Should calculate shipping with details - weight exceeds maximum")
    void testCalculateShippingWithDetailsWeightExceeds() {
        ShippingCalculationResult result = shippingCalculatorService.calculateShippingWithDetails("domestic", 3000);

        assertFalse(result.isValid());
        assertEquals("domestic", result.getZone());
        assertEquals("Domestic", result.getZoneName());
        assertEquals(3000, result.getWeightGrams());
        assertNull(result.getCost());
        assertNull(result.getBracket());
        assertEquals("Weight 3000g exceeds maximum shipping weight for zone Domestic", result.getError());
    }

    @Test
    @DisplayName("Should handle zero weight")
    void testZeroWeight() {
        BigDecimal cost = shippingCalculatorService.calculateShippingCost("domestic", 0);
        assertEquals(new BigDecimal("5.00"), cost);
    }

    @Test
    @DisplayName("Should handle negative weight gracefully")
    void testNegativeWeight() {
        BigDecimal cost = shippingCalculatorService.calculateShippingCost("domestic", -100);
        assertEquals(new BigDecimal("5.00"), cost);
    }

    @Test
    @DisplayName("Should select correct bracket for weight just above boundary")
    void testWeightJustAboveBoundary() {
        BigDecimal cost = shippingCalculatorService.calculateShippingCost("domestic", 501);
        assertEquals(new BigDecimal("7.50"), cost);

        BigDecimal cost2 = shippingCalculatorService.calculateShippingCost("domestic", 1001);
        assertEquals(new BigDecimal("10.00"), cost2);
    }

    @Test
    @DisplayName("Should work with all zones for same weight")
    void testAllZonesSameWeight() {
        int weight = 250;

        BigDecimal domesticCost = shippingCalculatorService.calculateShippingCost("domestic", weight);
        BigDecimal euCost = shippingCalculatorService.calculateShippingCost("eu", weight);
        BigDecimal rowCost = shippingCalculatorService.calculateShippingCost("row", weight);

        assertEquals(new BigDecimal("5.00"), domesticCost);
        assertEquals(new BigDecimal("12.00"), euCost);
        assertEquals(new BigDecimal("20.00"), rowCost);

        assertTrue(rowCost.compareTo(euCost) > 0);
        assertTrue(euCost.compareTo(domesticCost) > 0);
    }
}
