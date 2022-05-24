package com.example.calculator;

public class ChildItem {

    public String vegName, vegWeight, vegRate;
    public double vegAmount;
    public int entryId;

    public ChildItem(int entryId, String vegName, String vegWeight, String vegRate, Double vegAmount) {
        this.vegName = vegName;
        this.vegWeight = vegWeight;
        this.vegRate = vegRate;
        this.vegAmount = vegAmount;
        this.entryId = entryId;
    }
}
