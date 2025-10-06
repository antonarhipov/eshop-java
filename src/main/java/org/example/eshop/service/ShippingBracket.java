package org.example.eshop.service;

import java.math.BigDecimal;

public class ShippingBracket {
    private int weight = 0; // grams
    private BigDecimal cost = BigDecimal.ZERO; // currency amount

    public ShippingBracket() {
    }

    public ShippingBracket(int weight, BigDecimal cost) {
        this.weight = weight;
        this.cost = cost;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    @Override
    public String toString() {
        return "ShippingBracket{" +
                "weight=" + weight +
                ", cost=" + cost +
                '}';
    }
}
