package org.example.eshop.service;

import org.example.eshop.dto.*;
import org.example.eshop.entity.*;
import org.example.eshop.repository.OrderItemRepository;
import org.example.eshop.repository.OrderRepository;
import org.example.eshop.repository.ProductRepository;
import org.example.eshop.repository.VariantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminOrderService {

    private static final Logger logger = LoggerFactory.getLogger(AdminOrderService.class);

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final VariantRepository variantRepository;
    private final ProductRepository productRepository;

    public AdminOrderService(OrderRepository orderRepository,
                             OrderItemRepository orderItemRepository,
                             VariantRepository variantRepository,
                             ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.variantRepository = variantRepository;
        this.productRepository = productRepository;
    }

    public AdminOrderListResponse getAllOrders(int page, int size, String status, String paymentStatus, String fulfillmentStatus) {
        OrderStatus orderStatus = null;
        PaymentStatus paymentStatusEnum = null;
        FulfillmentStatus fulfillmentStatusEnum = null;

        if (status != null) {
            try { orderStatus = OrderStatus.valueOf(status.toUpperCase()); }
            catch (IllegalArgumentException e) { throw new IllegalArgumentException("Invalid order status: " + status); }
        }
        if (paymentStatus != null) {
            try { paymentStatusEnum = PaymentStatus.valueOf(paymentStatus.toUpperCase()); }
            catch (IllegalArgumentException e) { throw new IllegalArgumentException("Invalid payment status: " + paymentStatus); }
        }
        if (fulfillmentStatus != null) {
            try { fulfillmentStatusEnum = FulfillmentStatus.valueOf(fulfillmentStatus.toUpperCase()); }
            catch (IllegalArgumentException e) { throw new IllegalArgumentException("Invalid fulfillment status: " + fulfillmentStatus); }
        }

        Pageable pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Order> ordersPage = orderRepository.findAllWithFilters(orderStatus, paymentStatusEnum, fulfillmentStatusEnum, pageRequest);

        List<AdminOrderSummaryResponse> summaries = ordersPage.getContent().stream().map(order ->
                new AdminOrderSummaryResponse(
                        order.getId(),
                        order.getNumber(),
                        order.getEmail(),
                        order.getTotal(),
                        order.getStatus(),
                        order.getPaymentStatus(),
                        order.getFulfillmentStatus(),
                        order.getCreatedAt(),
                        order.getItems().size()
                )
        ).collect(Collectors.toList());

        return new AdminOrderListResponse(
                summaries,
                ordersPage.getTotalElements(),
                ordersPage.getTotalPages(),
                page,
                size
        );
    }

    public AdminOrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Order with id " + id + " not found"));
        return toAdminOrderResponse(order);
    }

    public AdminOrderResponse markOrderAsPaid(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Order with id " + id + " not found"));

        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            throw new IllegalStateException("Order " + order.getNumber() + " is already marked as paid");
        }
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot mark cancelled order " + order.getNumber() + " as paid");
        }

        order.setPaymentStatus(PaymentStatus.PAID);
        order.setStatus(OrderStatus.CONFIRMED);

        order.getItems().forEach(orderItem -> {
            Variant variant = variantRepository.findById(orderItem.getVariantId())
                    .orElseThrow(() -> new IllegalStateException("Variant " + orderItem.getVariantId() + " not found for order item " + orderItem.getId()));

            if (variant.getReservedQty() < orderItem.getQty()) {
                throw new IllegalStateException("Insufficient reserved stock for variant " + variant.getSku() + ". " +
                        "Required: " + orderItem.getQty() + ", Reserved: " + variant.getReservedQty());
            }

            int oldStock = variant.getStockQty();
            int oldReserved = variant.getReservedQty();
            variant.setStockQty(variant.getStockQty() - orderItem.getQty());
            variant.setReservedQty(variant.getReservedQty() - orderItem.getQty());
            variantRepository.save(variant);

            logger.info("Stock adjusted for variant {}: stockQty {} -> {}, reservedQty {} -> {}",
                    variant.getSku(), oldStock, variant.getStockQty(), oldReserved, variant.getReservedQty());
        });

        Order saved = orderRepository.save(order);

        logger.info("Payment Received - Order: {}, Items: {}, Total: {}, VAT: {}, Shipping: {}",
                order.getNumber(),
                order.getItems().stream().map(i -> i.getTitleSnapshot() + " (" + i.getQty() + ")").collect(Collectors.toList()),
                order.getTotal(), order.getTax(), order.getShipping());

        return toAdminOrderResponse(saved);
    }

    public AdminOrderResponse shipOrder(Long id, String trackingUrl) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Order with id " + id + " not found"));

        if (order.getPaymentStatus() != PaymentStatus.PAID) {
            throw new IllegalStateException("Cannot ship unpaid order " + order.getNumber());
        }
        if (order.getFulfillmentStatus() == FulfillmentStatus.FULFILLED) {
            throw new IllegalStateException("Order " + order.getNumber() + " is already shipped");
        }
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot ship cancelled order " + order.getNumber());
        }

        order.setFulfillmentStatus(FulfillmentStatus.FULFILLED);
        order.setTrackingUrl(trackingUrl);

        Order saved = orderRepository.save(order);
        String trackingInfo = trackingUrl != null ? "Tracking: " + trackingUrl : "No tracking provided";
        logger.info("Order Shipped - Order: {}, {}", order.getNumber(), trackingInfo);
        return toAdminOrderResponse(saved);
    }

    public AdminOrderResponse cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Order with id " + id + " not found"));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order " + order.getNumber() + " is already cancelled");
        }
        if (order.getFulfillmentStatus() == FulfillmentStatus.FULFILLED) {
            throw new IllegalStateException("Cannot cancel a fulfilled order " + order.getNumber());
        }

        // Revert stock reservations if the order was previously reserved but not paid
        order.getItems().forEach(orderItem -> {
            Variant variant = variantRepository.findById(orderItem.getVariantId())
                    .orElseThrow(() -> new IllegalStateException("Variant " + orderItem.getVariantId() + " not found for order item " + orderItem.getId()));

            int oldReserved = variant.getReservedQty();
            variant.setReservedQty(Math.max(0, variant.getReservedQty() - orderItem.getQty()));
            variantRepository.save(variant);

            logger.info("Reservation reverted for variant {}: reservedQty {} -> {}",
                    variant.getSku(), oldReserved, variant.getReservedQty());
        });

        order.setStatus(OrderStatus.CANCELLED);
        Order saved = orderRepository.save(order);
        logger.info("Order Cancelled - Order: {}", order.getNumber());
        return toAdminOrderResponse(saved);
    }

    private AdminOrderResponse toAdminOrderResponse(Order order) {
        List<AdminOrderItemResponse> itemResponses = order.getItems().stream().map(orderItem -> {
            Variant variant = variantRepository.findById(orderItem.getVariantId()).orElse(null);
            Product product = (variant != null) ? productRepository.findById(variant.getProductId()).orElse(null) : null;
            return new AdminOrderItemResponse(
                    orderItem.getId(),
                    orderItem.getVariantId(),
                    orderItem.getTitleSnapshot(),
                    orderItem.getQty(),
                    orderItem.getPriceSnapshot(),
                    orderItem.getLineTotal(),
                    variant != null ? variant.getSku() : null,
                    product != null ? product.getTitle() : null
            );
        }).collect(Collectors.toList());

        return new AdminOrderResponse(
                order.getId(),
                order.getNumber(),
                order.getEmail(),
                order.getAddress(),
                order.getSubtotal(),
                order.getTax(),
                order.getShipping(),
                order.getTotal(),
                order.getStatus(),
                order.getPaymentStatus(),
                order.getFulfillmentStatus(),
                order.getTrackingUrl(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                itemResponses
        );
    }
}
