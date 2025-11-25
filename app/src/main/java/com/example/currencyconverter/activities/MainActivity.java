package com.example.currencyconverter.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import java.util.*;

import com.example.currencyconverter.R;
import com.example.currencyconverter.adapters.HistoryAdapter;
import com.example.currencyconverter.database.ConversionHistoryDao;
import com.example.currencyconverter.models.ConversionHistory;
import com.example.currencyconverter.sensors.ShakeDetector;
import com.example.currencyconverter.viewmodels.CurrencyViewModel;

public class MainActivity extends AppCompatActivity {

    private CurrencyViewModel viewModel;
    private HistoryAdapter historyAdapter;
    private ShakeDetector shakeDetector;
    private SensorManager sensorManager;
    private Sensor accelerometer;

    private EditText etAmount;
    private Spinner spinnerFrom, spinnerTo;
    private Button btnConvert;
    private TextView tvResult, tvError;
    private ProgressBar progressBar;
    private RecyclerView rvHistory;

    private final String[] currencies = {"USD", "EUR", "GBP", "JPY", "CAD", "AUD", "CHF", "CNY", "RSD"};
    private String currentBaseCurrency = "EUR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupViewModel();
        setupSpinners();
        setupRecyclerView();
        setupShakeDetector();
        setupListeners();

        // Load initial data
        viewModel.fetchExchangeRates(currentBaseCurrency);
    }

    private void initViews() {
        etAmount = findViewById(R.id.etAmount);
        spinnerFrom = findViewById(R.id.spinnerFrom);
        spinnerTo = findViewById(R.id.spinnerTo);
        btnConvert = findViewById(R.id.btnConvert);
        tvResult = findViewById(R.id.tvResult);
        tvError = findViewById(R.id.tvError);
        progressBar = findViewById(R.id.progressBar);
        rvHistory = findViewById(R.id.rvHistory);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(CurrencyViewModel.class);

        viewModel.getExchangeRates().observe(this, currencyResponse -> {
            if (currencyResponse != null) {
                tvError.setVisibility(View.GONE);
            }
        });

        viewModel.getConversionResult().observe(this, result -> {
            if (result != null) {
                tvResult.setText(String.format("Result: %.2f", result));
            }
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                tvError.setText(error);
                tvError.setVisibility(View.VISIBLE);
            } else {
                tvError.setVisibility(View.GONE);
            }
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading != null && isLoading ? View.VISIBLE : View.GONE);
            btnConvert.setEnabled(!isLoading);
        });

        viewModel.getRecentConversions().observe(this, history -> {
            if (history != null) {
                historyAdapter.updateData(history);
            }
        });
    }

    private void setupSpinners() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, currencies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);

        // Set default selections
        spinnerFrom.setSelection(1); // EUR
        spinnerTo.setSelection(8);   // RSD
    }

    private void setupRecyclerView() {
        historyAdapter = new HistoryAdapter(new ArrayList<>());
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        rvHistory.setAdapter(historyAdapter);
    }

    private void setupShakeDetector() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        shakeDetector = new ShakeDetector();

        shakeDetector.setOnShakeListener(() -> {
            // Refresh exchange rates on shake
            viewModel.fetchExchangeRates(currentBaseCurrency);

            // Clear recent conversions list
            clearRecentConversionsList();

            // Clear database table
            clearDatabaseTable();

            // Reset amount input field
            resetAmountField();

            Toast.makeText(this, "🔄 Rates refreshed & history cleared!", Toast.LENGTH_SHORT).show();
        });
    }

    private void clearRecentConversionsList() {
        try {
            // Clear the adapter data
            if (historyAdapter != null) {
                historyAdapter.updateData(new ArrayList<>());
                Log.d("Shake", "Recent conversions list cleared from adapter");
            }

            // Clear the LiveData in ViewModel
            if (viewModel != null) {
                viewModel.clearRecentConversionsData();
            }

            // Force RecyclerView refresh
            if (rvHistory != null && historyAdapter != null) {
                historyAdapter.notifyDataSetChanged();
            }

        } catch (Exception e) {
            Log.e("Shake", "Error clearing recent conversions list: " + e.getMessage());
        }
    }

    private void clearDatabaseTable() {
        try {
            if (viewModel != null) {
                viewModel.clearAllHistoryFromDatabase();
                Log.d("Shake", "Database table clear requested");
            }
        } catch (Exception e) {
            Log.e("Shake", "Error clearing database table: " + e.getMessage());
        }
    }

    private void resetAmountField() {
        // Resetuj amount polje
        if (etAmount != null) {
            etAmount.setText(""); // Prazan string
            etAmount.clearFocus(); // Ukloni fokus

            // Opciono: postavi hint nazad
            etAmount.setHint("Enter amount");
        }

        // Resetuj rezultat
        if (tvResult != null) {
            tvResult.setText("Result: ");
        }

        // Sakrij greške
        if (tvError != null) {
            tvError.setVisibility(View.GONE);
        }
    }

    private void setupListeners() {
        btnConvert.setOnClickListener(v -> performConversion());

        spinnerFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String newBase = currencies[position];
                if (!newBase.equals(currentBaseCurrency)) {
                    currentBaseCurrency = newBase;
                    viewModel.fetchExchangeRates(currentBaseCurrency);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void performConversion() {
        String amountText = etAmount.getText().toString();
        if (amountText.isEmpty()) {
            tvError.setText("Please enter amount");
            tvError.setVisibility(View.VISIBLE);
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            String fromCurrency = (String) spinnerFrom.getSelectedItem();
            String toCurrency = (String) spinnerTo.getSelectedItem();

            viewModel.convertCurrency(fromCurrency, toCurrency, amount);

        } catch (NumberFormatException e) {
            tvError.setText("Invalid amount");
            tvError.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(shakeDetector, accelerometer,
                    SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (accelerometer != null) {
            sensorManager.unregisterListener(shakeDetector);
        }
    }
}