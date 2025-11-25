package com.example.currencyconverter.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "conversion_history")
public class ConversionHistory {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String fromCurrency;
    private String toCurrency;
    private double amount;
    private double convertedAmount;
    private double exchangeRate;
    private long timestamp;

    public ConversionHistory(String fromCurrency, String toCurrency,
                             double amount, double convertedAmount,
                             double exchangeRate) {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.amount = amount;
        this.convertedAmount = convertedAmount;
        this.exchangeRate = exchangeRate;
        this.timestamp = new Date().getTime();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFromCurrency() { return fromCurrency; }
    public void setFromCurrency(String fromCurrency) { this.fromCurrency = fromCurrency; }

    public String getToCurrency() { return toCurrency; }
    public void setToCurrency(String toCurrency) { this.toCurrency = toCurrency; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public double getConvertedAmount() { return convertedAmount; }
    public void setConvertedAmount(double convertedAmount) { this.convertedAmount = convertedAmount; }

    public double getExchangeRate() { return exchangeRate; }
    public void setExchangeRate(double exchangeRate) { this.exchangeRate = exchangeRate; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}