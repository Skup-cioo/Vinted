package pl.skup.vinted.models.dto;

import lombok.Data;
import pl.skup.vinted.models.vintedresponsemodel.Order;

import java.util.List;

public class MonthDetailsDTO {
    private int orderCount;
    private double averagePrice;
    private double totalCash;
    private List<Order> orders;

    public MonthDetailsDTO(int orderCount, List<Order> orders, double totalCash, double averagePrice) {
        this.orderCount = orderCount;
        this.orders = orders;
        this.totalCash = totalCash;
        this.averagePrice = averagePrice;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public double getTotalCash() {
        return totalCash;
    }

    public double getAveragePrice() {
        return averagePrice;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public void setTotalCash(double totalCash) {
        this.totalCash = totalCash;
    }

    public void setAveragePrice(double averagePrice) {
        this.averagePrice = averagePrice;
    }
}
