package org.example.eshop.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public final class AdminOrderListResponse {
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
