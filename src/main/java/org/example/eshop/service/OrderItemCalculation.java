package org.example.eshop.service;

import java.math.BigDecimal;

public class OrderItemCalculation {
    private final String id;
    private final String title;
    private final String sku;
    private final BigDecimal vatInclusivePrice;
    private final int quantity;
    private final int shippingWeightGrams;

    public OrderItemCalculation(String id, String title, String sku, BigDecimal vatInclusivePrice, int quantity, int shippingWeightGrams) {
        this.id = id;
        this.title = title;
        this.sku = sku;
        this.vatInclusivePrice = vatInclusivePrice;
        this.quantity = quantity;
        this.shippingWeightGrams = shippingWeightGrams;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getSku() { return sku; }
    public BigDecimal getVatInclusivePrice() { return vatInclusivePrice; }
    public int getQuantity() { return quantity; }
    public int getShippingWeightGrams() { return shippingWeightGrams; }
}
