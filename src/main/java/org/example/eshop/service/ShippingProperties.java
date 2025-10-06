package org.example.eshop.service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ShippingProperties {
    private Map<String, ShippingZone> zones = new LinkedHashMap<>();

    public ShippingProperties() {
    }

    public ShippingProperties(Map<String, ShippingZone> zones) {
        this.zones = new LinkedHashMap<>(zones);
    }

    public Map<String, ShippingZone> getZones() {
        return zones;
    }

    public void setZones(Map<String, ShippingZone> zones) {
        this.zones = zones != null ? new LinkedHashMap<>(zones) : new LinkedHashMap<>();
    }

    @Override
    public String toString() {
        return "ShippingProperties{" +
                "zones=" + zones +
                '}';
    }
}
