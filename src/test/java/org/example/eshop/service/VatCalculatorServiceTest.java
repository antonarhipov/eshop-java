package org.example.eshop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("VAT Calculator Service Tests")
class VatCalculatorServiceTest {

    private VatCalculatorService vatCalculatorService;
    private ShopProperties shopProperties;

    @BeforeEach
    void setUp() {
        shopProperties = new ShopProperties(new BigDecimal("0.20"), new ShippingProperties());
        vatCalculatorService = new VatCalculatorService(shopProperties);
    }

    @Test
    @DisplayName("Should extract correct VAT amount from VAT-inclusive price")
    void testExtractVatAmount() {
        BigDecimal vatInclusivePrice = new BigDecimal("12.00");
        BigDecimal expectedVatAmount = new BigDecimal("2.00");

        BigDecimal actual = vatCalculatorService.extractVatAmount(vatInclusivePrice);
        assertEquals(expectedVatAmount, actual);
    }

    @Test
    @DisplayName("Should extract correct VAT-exclusive price from VAT-inclusive price")
    void testExtractVatExclusivePrice() {
        BigDecimal vatInclusivePrice = new BigDecimal("12.00");
        BigDecimal expectedVatExclusivePrice = new BigDecimal("10.00");

        BigDecimal actual = vatCalculatorService.extractVatExclusivePrice(vatInclusivePrice);
        assertEquals(expectedVatExclusivePrice, actual);
    }

    @Test
    @DisplayName("Should add VAT correctly to VAT-exclusive price")
    void testAddVat() {
        BigDecimal vatExclusivePrice = new BigDecimal("10.00");
        BigDecimal expectedVatInclusivePrice = new BigDecimal("12.00");

        BigDecimal actual = vatCalculatorService.addVat(vatExclusivePrice);
        assertEquals(expectedVatInclusivePrice, actual);
    }

    @Test
    @DisplayName("Should handle complex price calculations with proper rounding")
    void testComplexPriceCalculations() {
        BigDecimal vatInclusivePrice = new BigDecimal("15.99");

        BigDecimal vatAmount = vatCalculatorService.extractVatAmount(vatInclusivePrice);
        BigDecimal vatExclusivePrice = vatCalculatorService.extractVatExclusivePrice(vatInclusivePrice);

        BigDecimal expectedVatExclusivePrice = new BigDecimal("13.33");
        BigDecimal expectedVatAmount = new BigDecimal("2.67");

        assertEquals(expectedVatExclusivePrice, vatExclusivePrice);
        assertEquals(expectedVatAmount, vatAmount);

        BigDecimal reconstructed = vatExclusivePrice.add(vatAmount);
        assertTrue(reconstructed.subtract(vatInclusivePrice).abs().compareTo(new BigDecimal("0.01")) <= 0);
    }

    @Test
    @DisplayName("Should handle zero price correctly")
    void testZeroPrice() {
        BigDecimal zero = BigDecimal.ZERO;

        BigDecimal vatAmount = vatCalculatorService.extractVatAmount(zero);
        BigDecimal vatExclusive = vatCalculatorService.extractVatExclusivePrice(zero);
        BigDecimal vatInclusive = vatCalculatorService.addVat(zero);

        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.UNNECESSARY), vatAmount);
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.UNNECESSARY), vatExclusive);
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.UNNECESSARY), vatInclusive);
    }

    @Test
    @DisplayName("Should handle small amounts with proper precision")
    void testSmallAmounts() {
        BigDecimal vatInclusivePrice = new BigDecimal("0.01");

        BigDecimal vatAmount = vatCalculatorService.extractVatAmount(vatInclusivePrice);
        BigDecimal vatExclusivePrice = vatCalculatorService.extractVatExclusivePrice(vatInclusivePrice);

        assertNotNull(vatAmount);
        assertNotNull(vatExclusivePrice);
        assertTrue(vatAmount.compareTo(BigDecimal.ZERO) >= 0);
        assertTrue(vatExclusivePrice.compareTo(BigDecimal.ZERO) >= 0);
    }

    @Test
    @DisplayName("Should return correct VAT rate")
    void testGetVatRate() {
        assertEquals(new BigDecimal("0.20"), vatCalculatorService.getVatRate());
    }

    @Test
    @DisplayName("Should work with different VAT rates")
    void testDifferentVatRates() {
        ShopProperties custom = new ShopProperties(new BigDecimal("0.10"), new ShippingProperties());
        VatCalculatorService customService = new VatCalculatorService(custom);

        BigDecimal vatInclusivePrice = new BigDecimal("11.00");
        BigDecimal expectedVatExclusivePrice = new BigDecimal("10.00");
        BigDecimal expectedVatAmount = new BigDecimal("1.00");

        BigDecimal actualExclusive = customService.extractVatExclusivePrice(vatInclusivePrice);
        BigDecimal actualVat = customService.extractVatAmount(vatInclusivePrice);

        assertEquals(expectedVatExclusivePrice, actualExclusive);
        assertEquals(expectedVatAmount, actualVat);
    }

    @Test
    @DisplayName("Should maintain precision in round-trip calculations")
    void testRoundTripCalculations() {
        BigDecimal originalVatExclusivePrice = new BigDecimal("25.50");
        BigDecimal vatInclusivePrice = vatCalculatorService.addVat(originalVatExclusivePrice);
        BigDecimal extracted = vatCalculatorService.extractVatExclusivePrice(vatInclusivePrice);
        assertEquals(originalVatExclusivePrice, extracted);
    }

    @Test
    @DisplayName("Should handle large amounts correctly")
    void testLargeAmounts() {
        BigDecimal largeVatInclusivePrice = new BigDecimal("999999.99");

        BigDecimal vatAmount = vatCalculatorService.extractVatAmount(largeVatInclusivePrice);
        BigDecimal vatExclusivePrice = vatCalculatorService.extractVatExclusivePrice(largeVatInclusivePrice);

        assertNotNull(vatAmount);
        assertNotNull(vatExclusivePrice);
        assertTrue(vatAmount.compareTo(BigDecimal.ZERO) > 0);
        assertTrue(vatExclusivePrice.compareTo(BigDecimal.ZERO) > 0);

        BigDecimal expectedVatAmount = largeVatInclusivePrice.multiply(new BigDecimal("0.20"))
                .divide(new BigDecimal("1.20"), 2, RoundingMode.HALF_UP);
        assertTrue(vatAmount.subtract(expectedVatAmount).abs().compareTo(new BigDecimal("1.00")) < 0);
    }
}
