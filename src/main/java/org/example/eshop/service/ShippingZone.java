package org.example.eshop.service;

import java.util.ArrayList;
import java.util.List;

public class ShippingZone {
    private String name = "";
    private List<ShippingBracket> brackets = new ArrayList<>();

    public ShippingZone() {
    }

    public ShippingZone(String name, List<ShippingBracket> brackets) {
        this.name = name;
        this.brackets = brackets != null ? new ArrayList<>(brackets) : new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ShippingBracket> getBrackets() {
        return brackets;
    }

    public void setBrackets(List<ShippingBracket> brackets) {
        this.brackets = brackets != null ? new ArrayList<>(brackets) : new ArrayList<>();
    }

    @Override
    public String toString() {
        return "ShippingZone{" +
                "name='" + name + '\'' +
                ", brackets=" + brackets +
                '}';
    }
}
