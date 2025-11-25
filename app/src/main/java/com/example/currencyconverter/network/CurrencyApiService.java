package com.example.currencyconverter.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CurrencyApiService {
    private static final String BASE_URL = "https://api.exchangerate-api.com/v4/latest/";
    private static Retrofit retrofit = null;

    public static CurrencyApi getApi() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(CurrencyApi.class);
    }
}