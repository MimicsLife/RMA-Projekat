package com.example.currencyconverter.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.example.currencyconverter.models.CurrencyResponse;
import com.example.currencyconverter.models.ConversionHistory;
import com.example.currencyconverter.database.AppDatabase;
import com.example.currencyconverter.database.ConversionHistoryDao;
import com.example.currencyconverter.network.CurrencyApi;
import com.example.currencyconverter.network.CurrencyApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CurrencyViewModel extends AndroidViewModel {
    private final ConversionHistoryDao historyDao;
    private final Executor executor = Executors.newSingleThreadExecutor();

    private MutableLiveData<CurrencyResponse> exchangeRates = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<Double> conversionResult = new MutableLiveData<>();

    public CurrencyViewModel(Application application) {
        super(application);
        AppDatabase database = AppDatabase.getDatabase(application);
        historyDao = database.conversionHistoryDao();
    }

    public LiveData<List<ConversionHistory>> getRecentConversions() {
        return historyDao.getRecentConversions();
    }

    public void fetchExchangeRates(String baseCurrency) {
        isLoading.setValue(true);

        CurrencyApi api = CurrencyApiService.getApi();
        Call<CurrencyResponse> call = api.getExchangeRates(baseCurrency);

        call.enqueue(new Callback<CurrencyResponse>() {
            @Override
            public void onResponse(Call<CurrencyResponse> call, Response<CurrencyResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    exchangeRates.setValue(response.body());
                } else {
                    errorMessage.setValue("Failed to fetch exchange rates");
                }
            }

            @Override
            public void onFailure(Call<CurrencyResponse> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void convertCurrency(String fromCurrency, String toCurrency, double amount) {
        CurrencyResponse rates = exchangeRates.getValue();
        if (rates != null && rates.getRates() != null) {
            Double rate = rates.getRates().get(toCurrency);
            if (rate != null) {
                double result = amount * rate;
                conversionResult.setValue(result);

                // Save to history
                ConversionHistory history = new ConversionHistory(
                        fromCurrency, toCurrency, amount, result, rate
                );
                saveConversionToHistory(history);
            } else {
                errorMessage.setValue("Exchange rate not available");
            }
        } else {
            errorMessage.setValue("Exchange rates not loaded");
        }
    }

    private void saveConversionToHistory(ConversionHistory history) {
        executor.execute(() -> {
            historyDao.insert(history);
        });
    }

    // Metoda za brisanje LiveData
    private MutableLiveData<List<ConversionHistory>> recentConversions = new MutableLiveData<>();

    public void clearRecentConversionsData() {
        // Clear the LiveData immediately
        recentConversions.setValue(new ArrayList<>());
        Log.d("ViewModel", "LiveData cleared");
    }

    // Metoda za brisanje iz baze
    public void clearAllHistoryFromDatabase() {
        executor.execute(() -> {
            try {
                // Obriši sve iz Room baze
                historyDao.deleteAll();

                // Očisti i LiveData
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    clearRecentConversionsData();
                });

                Log.d("ViewModel", "Room database table cleared");

            } catch (Exception e) {
                Log.e("ViewModel", "Error clearing Room database: " + e.getMessage());
            }
        });
    }

    // Metoda za brisanje samo history zapisa (ako ne želite sve obrisati)
    private void clearOnlyHistoryFromSharedPreferences(SharedPreferences sharedPreferences) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Map<String, ?> allEntries = sharedPreferences.getAll();

        for (String key : allEntries.keySet()) {
            if (key.startsWith("history_")) {
                editor.remove(key);
                Log.d("ViewModel", "Removed history entry: " + key);
            }
        }
        editor.apply();
    }

    // LiveData Getters
    public LiveData<CurrencyResponse> getExchangeRates() { return exchangeRates; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<Double> getConversionResult() { return conversionResult; }
}