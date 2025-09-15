package com.aliwis.bytebye.model;

import java.io.Serializable;

public class ProductModel implements Serializable {
    String name, description, image, category;
    String productID;

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    int price, quantity;

    public ProductModel() {
    }

    public ProductModel(String name, String description, String image, String category, String productID, int price, int quantity) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.category = category;
        this.productID = productID;
        this.price = price;
        this.quantity = quantity;
    }

    public ProductModel(String id, String name, int price, String image, String category) {
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public String getCategory() {
        return category;
    }

    public String getProductID() {
        return productID;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }
}
