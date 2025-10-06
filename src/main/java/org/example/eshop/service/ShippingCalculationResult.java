package org.example.eshop.service;

import java.math.BigDecimal;

public class ShippingCalculationResult {
    private final String zone;
    private final String zoneName; // nullable
    private final int weightGrams;
    private final BigDecimal cost; // nullable
    private final ShippingBracket bracket; // nullable
    private final String error; // nullable

    public ShippingCalculationResult(String zone, String zoneName, int weightGrams,
                                     BigDecimal cost, ShippingBracket bracket, String error) {
        this.zone = zone;
        this.zoneName = zoneName;
        this.weightGrams = weightGrams;
        this.cost = cost;
        this.bracket = bracket;
        this.error = error;
    }

    public String getZone() { return zone; }
    public String getZoneName() { return zoneName; }
    public int getWeightGrams() { return weightGrams; }
    public BigDecimal getCost() { return cost; }
    public ShippingBracket getBracket() { return bracket; }
    public String getError() { return error; }

    public boolean isValid() { return error == null && cost != null; }
}
