package org.example.eshop.service;

import org.example.eshop.entity.Order;
import org.example.eshop.repository.OrderItemRepository;
import org.example.eshop.repository.VariantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final OrderItemRepository orderItemRepository;
    private final VariantRepository variantRepository;

    public NotificationService(OrderItemRepository orderItemRepository, VariantRepository variantRepository) {
        this.orderItemRepository = orderItemRepository;
        this.variantRepository = variantRepository;
    }

    public void logOrderReceived(Order order) {
        try {
            var orderItems = orderItemRepository.findByOrderId(order.getId());

            StringBuilder items = new StringBuilder();
            orderItems.forEach(orderItem -> items.append("- ")
                    .append(orderItem.getTitleSnapshot())
                    .append(" (Qty: ")
                    .append(orderItem.getQty())
                    .append(", Price: ")
                    .append(orderItem.getPriceSnapshot())
                    .append(")\n"));

            String logMessage = "" +
                    "ORDER RECEIVED - AWAITING PAYMENT\n" +
                    "Order Number: " + order.getNumber() + "\n" +
                    "Order Date: " + order.getCreatedAt() + "\n\n" +
                    "Items:\n" + items + "\n" +
                    "Financial Summary:\n" +
                    "Subtotal: " + order.getSubtotal() + "\n" +
                    "VAT Amount: " + order.getTax() + "\n" +
                    "Shipping Cost: " + order.getShipping() + "\n" +
                    "Total: " + order.getTotal() + "\n\n" +
                    "Payment Status: " + order.getPaymentStatus() + "\n" +
                    "Order Status: " + order.getStatus();

            logger.info(logMessage);
        } catch (Exception e) {
            logger.error("Failed to log order received event for order " + order.getNumber(), e);
        }
    }

    public void logPaymentReceived(Order order) {
        try {
            String logMessage = "" +
                    "PAYMENT RECEIVED\n" +
                    "Order Number: " + order.getNumber() + "\n" +
                    "Customer Email: " + order.getEmail() + "\n" +
                    "Payment Date: " + order.getUpdatedAt() + "\n" +
                    "Total Paid: " + order.getTotal() + "\n" +
                    "Payment Status: " + order.getPaymentStatus();
            logger.info(logMessage);
        } catch (Exception e) {
            logger.error("Failed to log payment received event for order " + order.getNumber(), e);
        }
    }

    public void logOrderShipped(Order order) {
        try {
            String tracking = order.getTrackingUrl() != null ? order.getTrackingUrl() : "Not provided";
            String logMessage = "" +
                    "ORDER SHIPPED\n" +
                    "Order Number: " + order.getNumber() + "\n" +
                    "Customer Email: " + order.getEmail() + "\n" +
                    "Ship Date: " + order.getUpdatedAt() + "\n" +
                    "Tracking URL: " + tracking + "\n" +
                    "Fulfillment Status: " + order.getFulfillmentStatus();
            logger.info(logMessage);
        } catch (Exception e) {
            logger.error("Failed to log order shipped event for order " + order.getNumber(), e);
        }
    }
}
