package org.example.eshop.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.example.eshop.entity.FulfillmentStatus;
import org.example.eshop.entity.Order;
import org.example.eshop.entity.OrderStatus;
import org.example.eshop.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public final class CheckoutDto {
    private CheckoutDto() {}

    public static final class CheckoutRequest {
        private final String fullName;
        private final String email;
        private final String phone; // nullable
        private final String street1;
        private final String street2; // nullable
        private final String city;
        private final String region;
        private final String postalCode;
        private final String country;

        @JsonCreator
        public CheckoutRequest(
                @JsonProperty("fullName") @NotBlank(message = "Full name is required") @Size(max = 255, message = "Full name must not exceed 255 characters") String fullName,
                @JsonProperty("email") @NotBlank(message = "Email is required") @Email(message = "Email must be valid") @Size(max = 255, message = "Email must not exceed 255 characters") String email,
                @JsonProperty("phone") @Size(max = 50, message = "Phone must not exceed 50 characters") String phone,
                @JsonProperty("street1") @NotBlank(message = "Street address is required") @Size(max = 255, message = "Street address must not exceed 255 characters") String street1,
                @JsonProperty("street2") @Size(max = 255, message = "Street address line 2 must not exceed 255 characters") String street2,
                @JsonProperty("city") @NotBlank(message = "City is required") @Size(max = 255, message = "City must not exceed 255 characters") String city,
                @JsonProperty("region") @NotBlank(message = "Region/State is required") @Size(max = 255, message = "Region/State must not exceed 255 characters") String region,
                @JsonProperty("postalCode") @NotBlank(message = "Postal code is required") @Size(max = 32, message = "Postal code must not exceed 32 characters") String postalCode,
                @JsonProperty("country") @NotBlank(message = "Country is required") @Size(max = 255, message = "Country must not exceed 255 characters") String country
        ) {
            this.fullName = fullName;
            this.email = email;
            this.phone = phone;
            this.street1 = street1;
            this.street2 = street2;
            this.city = city;
            this.region = region;
            this.postalCode = postalCode;
            this.country = country;
        }

        public String getFullName() { return fullName; }
        public String getEmail() { return email; }
        public String getPhone() { return phone; }
        public String getStreet1() { return street1; }
        public String getStreet2() { return street2; }
        public String getCity() { return city; }
        public String getRegion() { return region; }
        public String getPostalCode() { return postalCode; }
        public String getCountry() { return country; }
    }

    public static final class OrderDto {
        private final long id;
        private final String number;
        private final String email;
        private final String fullName;
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
        private final List<OrderItemDto> items;

        @JsonCreator
        public OrderDto(
                @JsonProperty("id") long id,
                @JsonProperty("number") String number,
                @JsonProperty("email") String email,
                @JsonProperty("fullName") String fullName,
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
                @JsonProperty("items") List<OrderItemDto> items) {
            this.id = id;
            this.number = number;
            this.email = email;
            this.fullName = fullName;
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
            this.items = items == null ? Collections.emptyList() : List.copyOf(items);
        }

        public static OrderDto fromEntity(Order order) {
            return new OrderDto(
                    order.getId(),
                    order.getNumber(),
                    order.getEmail(),
                    order.getFullName(),
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
                    order.getItems().stream().map(OrderItemDto::fromEntity).toList()
            );
        }

        public long getId() { return id; }
        public String getNumber() { return number; }
        public String getEmail() { return email; }
        public String getFullName() { return fullName; }
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
        public List<OrderItemDto> getItems() { return items; }
    }

    public static final class OrderItemDto {
        private final long id;
        private final long variantId;
        private final String titleSnapshot;
        private final int qty;
        private final BigDecimal priceSnapshot;
        private final BigDecimal lineTotal;

        @JsonCreator
        public OrderItemDto(
                @JsonProperty("id") long id,
                @JsonProperty("variantId") long variantId,
                @JsonProperty("titleSnapshot") String titleSnapshot,
                @JsonProperty("qty") int qty,
                @JsonProperty("priceSnapshot") BigDecimal priceSnapshot,
                @JsonProperty("lineTotal") BigDecimal lineTotal) {
            this.id = id;
            this.variantId = variantId;
            this.titleSnapshot = titleSnapshot;
            this.qty = qty;
            this.priceSnapshot = priceSnapshot;
            this.lineTotal = lineTotal;
        }

        public static OrderItemDto fromEntity(org.example.eshop.entity.OrderItem orderItem) {
            return new OrderItemDto(
                    orderItem.getId(),
                    orderItem.getVariantId(),
                    orderItem.getTitleSnapshot(),
                    orderItem.getQty(),
                    orderItem.getPriceSnapshot(),
                    orderItem.getLineTotal()
            );
        }

        public long getId() { return id; }
        public long getVariantId() { return variantId; }
        public String getTitleSnapshot() { return titleSnapshot; }
        public int getQty() { return qty; }
        public BigDecimal getPriceSnapshot() { return priceSnapshot; }
        public BigDecimal getLineTotal() { return lineTotal; }
    }

    public static final class CheckoutResponse {
        private final boolean success;
        private final String message; // nullable
        private final OrderDto order; // nullable
        private final List<String> errors; // default empty

        @JsonCreator
        public CheckoutResponse(
                @JsonProperty("success") boolean success,
                @JsonProperty("message") String message,
                @JsonProperty("order") OrderDto order,
                @JsonProperty("errors") List<String> errors) {
            this.success = success;
            this.message = message;
            this.order = order;
            this.errors = errors == null ? Collections.emptyList() : List.copyOf(errors);
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public OrderDto getOrder() { return order; }
        public List<String> getErrors() { return errors; }
    }
}
