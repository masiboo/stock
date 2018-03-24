package com.fasolutions.stock.model;

import javax.persistence.Entity;
import java.util.List;

@Entity
public class StockElement {
    private String date;
    private float open;
    private float high;
    private float low;
    private float close;
    private List<String> stockElements;

    public StockElement(List<String> stockElements) {
        this.stockElements = stockElements;
        fillProperties();
    }

    private void fillProperties()
    {
        if(stockElements.size() == 5)
        {
            setDate(stockElements.get(0).toString());
            setOpen(Float.parseFloat(stockElements.get(1).toString()));
            setHigh(Float.parseFloat(stockElements.get(2).toString()));
            setLow(Float.parseFloat(stockElements.get(3).toString()));
            setClose(Float.parseFloat(stockElements.get(4).toString()));
        }
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getOpen() {
        return open;
    }

    public void setOpen(float open) {
        this.open = open;
    }

    public float getHigh() {
        return high;
    }

    public void setHigh(float high) {
        this.high = high;
    }

    public float getLow() {
        return low;
    }

    public void setLow(float low) {
        this.low = low;
    }

    public float getClose() {
        return close;
    }

    public void setClose(float close) {
        this.close = close;
    }

    @Override
    public String toString() {
        return "StockElement{" +
                "Date='" + date + '\'' +
                ", Open=" + open +
                ", High=" + high +
                ", Low=" + low +
                ", Close=" + close +
                '}';
    }
}