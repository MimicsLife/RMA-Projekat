package com.example.currencyconverter.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import com.example.currencyconverter.R;
import com.example.currencyconverter.models.ConversionHistory;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private List<ConversionHistory> historyList;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView fromCurrency, toCurrency, amount, result, date;

        public ViewHolder(View view) {
            super(view);
            fromCurrency = view.findViewById(R.id.tvFromCurrency);
            toCurrency = view.findViewById(R.id.tvToCurrency);
            amount = view.findViewById(R.id.tvAmount);
            result = view.findViewById(R.id.tvResult);
            date = view.findViewById(R.id.tvDate);
        }
    }

    public HistoryAdapter(List<ConversionHistory> historyList) {
        this.historyList = historyList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_conversion_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ConversionHistory history = historyList.get(position);

        holder.fromCurrency.setText(history.getFromCurrency());
        holder.toCurrency.setText(history.getToCurrency());
        holder.amount.setText(String.format("%.2f", history.getAmount()));
        holder.result.setText(String.format("%.2f", history.getConvertedAmount()));
        holder.date.setText(dateFormat.format(history.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public void updateData(List<ConversionHistory> newHistory) {
        this.historyList = newHistory;
        notifyDataSetChanged();
    }
}