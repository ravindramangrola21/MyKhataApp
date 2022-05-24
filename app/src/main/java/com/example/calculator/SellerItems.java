package com.example.calculator;

public class SellerItems {
    public int id;
    public String sellerName, itemName, itemWeight, itemRate;
    public Double totalAmount;

    public SellerItems(int id, String sellerNAme, String itemName, String itemWeight, String itemRate, Double totalAmount) {
        this.id = id;
        this.sellerName = sellerNAme;
        this.itemName = itemName;
        this.itemWeight = itemWeight;
        this.itemRate = itemRate;
        this.totalAmount = totalAmount;
    }
}
