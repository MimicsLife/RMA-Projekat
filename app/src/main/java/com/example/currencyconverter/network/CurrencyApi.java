package com.example.currencyconverter.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import com.example.currencyconverter.models.CurrencyResponse;

public interface CurrencyApi {
    @GET("{baseCurrency}")
    Call<CurrencyResponse> getExchangeRates(@Path("baseCurrency") String baseCurrency);
}