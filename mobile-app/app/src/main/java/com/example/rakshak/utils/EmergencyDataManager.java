package com.example.rakshak.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.rakshak.model.EmergencyRecord;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EmergencyDataManager {

    private static final String PREFS_NAME = "emergency_data";
    private static final String RECORDS_KEY = "emergency_records";

    private Context context;
    private SharedPreferences sharedPreferences;
    private Gson gson;

    public EmergencyDataManager(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    public List<EmergencyRecord> getAllEmergencyRecords() {
        String recordsJson = sharedPreferences.getString(RECORDS_KEY, null);
        if (recordsJson != null) {
            Type listType = new TypeToken<List<EmergencyRecord>>(){}.getType();
            List<EmergencyRecord> records = gson.fromJson(recordsJson, listType);
            return records != null ? records : new ArrayList<>();
        }
        return new ArrayList<>();
    }

    public void saveEmergencyRecord(EmergencyRecord record) {
        List<EmergencyRecord> records = getAllEmergencyRecords();
        records.add(0, record); // Add to beginning for newest first
        saveAllRecords(records);
    }

    private void saveAllRecords(List<EmergencyRecord> records) {
        String recordsJson = gson.toJson(records);
        sharedPreferences.edit()
                .putString(RECORDS_KEY, recordsJson)
                .apply();
    }

    public void updateEmergencyRecord(EmergencyRecord updatedRecord) {
        List<EmergencyRecord> records = getAllEmergencyRecords();
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).getId().equals(updatedRecord.getId())) {
                records.set(i, updatedRecord);
                break;
            }
        }
        saveAllRecords(records);
    }

    public List<EmergencyRecord> getRecordsForToday() {
        List<EmergencyRecord> allRecords = getAllEmergencyRecords();
        List<EmergencyRecord> todayRecords = new ArrayList<>();

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        long todayStart = today.getTimeInMillis();

        for (EmergencyRecord record : allRecords) {
            if (record.getTimestamp() >= todayStart) {
                todayRecords.add(record);
            }
        }

        return todayRecords;
    }

    public List<EmergencyRecord> getRecordsForWeek() {
        List<EmergencyRecord> allRecords = getAllEmergencyRecords();
        List<EmergencyRecord> weekRecords = new ArrayList<>();

        Calendar weekStart = Calendar.getInstance();
        weekStart.add(Calendar.DAY_OF_YEAR, -7);
        long weekStartTime = weekStart.getTimeInMillis();

        for (EmergencyRecord record : allRecords) {
            if (record.getTimestamp() >= weekStartTime) {
                weekRecords.add(record);
            }
        }

        return weekRecords;
    }

    public List<EmergencyRecord> getRecordsForMonth() {
        List<EmergencyRecord> allRecords = getAllEmergencyRecords();
        List<EmergencyRecord> monthRecords = new ArrayList<>();

        Calendar monthStart = Calendar.getInstance();
        monthStart.add(Calendar.DAY_OF_YEAR, -30);
        long monthStartTime = monthStart.getTimeInMillis();

        for (EmergencyRecord record : allRecords) {
            if (record.getTimestamp() >= monthStartTime) {
                monthRecords.add(record);
            }
        }

        return monthRecords;
    }

    public int getTodayAlertsCount() {
        return getRecordsForToday().size();
    }

    public int getTotalAlertsCount() {
        return getAllEmergencyRecords().size();
    }

    public void clearAllRecords() {
        sharedPreferences.edit()
                .remove(RECORDS_KEY)
                .apply();
    }

    public EmergencyRecord createTestRecord() {
        EmergencyRecord testRecord = new EmergencyRecord(
                EmergencyRecord.AlertType.TEST_ALERT,
                "Test Location",
                28.7041, // Delhi lat
                77.1025  // Delhi lng
        );
        testRecord.setStatus(EmergencyRecord.Status.SENT);
        testRecord.setResponseTime("< 5s");
        testRecord.setNotes("Test alert generated for system verification");
        return testRecord;
    }
}