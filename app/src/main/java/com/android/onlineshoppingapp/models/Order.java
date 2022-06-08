package com.android.onlineshoppingapp.models;

import java.util.List;

public class Order {
    private String orderId;
    private String orderer;
    private int orderStatus;
    private int totalPrice;
    private List<OrderProduct> listOrderProduct;
    private UserAddress address;

    public Order(String orderId, String orderer, int orderStatus, int totalPrice, List<OrderProduct> listOrderProduct, UserAddress address) {
        this.orderId = orderId;
        this.address = address;
        this.orderer = orderer;
        this.orderStatus = orderStatus;
        this.totalPrice = totalPrice;
        this.listOrderProduct = listOrderProduct;
    }

    public Order(){};

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderer() {
        return orderer;
    }

    public void setOrderer(String orderer) {
        this.orderer = orderer;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }



    public List<OrderProduct> getListOrderProduct() {
        return listOrderProduct;
    }

    public void setListOrderProduct(List<OrderProduct> listOrderProduct) {
        this.listOrderProduct = listOrderProduct;
    }

    public UserAddress getAddress() {
        return address;
    }

    public void setAddress(UserAddress address) {
        this.address = address;
    }
}