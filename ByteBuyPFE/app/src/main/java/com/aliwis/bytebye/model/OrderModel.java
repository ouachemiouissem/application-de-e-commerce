package com.aliwis.bytebye.model;

import java.util.ArrayList;

public class OrderModel {
    ArrayList<ProductModel> productModels;
    String fullName, address, phoneNumber, wilaya, commune, userId;
    int totalPrice;
    int state; // 0: waiting , 1: Accepted , 2:Refused , 3:Completed
    String orderId;

    public OrderModel(ArrayList<ProductModel> productModels, String fullName, String address, String phoneNumber, String wilaya, String commune, String userId, int totalPrice, int state, String orderId) {
        this.productModels = productModels;
        this.fullName = fullName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.wilaya = wilaya;
        this.commune = commune;
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.state = state;
        this.orderId = orderId;
    }

    public OrderModel() {
    }

    public ArrayList<ProductModel> getProductModels() {
        return productModels;
    }

    public String getFullName() {
        return fullName;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getWilaya() {
        return wilaya;
    }

    public String getCommune() {
        return commune;
    }

    public String getUserId() {
        return userId;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public int getState() {
        return state;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setState(int state) {
        this.state = state;
    }
}