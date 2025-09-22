package org.example.eshop.service

import org.springframework.stereotype.Service
import java.math.BigDecimal

/**
 * Service for shipping cost calculations.
 * Calculates shipping costs based on destination zone and total weight.
 */
@Service
class ShippingCalculatorService(
    private val shopProperties: ShopProperties
) {

    /**
     * Calculates shipping cost for a given zone and weight.
     *
     * @param zone The shipping zone (domestic, eu, row)
     * @param totalWeightGrams The total weight in grams
     * @return The shipping cost, or null if zone is invalid or weight exceeds maximum bracket
     */
    fun calculateShippingCost(zone: String, totalWeightGrams: Int): BigDecimal? {
        val shippingZone = shopProperties.shipping.zones[zone.lowercase()] ?: return null
        
        // Find the appropriate bracket for the weight
        // Brackets are ordered by weight, find the first bracket that can handle this weight
        val bracket = shippingZone.brackets
            .sortedBy { it.weight }
            .firstOrNull { it.weight >= totalWeightGrams }
            
        return bracket?.cost
    }

    /**
     * Gets all available shipping zones.
     *
     * @return Map of zone keys to zone names
     */
    fun getAvailableZones(): Map<String, String> {
        return shopProperties.shipping.zones.mapValues { it.value.name }
    }

    /**
     * Gets the weight brackets for a specific zone.
     *
     * @param zone The shipping zone
     * @return List of shipping brackets for the zone, or empty list if zone doesn't exist
     */
    fun getZoneBrackets(zone: String): List<ShippingBracket> {
        return shopProperties.shipping.zones[zone.lowercase()]?.brackets ?: emptyList()
    }

    /**
     * Calculates shipping cost with detailed breakdown.
     *
     * @param zone The shipping zone
     * @param totalWeightGrams The total weight in grams
     * @return Shipping calculation result with details
     */
    fun calculateShippingWithDetails(zone: String, totalWeightGrams: Int): ShippingCalculationResult {
        val normalizedZone = zone.lowercase()
        val shippingZone = shopProperties.shipping.zones[normalizedZone]
        
        if (shippingZone == null) {
            return ShippingCalculationResult(
                zone = zone,
                zoneName = null,
                weightGrams = totalWeightGrams,
                cost = null,
                bracket = null,
                error = "Invalid shipping zone: $zone"
            )
        }

        val bracket = shippingZone.brackets
            .sortedBy { it.weight }
            .firstOrNull { it.weight >= totalWeightGrams }

        if (bracket == null) {
            return ShippingCalculationResult(
                zone = zone,
                zoneName = shippingZone.name,
                weightGrams = totalWeightGrams,
                cost = null,
                bracket = null,
                error = "Weight ${totalWeightGrams}g exceeds maximum shipping weight for zone ${shippingZone.name}"
            )
        }

        return ShippingCalculationResult(
            zone = zone,
            zoneName = shippingZone.name,
            weightGrams = totalWeightGrams,
            cost = bracket.cost,
            bracket = bracket,
            error = null
        )
    }
}

/**
 * Result of shipping calculation with detailed information.
 */
data class ShippingCalculationResult(
    val zone: String,
    val zoneName: String?,
    val weightGrams: Int,
    val cost: BigDecimal?,
    val bracket: ShippingBracket?,
    val error: String?
) {
    val isValid: Boolean get() = error == null && cost != null
}