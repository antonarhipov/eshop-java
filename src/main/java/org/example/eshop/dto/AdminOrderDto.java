package org.example.eshop.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import org.example.eshop.entity.FulfillmentStatus;
import org.example.eshop.entity.OrderStatus;
import org.example.eshop.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public final class AdminOrderDto {
    private AdminOrderDto() {}

    // Request DTOs
    public static final class MarkOrderPaidRequest {
        private final String notes; // nullable

        @JsonCreator
        public MarkOrderPaidRequest(@JsonProperty("notes") String notes) {
            this.notes = notes;
        }

        public String getNotes() { return notes; }
    }

    public static final class ShipOrderRequest {
        private final String trackingUrl; // nullable
        private final String notes; // nullable

        @JsonCreator
        public ShipOrderRequest(
                @JsonProperty("trackingUrl") @Size(max = 500, message = "Tracking URL must not exceed 500 characters") String trackingUrl,
                @JsonProperty("notes") String notes) {
            this.trackingUrl = trackingUrl;
            this.notes = notes;
        }

        public String getTrackingUrl() { return trackingUrl; }
        public String getNotes() { return notes; }
    }

    public static final class CancelOrderRequest {
        private final String reason; // nullable
        private final String notes;  // nullable

        @JsonCreator
        public CancelOrderRequest(@JsonProperty("reason") String reason,
                                  @JsonProperty("notes") String notes) {
            this.reason = reason;
            this.notes = notes;
        }

        public String getReason() { return reason; }
        public String getNotes() { return notes; }
    }

    // Response DTOs
    public static final class AdminOrderResponse {
        private final long id;
        private final String number;
        private final String email;
        private final String address;
        private final BigDecimal subtotal;
        private final BigDecimal tax;
        private final BigDecimal shipping;
        private final BigDecimal total;
        private final OrderStatus status;
        private final PaymentStatus paymentStatus;
        private final FulfillmentStatus fulfillmentStatus;
        private final String trackingUrl; // nullable
        private final LocalDateTime createdAt;
        private final LocalDateTime updatedAt;
        private final List<AdminOrderItemResponse> items;

        @JsonCreator
        public AdminOrderResponse(
                @JsonProperty("id") long id,
                @JsonProperty("number") String number,
                @JsonProperty("email") String email,
                @JsonProperty("address") String address,
                @JsonProperty("subtotal") BigDecimal subtotal,
                @JsonProperty("tax") BigDecimal tax,
                @JsonProperty("shipping") BigDecimal shipping,
                @JsonProperty("total") BigDecimal total,
                @JsonProperty("status") OrderStatus status,
                @JsonProperty("paymentStatus") PaymentStatus paymentStatus,
                @JsonProperty("fulfillmentStatus") FulfillmentStatus fulfillmentStatus,
                @JsonProperty("trackingUrl") String trackingUrl,
                @JsonProperty("createdAt") LocalDateTime createdAt,
                @JsonProperty("updatedAt") LocalDateTime updatedAt,
                @JsonProperty("items") List<AdminOrderItemResponse> items) {
            this.id = id;
            this.number = number;
            this.email = email;
            this.address = address;
            this.subtotal = subtotal;
            this.tax = tax;
            this.shipping = shipping;
            this.total = total;
            this.status = status;
            this.paymentStatus = paymentStatus;
            this.fulfillmentStatus = fulfillmentStatus;
            this.trackingUrl = trackingUrl;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
            this.items = items;
        }

        public long getId() { return id; }
        public String getNumber() { return number; }
        public String getEmail() { return email; }
        public String getAddress() { return address; }
        public BigDecimal getSubtotal() { return subtotal; }
        public BigDecimal getTax() { return tax; }
        public BigDecimal getShipping() { return shipping; }
        public BigDecimal getTotal() { return total; }
        public OrderStatus getStatus() { return status; }
        public PaymentStatus getPaymentStatus() { return paymentStatus; }
        public FulfillmentStatus getFulfillmentStatus() { return fulfillmentStatus; }
        public String getTrackingUrl() { return trackingUrl; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public List<AdminOrderItemResponse> getItems() { return items; }
    }

    public static final class AdminOrderItemResponse {
        private final long id;
        private final long variantId;
        private final String titleSnapshot;
        private final int qty;
        private final BigDecimal priceSnapshot;
        private final BigDecimal lineTotal;
        private final String sku; // nullable
        private final String productTitle; // nullable

        @JsonCreator
        public AdminOrderItemResponse(
                @JsonProperty("id") long id,
                @JsonProperty("variantId") long variantId,
                @JsonProperty("titleSnapshot") String titleSnapshot,
                @JsonProperty("qty") int qty,
                @JsonProperty("priceSnapshot") BigDecimal priceSnapshot,
                @JsonProperty("lineTotal") BigDecimal lineTotal,
                @JsonProperty("sku") String sku,
                @JsonProperty("productTitle") String productTitle) {
            this.id = id;
            this.variantId = variantId;
            this.titleSnapshot = titleSnapshot;
            this.qty = qty;
            this.priceSnapshot = priceSnapshot;
            this.lineTotal = lineTotal;
            this.sku = sku;
            this.productTitle = productTitle;
        }

        public long getId() { return id; }
        public long getVariantId() { return variantId; }
        public String getTitleSnapshot() { return titleSnapshot; }
        public int getQty() { return qty; }
        public BigDecimal getPriceSnapshot() { return priceSnapshot; }
        public BigDecimal getLineTotal() { return lineTotal; }
        public String getSku() { return sku; }
        public String getProductTitle() { return productTitle; }
    }

    public static final class AdminOrderListResponse {
        private final List<AdminOrderSummaryResponse> orders;
        private final long totalElements;
        private final int totalPages;
        private final int currentPage;
        private final int pageSize;

        @JsonCreator
        public AdminOrderListResponse(
                @JsonProperty("orders") List<AdminOrderSummaryResponse> orders,
                @JsonProperty("totalElements") long totalElements,
                @JsonProperty("totalPages") int totalPages,
                @JsonProperty("currentPage") int currentPage,
                @JsonProperty("pageSize") int pageSize) {
            this.orders = orders;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
            this.currentPage = currentPage;
            this.pageSize = pageSize;
        }

        public List<AdminOrderSummaryResponse> getOrders() { return orders; }
        public long getTotalElements() { return totalElements; }
        public int getTotalPages() { return totalPages; }
        public int getCurrentPage() { return currentPage; }
        public int getPageSize() { return pageSize; }
    }

    public static final class AdminOrderSummaryResponse {
        private final long id;
        private final String number;
        private final String email;
        private final BigDecimal total;
        private final OrderStatus status;
        private final PaymentStatus paymentStatus;
        private final FulfillmentStatus fulfillmentStatus;
        private final LocalDateTime createdAt;
        private final int itemCount;

        @JsonCreator
        public AdminOrderSummaryResponse(
                @JsonProperty("id") long id,
                @JsonProperty("number") String number,
                @JsonProperty("email") String email,
                @JsonProperty("total") BigDecimal total,
                @JsonProperty("status") OrderStatus status,
                @JsonProperty("paymentStatus") PaymentStatus paymentStatus,
                @JsonProperty("fulfillmentStatus") FulfillmentStatus fulfillmentStatus,
                @JsonProperty("createdAt") LocalDateTime createdAt,
                @JsonProperty("itemCount") int itemCount) {
            this.id = id;
            this.number = number;
            this.email = email;
            this.total = total;
            this.status = status;
            this.paymentStatus = paymentStatus;
            this.fulfillmentStatus = fulfillmentStatus;
            this.createdAt = createdAt;
            this.itemCount = itemCount;
        }

        public long getId() { return id; }
        public String getNumber() { return number; }
        public String getEmail() { return email; }
        public BigDecimal getTotal() { return total; }
        public OrderStatus getStatus() { return status; }
        public PaymentStatus getPaymentStatus() { return paymentStatus; }
        public FulfillmentStatus getFulfillmentStatus() { return fulfillmentStatus; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public int getItemCount() { return itemCount; }
    }
}
