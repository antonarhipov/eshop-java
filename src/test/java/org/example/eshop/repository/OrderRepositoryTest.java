package org.example.eshop.repository;

import org.example.eshop.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OrderRepositoryTest {

    @Autowired private OrderRepository orderRepository;
    @Autowired private OrderItemRepository orderItemRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private VariantRepository variantRepository;

    @Test
    void findByNumberWithItems_shouldFetchItems() {
        // Seed product and variant to satisfy FK
        Product p = productRepository.save(new Product("puer", "Pu-erh", "tea", "", ProductStatus.ACTIVE));
        Variant v = variantRepository.save(new Variant(p.getId(), "SKU-1", "Cake 357g",
                new BigDecimal("20.00"), new BigDecimal("0.357"), new BigDecimal("0.400")));

        // Create order + item
        Order o = orderRepository.save(new Order("ORD-1", "user@example.com", "addr",
                new BigDecimal("20.00"), new BigDecimal("0.00"), new BigDecimal("0.00"), new BigDecimal("20.00")));
        orderRepository.flush();

        Order persisted = orderRepository.findByNumber("ORD-1");
        assertThat(persisted).isNotNull();

        OrderItem oi = new OrderItem(persisted.getId(), v.getId(), v.getTitle(), 1, v.getPrice());
        orderItemRepository.save(oi);

        Order fetched = orderRepository.findByNumberWithItems("ORD-1");
        assertThat(fetched).isNotNull();
        assertThat(fetched.getItems()).hasSize(1);
        assertThat(fetched.getItems().get(0).getVariantId()).isEqualTo(v.getId());
    }

    @Test
    void findPaidUnfulfilledOrders_shouldReturnOrdersMatchingStatuses() {
        // Order not matching
        Order o1 = new Order("ORD-A", "a@example.com", "addr",
                new BigDecimal("5.00"), new BigDecimal("0.00"), new BigDecimal("0.00"), new BigDecimal("5.00"));
        orderRepository.save(o1);

        // Matching order
        Order o2 = new Order("ORD-B", "b@example.com", "addr",
                new BigDecimal("10.00"), new BigDecimal("0.00"), new BigDecimal("0.00"), new BigDecimal("10.00"));
        o2.setPaymentStatus(PaymentStatus.PAID);
        o2.setFulfillmentStatus(FulfillmentStatus.UNFULFILLED);
        orderRepository.save(o2);

        List<Order> results = orderRepository.findPaidUnfulfilledOrders();
        assertThat(results).extracting(Order::getNumber).containsExactly("ORD-B");
    }

    @Test
    void findByEmailAndStatus_shouldFilterCorrectly() {
        Order o1 = new Order("ORD-100", "c@example.com", "addr",
                new BigDecimal("7.00"), new BigDecimal("0.00"), new BigDecimal("0.00"), new BigDecimal("7.00"));
        o1.setStatus(OrderStatus.PENDING);
        orderRepository.save(o1);

        Order o2 = new Order("ORD-101", "c@example.com", "addr",
                new BigDecimal("8.00"), new BigDecimal("0.00"), new BigDecimal("0.00"), new BigDecimal("8.00"));
        o2.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(o2);

        List<Order> results = orderRepository.findByEmailAndStatus("c@example.com", OrderStatus.CANCELLED);
        assertThat(results).extracting(Order::getNumber).containsExactly("ORD-101");
    }
}
