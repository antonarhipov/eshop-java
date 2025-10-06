package org.example.eshop.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ShippingCalculatorService {
    private final ShopProperties shopProperties;

    public ShippingCalculatorService(ShopProperties shopProperties) {
        this.shopProperties = shopProperties;
    }

    /**
     * Calculates shipping cost for a given zone and weight.
     * @return The shipping cost, or null if zone is invalid or weight exceeds maximum bracket
     */
    public BigDecimal calculateShippingCost(String zone, int totalWeightGrams) {
        if (zone == null) return null;
        ShippingZone shippingZone = shopProperties.getShipping().getZones().get(zone.toLowerCase(Locale.ROOT));
        if (shippingZone == null) return null;

        ShippingBracket bracket = shippingZone.getBrackets().stream()
                .sorted(Comparator.comparingInt(ShippingBracket::getWeight))
                .filter(b -> b.getWeight() >= totalWeightGrams)
                .findFirst()
                .orElse(null);
        return bracket != null ? bracket.getCost() : null;
    }

    /**
     * Gets all available shipping zones.
     */
    public Map<String, String> getAvailableZones() {
        Map<String, ShippingZone> zones = shopProperties.getShipping().getZones();
        // Preserve iteration order of the underlying map when possible
        Map<String, String> result = new LinkedHashMap<>();
        zones.forEach((k, v) -> result.put(k, v != null ? v.getName() : null));
        return result;
    }

    /**
     * Gets the weight brackets for a specific zone.
     */
    public List<ShippingBracket> getZoneBrackets(String zone) {
        if (zone == null) return List.of();
        ShippingZone shippingZone = shopProperties.getShipping().getZones().get(zone.toLowerCase(Locale.ROOT));
        if (shippingZone == null || shippingZone.getBrackets() == null) return List.of();
        return shippingZone.getBrackets();
    }

    /**
     * Calculates shipping cost with detailed breakdown.
     */
    public ShippingCalculationResult calculateShippingWithDetails(String zone, int totalWeightGrams) {
        String normalized = zone == null ? null : zone.toLowerCase(Locale.ROOT);
        ShippingZone shippingZone = normalized == null ? null : shopProperties.getShipping().getZones().get(normalized);

        if (shippingZone == null) {
            return new ShippingCalculationResult(zone, null, totalWeightGrams, null, null,
                    "Invalid shipping zone: " + zone);
        }

        ShippingBracket bracket = shippingZone.getBrackets().stream()
                .sorted(Comparator.comparingInt(ShippingBracket::getWeight))
                .filter(b -> b.getWeight() >= totalWeightGrams)
                .findFirst()
                .orElse(null);

        if (bracket == null) {
            return new ShippingCalculationResult(zone, shippingZone.getName(), totalWeightGrams, null, null,
                    "Weight " + totalWeightGrams + "g exceeds maximum shipping weight for zone " + shippingZone.getName());
        }

        return new ShippingCalculationResult(zone, shippingZone.getName(), totalWeightGrams, bracket.getCost(), bracket, null);
    }
}
