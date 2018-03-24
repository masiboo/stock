package com.fasolutions.stock.callback;

import com.fasolutions.stock.model.StockElement;

import java.util.List;


public interface CallBack {
    List<StockElement> getDownloadedStock();

}
