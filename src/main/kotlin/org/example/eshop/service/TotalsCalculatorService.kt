package org.example.eshop.service

import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Service for calculating comprehensive order totals including VAT and shipping.
 * Combines VAT extraction and shipping calculations to provide complete order breakdowns.
 */
@Service
class TotalsCalculatorService(
    private val vatCalculatorService: VatCalculatorService,
    private val shippingCalculatorService: ShippingCalculatorService
) {

    /**
     * Calculates comprehensive totals for a list of items with shipping.
     *
     * @param items List of order items with VAT-inclusive prices
     * @param shippingZone The shipping zone (domestic, eu, row)
     * @param totalShippingWeightGrams Total shipping weight in grams
     * @return Complete totals breakdown
     */
    fun calculateTotals(
        items: List<OrderItemCalculation>,
        shippingZone: String,
        totalShippingWeightGrams: Int
    ): OrderTotalsCalculation {
        
        // Calculate item totals
        val itemTotals = items.map { item ->
            val lineTotal = item.vatInclusivePrice.multiply(BigDecimal(item.quantity))
                .setScale(2, RoundingMode.HALF_UP)
            val lineVatAmount = vatCalculatorService.extractVatAmount(lineTotal)
            val lineVatExclusiveTotal = vatCalculatorService.extractVatExclusivePrice(lineTotal)
            
            ItemTotalCalculation(
                item = item,
                lineTotal = lineTotal,
                lineVatExclusiveTotal = lineVatExclusiveTotal,
                lineVatAmount = lineVatAmount
            )
        }

        // Calculate subtotals
        val subtotalVatInclusive = itemTotals.sumOf { it.lineTotal }
        val subtotalVatExclusive = itemTotals.sumOf { it.lineVatExclusiveTotal }
        val totalVatAmount = itemTotals.sumOf { it.lineVatAmount }

        // Calculate shipping
        val shippingCalculation = shippingCalculatorService.calculateShippingWithDetails(
            shippingZone, 
            totalShippingWeightGrams
        )

        // Calculate final totals
        val shippingCost = shippingCalculation.cost ?: BigDecimal.ZERO
        val grandTotal = subtotalVatInclusive.add(shippingCost)

        return OrderTotalsCalculation(
            itemTotals = itemTotals,
            subtotalVatInclusive = subtotalVatInclusive,
            subtotalVatExclusive = subtotalVatExclusive,
            totalVatAmount = totalVatAmount,
            shippingCalculation = shippingCalculation,
            shippingCost = shippingCost,
            grandTotal = grandTotal,
            vatRate = vatCalculatorService.getVatRate()
        )
    }

    /**
     * Calculates totals for items only (without shipping).
     *
     * @param items List of order items with VAT-inclusive prices
     * @return Item totals breakdown without shipping
     */
    fun calculateItemTotals(items: List<OrderItemCalculation>): ItemsTotalsCalculation {
        val itemTotals = items.map { item ->
            val lineTotal = item.vatInclusivePrice.multiply(BigDecimal(item.quantity))
                .setScale(2, RoundingMode.HALF_UP)
            val lineVatAmount = vatCalculatorService.extractVatAmount(lineTotal)
            val lineVatExclusiveTotal = vatCalculatorService.extractVatExclusivePrice(lineTotal)
            
            ItemTotalCalculation(
                item = item,
                lineTotal = lineTotal,
                lineVatExclusiveTotal = lineVatExclusiveTotal,
                lineVatAmount = lineVatAmount
            )
        }

        val subtotalVatInclusive = itemTotals.sumOf { it.lineTotal }
        val subtotalVatExclusive = itemTotals.sumOf { it.lineVatExclusiveTotal }
        val totalVatAmount = itemTotals.sumOf { it.lineVatAmount }

        return ItemsTotalsCalculation(
            itemTotals = itemTotals,
            subtotalVatInclusive = subtotalVatInclusive,
            subtotalVatExclusive = subtotalVatExclusive,
            totalVatAmount = totalVatAmount,
            vatRate = vatCalculatorService.getVatRate()
        )
    }
}

/**
 * Represents an item for calculation purposes.
 */
data class OrderItemCalculation(
    val id: String,
    val title: String,
    val sku: String,
    val vatInclusivePrice: BigDecimal,
    val quantity: Int,
    val shippingWeightGrams: Int
)

/**
 * Calculation result for a single item line.
 */
data class ItemTotalCalculation(
    val item: OrderItemCalculation,
    val lineTotal: BigDecimal,
    val lineVatExclusiveTotal: BigDecimal,
    val lineVatAmount: BigDecimal
)

/**
 * Complete order totals calculation including shipping.
 */
data class OrderTotalsCalculation(
    val itemTotals: List<ItemTotalCalculation>,
    val subtotalVatInclusive: BigDecimal,
    val subtotalVatExclusive: BigDecimal,
    val totalVatAmount: BigDecimal,
    val shippingCalculation: ShippingCalculationResult,
    val shippingCost: BigDecimal,
    val grandTotal: BigDecimal,
    val vatRate: BigDecimal
) {
    val isValid: Boolean get() = shippingCalculation.isValid
    val errorMessage: String? get() = shippingCalculation.error
}

/**
 * Items-only totals calculation (without shipping).
 */
data class ItemsTotalsCalculation(
    val itemTotals: List<ItemTotalCalculation>,
    val subtotalVatInclusive: BigDecimal,
    val subtotalVatExclusive: BigDecimal,
    val totalVatAmount: BigDecimal,
    val vatRate: BigDecimal
)