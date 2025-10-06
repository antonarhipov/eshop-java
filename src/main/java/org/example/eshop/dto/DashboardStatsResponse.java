package org.example.eshop.dto;

public class DashboardStatsResponse {
    private long totalProducts;
    private long totalOrders;
    private long pendingOrders;
    private long lowStockItems;

    public DashboardStatsResponse() {
    }

    public DashboardStatsResponse(long totalProducts, long totalOrders, long pendingOrders, long lowStockItems) {
        this.totalProducts = totalProducts;
        this.totalOrders = totalOrders;
        this.pendingOrders = pendingOrders;
        this.lowStockItems = lowStockItems;
    }

    public long getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(long totalProducts) {
        this.totalProducts = totalProducts;
    }

    public long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public long getPendingOrders() {
        return pendingOrders;
    }

    public void setPendingOrders(long pendingOrders) {
        this.pendingOrders = pendingOrders;
    }

    public long getLowStockItems() {
        return lowStockItems;
    }

    public void setLowStockItems(long lowStockItems) {
        this.lowStockItems = lowStockItems;
    }
}
