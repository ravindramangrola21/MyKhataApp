package com.example.calculator;

public class SetListValue {
    String name;
    String weight;
    String rate;
    String total;

    public SetListValue(String name, String weight, String rate, String total) {
        this.name = name;
        this.weight = weight;
        this.rate = rate;
        this.total = total;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() { return name; }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
