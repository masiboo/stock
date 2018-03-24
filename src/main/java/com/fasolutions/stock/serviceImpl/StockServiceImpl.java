package com.fasolutions.stock.serviceImpl;

import com.fasolutions.stock.callback.CallBack;
import com.fasolutions.stock.model.StockElement;
import com.fasolutions.stock.service.StockService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

@Service
public class StockServiceImpl implements StockService, CallBack {
    private static final String strUrl = "https://www.quandl.com/api/v3/datasets/EOD/AAPL.json?api_key=s2zrK5rk1-_bu5cP6Q2d";
    private static Future<List<StockElement>> stockDownloaderFuture = null;
    List<StockElement> stockElementList = null;
    private ExecutorService executorService = Executors.newFixedThreadPool(2);

    public void downloadStockContent() {
        stockDownloaderFuture = downloadStock();
    }

    public Future<List<StockElement>> downloadStock() {
        return executorService.submit(() -> {
            URL url = new URL(strUrl);
            URLConnection urlConnection = url.openConnection();
            InputStreamReader inputStreamReader = new InputStreamReader(urlConnection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String stockContent = bufferedReader.readLine();
            stockElementList = parseStockContents(stockContent);
            executorService.shutdownNow();
            return stockElementList;
        });
    }

    public List<StockElement> parseStockContents(String stockContent) {
        JsonArray data_array = new JsonParser()
                .parse(stockContent)
                .getAsJsonObject()
                .getAsJsonObject("dataset")
                .getAsJsonArray("data");

        List<StockElement> stockElementList = new ArrayList<>();
        for (JsonElement element : data_array) {
            try {
                String[] commaSplit = element.toString().split(",");
                List<String> elementList = new ArrayList<>(Arrays.asList(commaSplit));
                for (int i = elementList.size() - 1; i >= 0; i--) {
                    // we only need fist 5 element. Remove the rest.
                    if (i > 4) {
                        elementList.remove(i);
                    }
                    // first element is the date
                    if (i == 0) {
                        // First element is date as ["2018-03-23". We have to remove extra characters
                        String replaced = elementList.get(i).replace("[\"", " ");
                        String[] splitted = replaced.split("\"");
                        replaced = splitted[0];
                        replaced = replaced.trim();
                        elementList.set(i, replaced);
                    }
                }
                StockElement stockElement = new StockElement(elementList);
                stockElementList.add(stockElement);

            } catch (Exception ex) {
                System.out.println("Exception: " + ex.getMessage() + " At: " + Thread.currentThread().getStackTrace()[1]);
            }
        }
        return stockElementList;
    }


    @Override
    public List<StockElement> getDownloadedStock() {
        if (stockDownloaderFuture.isDone()) {
            try {
                stockElementList = stockDownloaderFuture.get();
                return stockElementList;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
