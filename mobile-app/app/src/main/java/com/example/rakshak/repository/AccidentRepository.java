package com.example.rakshak.repository;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.rakshak.model.AccidentReport;
import com.example.rakshak.network.ApiService;
import com.example.rakshak.network.RetrofitClient;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccidentRepository {

    private static final String TAG = "AccidentRepository";
    private final ApiService apiService;

    // A callback interface to send results back to the UI
    public interface ApiResultCallback {
        void onSuccess(String responseMessage);
        void onFailure(String errorMessage);
    }

    public AccidentRepository() {
        this.apiService = RetrofitClient.getApiService();
    }

    /**
     * Reports an accident to the backend.
     * @param deviceId The unique ID of the device.
     * @param latitude The current latitude.
     * @param longitude The current longitude.
     * @param callback The callback to handle the success or failure response.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void reportAccident(String deviceId, double latitude, double longitude, final ApiResultCallback callback) {

        // Create the request body object
        String timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now());
        AccidentReport report = new AccidentReport(deviceId, latitude, longitude, "High", timestamp);

        Log.d(TAG, "Reporting accident for device: " + deviceId);

        // Make the asynchronous API call
        apiService.reportAccident(report).enqueue(new Callback<Map<String,String>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String,String>> call, @NonNull Response<Map<String,String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String message=response.body().get("message");
                    Log.i(TAG, "API call successful. Response: " + message);
                    callback.onSuccess(message);
                } else {
                    String errorMsg = "API call failed with code: " + response.code();
                    Log.e(TAG, errorMsg);
                    callback.onFailure(errorMsg);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String,String>> call, @NonNull Throwable t) {
                String errorMsg = "Network request failed: " + t.getMessage();
                Log.e(TAG, errorMsg);
                callback.onFailure(errorMsg);
            }
        });
    }
}