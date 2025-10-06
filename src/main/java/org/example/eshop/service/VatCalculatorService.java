package org.example.eshop.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class VatCalculatorService {
    private final ShopProperties shopProperties;

    public VatCalculatorService(ShopProperties shopProperties) {
        this.shopProperties = shopProperties;
    }

    /**
     * Extracts the VAT amount from a VAT-inclusive price.
     * Formula: VAT = price - (price / (1 + vatRate))
     */
    public BigDecimal extractVatAmount(BigDecimal vatInclusivePrice) {
        BigDecimal vatRate = shopProperties.getVatRate();
        BigDecimal vatExclusivePrice = vatInclusivePrice.divide(
                BigDecimal.ONE.add(vatRate),
                4,
                RoundingMode.HALF_UP
        );
        return vatInclusivePrice.subtract(vatExclusivePrice).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculates the VAT-exclusive price from a VAT-inclusive price.
     * Formula: VAT-exclusive price = price / (1 + vatRate)
     */
    public BigDecimal extractVatExclusivePrice(BigDecimal vatInclusivePrice) {
        BigDecimal vatRate = shopProperties.getVatRate();
        return vatInclusivePrice.divide(
                BigDecimal.ONE.add(vatRate),
                2,
                RoundingMode.HALF_UP
        );
    }

    /**
     * Adds VAT to a VAT-exclusive price.
     * Formula: VAT-inclusive price = price * (1 + vatRate)
     */
    public BigDecimal addVat(BigDecimal vatExclusivePrice) {
        BigDecimal vatRate = shopProperties.getVatRate();
        return vatExclusivePrice.multiply(BigDecimal.ONE.add(vatRate))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getVatRate() {
        return shopProperties.getVatRate();
    }
}
