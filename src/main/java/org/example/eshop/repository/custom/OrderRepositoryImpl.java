package org.example.eshop.repository.custom;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.example.eshop.entity.Order;
import org.springframework.stereotype.Repository;

@Repository
public class OrderRepositoryImpl implements OrderRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Order findByNumberWithItems(String number) {
        // Ensure pending changes are flushed so the query sees new OrderItem rows
        em.flush();
        // Clear the persistence context to avoid returning a cached Order with an uninitialized/empty collection
        em.clear();
        TypedQuery<Order> q = em.createQuery(
                "SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items WHERE o.number = :number",
                Order.class);
        q.setParameter("number", number);
        // get single result or null with items initialized via fetch-join
        return q.getResultStream().findFirst().orElse(null);
    }
}
