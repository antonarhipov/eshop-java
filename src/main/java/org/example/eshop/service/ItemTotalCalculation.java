package org.example.eshop.service;

import java.math.BigDecimal;

public class ItemTotalCalculation {
    private final OrderItemCalculation item;
    private final BigDecimal lineTotal;
    private final BigDecimal lineVatExclusiveTotal;
    private final BigDecimal lineVatAmount;

    public ItemTotalCalculation(OrderItemCalculation item, BigDecimal lineTotal, BigDecimal lineVatExclusiveTotal, BigDecimal lineVatAmount) {
        this.item = item;
        this.lineTotal = lineTotal;
        this.lineVatExclusiveTotal = lineVatExclusiveTotal;
        this.lineVatAmount = lineVatAmount;
    }

    public OrderItemCalculation getItem() { return item; }
    public BigDecimal getLineTotal() { return lineTotal; }
    public BigDecimal getLineVatExclusiveTotal() { return lineVatExclusiveTotal; }
    public BigDecimal getLineVatAmount() { return lineVatAmount; }
}
