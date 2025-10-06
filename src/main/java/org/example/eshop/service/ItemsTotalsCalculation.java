package org.example.eshop.service;

import java.math.BigDecimal;
import java.util.List;

public class ItemsTotalsCalculation {
    private final List<ItemTotalCalculation> itemTotals;
    private final BigDecimal subtotalVatInclusive;
    private final BigDecimal subtotalVatExclusive;
    private final BigDecimal totalVatAmount;
    private final BigDecimal vatRate;

    public ItemsTotalsCalculation(List<ItemTotalCalculation> itemTotals,
                                  BigDecimal subtotalVatInclusive,
                                  BigDecimal subtotalVatExclusive,
                                  BigDecimal totalVatAmount,
                                  BigDecimal vatRate) {
        this.itemTotals = itemTotals;
        this.subtotalVatInclusive = subtotalVatInclusive;
        this.subtotalVatExclusive = subtotalVatExclusive;
        this.totalVatAmount = totalVatAmount;
        this.vatRate = vatRate;
    }

    public List<ItemTotalCalculation> getItemTotals() { return itemTotals; }
    public BigDecimal getSubtotalVatInclusive() { return subtotalVatInclusive; }
    public BigDecimal getSubtotalVatExclusive() { return subtotalVatExclusive; }
    public BigDecimal getTotalVatAmount() { return totalVatAmount; }
    public BigDecimal getVatRate() { return vatRate; }
}
