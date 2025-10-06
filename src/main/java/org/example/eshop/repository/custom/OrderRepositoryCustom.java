package org.example.eshop.repository.custom;

import org.example.eshop.entity.Order;

public interface OrderRepositoryCustom {
    /**
     * Returns the order by number and ensures items are loaded in the same persistence context.
     * Returns null when not found.
     */
    Order findByNumberWithItems(String number);
}
