package org.example.eshop.service

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Service for VAT calculations.
 * Handles extraction of VAT from VAT-inclusive prices and calculation of VAT amounts.
 */
@Service
class VatCalculatorService(
    private val shopProperties: ShopProperties
) {

    /**
     * Extracts the VAT amount from a VAT-inclusive price.
     * Formula: VAT = price - (price / (1 + vatRate))
     *
     * @param vatInclusivePrice The price that includes VAT
     * @return The VAT amount extracted from the price
     */
    fun extractVatAmount(vatInclusivePrice: BigDecimal): BigDecimal {
        val vatRate = shopProperties.vatRate
        val vatExclusivePrice = vatInclusivePrice.divide(
            BigDecimal.ONE.add(vatRate), 
            4, 
            RoundingMode.HALF_UP
        )
        return vatInclusivePrice.subtract(vatExclusivePrice).setScale(2, RoundingMode.HALF_UP)
    }

    /**
     * Calculates the VAT-exclusive price from a VAT-inclusive price.
     * Formula: VAT-exclusive price = price / (1 + vatRate)
     *
     * @param vatInclusivePrice The price that includes VAT
     * @return The price without VAT
     */
    fun extractVatExclusivePrice(vatInclusivePrice: BigDecimal): BigDecimal {
        val vatRate = shopProperties.vatRate
        return vatInclusivePrice.divide(
            BigDecimal.ONE.add(vatRate), 
            2, 
            RoundingMode.HALF_UP
        )
    }

    /**
     * Adds VAT to a VAT-exclusive price.
     * Formula: VAT-inclusive price = price * (1 + vatRate)
     *
     * @param vatExclusivePrice The price without VAT
     * @return The price with VAT included
     */
    fun addVat(vatExclusivePrice: BigDecimal): BigDecimal {
        val vatRate = shopProperties.vatRate
        return vatExclusivePrice.multiply(BigDecimal.ONE.add(vatRate))
            .setScale(2, RoundingMode.HALF_UP)
    }

    /**
     * Gets the current VAT rate as a percentage (e.g., 0.20 for 20%).
     *
     * @return The VAT rate
     */
    fun getVatRate(): BigDecimal = shopProperties.vatRate
}

/**
 * Configuration properties for shop settings.
 */
@ConfigurationProperties(prefix = "shop")
data class ShopProperties(
    val vatRate: BigDecimal = BigDecimal("0.20"),
    val shipping: ShippingProperties = ShippingProperties()
)

/**
 * Configuration properties for shipping settings.
 */
data class ShippingProperties(
    val zones: Map<String, ShippingZone> = emptyMap()
)

/**
 * Represents a shipping zone with its brackets.
 */
data class ShippingZone(
    val name: String = "",
    val brackets: List<ShippingBracket> = emptyList()
)

/**
 * Represents a shipping bracket with weight limit and cost.
 */
data class ShippingBracket(
    val weight: Int = 0, // weight in grams
    val cost: BigDecimal = BigDecimal.ZERO
)