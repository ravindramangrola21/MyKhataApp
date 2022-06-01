package com.example.calculator;

public class ChildItem {

    public String vegName, vegWeight, vegRate;
    public double vegAmount;
    public int entryId,dateId,cId;

    public ChildItem(int entryId, String vegName, String vegWeight, String vegRate, Double vegAmount, int dateId, int cId) {
        this.vegName = vegName;
        this.vegWeight = vegWeight;
        this.vegRate = vegRate;
        this.vegAmount = vegAmount;
        this.entryId = entryId;
        this.dateId = dateId;
        this.cId = cId;
    }
}
