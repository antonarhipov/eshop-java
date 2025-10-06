package org.example.eshop.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class TotalsCalculatorService {
    private final VatCalculatorService vatCalculatorService;
    private final ShippingCalculatorService shippingCalculatorService;

    public TotalsCalculatorService(VatCalculatorService vatCalculatorService, ShippingCalculatorService shippingCalculatorService) {
        this.vatCalculatorService = vatCalculatorService;
        this.shippingCalculatorService = shippingCalculatorService;
    }

    /**
     * Calculates comprehensive totals for a list of items with shipping.
     */
    public OrderTotalsCalculation calculateTotals(List<OrderItemCalculation> items, String shippingZone, int totalShippingWeightGrams) {
        List<ItemTotalCalculation> itemTotals = new ArrayList<>();
        for (OrderItemCalculation item : items) {
            BigDecimal lineTotal = item.getVatInclusivePrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                    .setScale(2, RoundingMode.HALF_UP);
            BigDecimal lineVatAmount = vatCalculatorService.extractVatAmount(lineTotal);
            BigDecimal lineVatExclusiveTotal = vatCalculatorService.extractVatExclusivePrice(lineTotal);
            itemTotals.add(new ItemTotalCalculation(item, lineTotal, lineVatExclusiveTotal, lineVatAmount));
        }

        BigDecimal subtotalVatInclusive = itemTotals.stream().map(ItemTotalCalculation::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal subtotalVatExclusive = itemTotals.stream().map(ItemTotalCalculation::getLineVatExclusiveTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalVatAmount = itemTotals.stream().map(ItemTotalCalculation::getLineVatAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        ShippingCalculationResult shippingCalculation = shippingCalculatorService.calculateShippingWithDetails(
                shippingZone, totalShippingWeightGrams);
        BigDecimal shippingCost = shippingCalculation.getCost() != null ? shippingCalculation.getCost() : BigDecimal.ZERO;
        BigDecimal grandTotal = subtotalVatInclusive.add(shippingCost);

        return new OrderTotalsCalculation(
                itemTotals,
                subtotalVatInclusive,
                subtotalVatExclusive,
                totalVatAmount,
                shippingCalculation,
                shippingCost,
                grandTotal,
                vatCalculatorService.getVatRate()
        );
    }

    /**
     * Calculates totals for items only (without shipping).
     */
    public ItemsTotalsCalculation calculateItemTotals(List<OrderItemCalculation> items) {
        List<ItemTotalCalculation> itemTotals = new ArrayList<>();
        for (OrderItemCalculation item : items) {
            BigDecimal lineTotal = item.getVatInclusivePrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                    .setScale(2, RoundingMode.HALF_UP);
            BigDecimal lineVatAmount = vatCalculatorService.extractVatAmount(lineTotal);
            BigDecimal lineVatExclusiveTotal = vatCalculatorService.extractVatExclusivePrice(lineTotal);
            itemTotals.add(new ItemTotalCalculation(item, lineTotal, lineVatExclusiveTotal, lineVatAmount));
        }

        BigDecimal subtotalVatInclusive = itemTotals.stream().map(ItemTotalCalculation::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal subtotalVatExclusive = itemTotals.stream().map(ItemTotalCalculation::getLineVatExclusiveTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalVatAmount = itemTotals.stream().map(ItemTotalCalculation::getLineVatAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ItemsTotalsCalculation(
                itemTotals,
                subtotalVatInclusive,
                subtotalVatExclusive,
                totalVatAmount,
                vatCalculatorService.getVatRate()
        );
    }
}
