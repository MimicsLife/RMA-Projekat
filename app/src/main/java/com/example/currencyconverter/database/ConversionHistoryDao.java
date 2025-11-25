package com.example.currencyconverter.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.lifecycle.LiveData;
import java.util.List;
import com.example.currencyconverter.models.ConversionHistory;

@Dao
public interface ConversionHistoryDao {
    @Insert
    void insert(ConversionHistory history);

    @Query("SELECT * FROM conversion_history ORDER BY timestamp DESC LIMIT 50")
    LiveData<List<ConversionHistory>> getRecentConversions();

    @Query("DELETE FROM conversion_history WHERE timestamp < :cutoffTime")
    void deleteOldEntries(long cutoffTime);

    @Query("DELETE FROM conversion_history")
    void deleteAll();
}