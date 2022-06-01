package com.example.calculator;

public class SellerItems {
    public int id, nangCount,sellerID, dateID;
    public String itemName, itemWeight, itemRate;
    public Double mTax, wChungi, totalAmount;

    public SellerItems(int id, int sellerID, int  dateID, String itemName, String itemWeight, String itemRate, int nangCount, Double wChungi, Double mTax, Double totalAmount) {
        this.id = id;
        this.sellerID = sellerID;
        this.dateID = dateID;
        this.itemName = itemName;
        this.itemWeight = itemWeight;
        this.itemRate = itemRate;
        this.nangCount = nangCount;
        this.wChungi = wChungi;
        this.mTax = mTax;
        this.totalAmount = totalAmount;
    }
}
