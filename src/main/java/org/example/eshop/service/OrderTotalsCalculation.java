package org.example.eshop.service;

import java.math.BigDecimal;
import java.util.List;

public class OrderTotalsCalculation {
    private final List<ItemTotalCalculation> itemTotals;
    private final BigDecimal subtotalVatInclusive;
    private final BigDecimal subtotalVatExclusive;
    private final BigDecimal totalVatAmount;
    private final ShippingCalculationResult shippingCalculation;
    private final BigDecimal shippingCost;
    private final BigDecimal grandTotal;
    private final BigDecimal vatRate;

    public OrderTotalsCalculation(List<ItemTotalCalculation> itemTotals,
                                  BigDecimal subtotalVatInclusive,
                                  BigDecimal subtotalVatExclusive,
                                  BigDecimal totalVatAmount,
                                  ShippingCalculationResult shippingCalculation,
                                  BigDecimal shippingCost,
                                  BigDecimal grandTotal,
                                  BigDecimal vatRate) {
        this.itemTotals = itemTotals;
        this.subtotalVatInclusive = subtotalVatInclusive;
        this.subtotalVatExclusive = subtotalVatExclusive;
        this.totalVatAmount = totalVatAmount;
        this.shippingCalculation = shippingCalculation;
        this.shippingCost = shippingCost;
        this.grandTotal = grandTotal;
        this.vatRate = vatRate;
    }

    public List<ItemTotalCalculation> getItemTotals() { return itemTotals; }
    public BigDecimal getSubtotalVatInclusive() { return subtotalVatInclusive; }
    public BigDecimal getSubtotalVatExclusive() { return subtotalVatExclusive; }
    public BigDecimal getTotalVatAmount() { return totalVatAmount; }
    public ShippingCalculationResult getShippingCalculation() { return shippingCalculation; }
    public BigDecimal getShippingCost() { return shippingCost; }
    public BigDecimal getGrandTotal() { return grandTotal; }
    public BigDecimal getVatRate() { return vatRate; }

    public boolean isValid() { return shippingCalculation != null && shippingCalculation.isValid(); }
    public String getErrorMessage() { return shippingCalculation != null ? shippingCalculation.getError() : null; }
}
