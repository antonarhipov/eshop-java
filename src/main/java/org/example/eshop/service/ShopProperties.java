package org.example.eshop.service;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;
import java.util.Objects;

@ConfigurationProperties(prefix = "shop")
public class ShopProperties {
    private BigDecimal vatRate = new BigDecimal("0.20");
    private ShippingProperties shipping = new ShippingProperties();

    public ShopProperties() {
    }

    public ShopProperties(BigDecimal vatRate, ShippingProperties shipping) {
        this.vatRate = vatRate;
        this.shipping = shipping;
    }

    public BigDecimal getVatRate() {
        return vatRate;
    }

    public void setVatRate(BigDecimal vatRate) {
        this.vatRate = vatRate;
    }

    public ShippingProperties getShipping() {
        return shipping;
    }

    public void setShipping(ShippingProperties shipping) {
        this.shipping = shipping;
    }

    @Override
    public String toString() {
        return "ShopProperties{" +
                "vatRate=" + vatRate +
                ", shipping=" + shipping +
                '}';
    }
}
