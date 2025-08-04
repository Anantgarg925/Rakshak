package com.example.rakshak.ui.viewmodels;

import android.app.Application;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.rakshak.model.AccidentHistory;
import com.example.rakshak.network.ApiService;
import com.example.rakshak.network.RetrofitClient;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryViewModel extends AndroidViewModel {

    private final ApiService apiService;
    private final String deviceId;

    private final MutableLiveData<List<AccidentHistory>> historyLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public HistoryViewModel(@NonNull Application application) {
        super(application);
        this.apiService = RetrofitClient.getApiService();
        this.deviceId = Settings.Secure.getString(application.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public LiveData<List<AccidentHistory>> getHistory() {
        return historyLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }

    public void fetchHistory() {
        apiService.getHistory(deviceId).enqueue(new Callback<List<AccidentHistory>>() {
            @Override
            public void onResponse(@NonNull Call<List<AccidentHistory>> call, @NonNull Response<List<AccidentHistory>> response) {
                if (response.isSuccessful()) {
                    historyLiveData.setValue(response.body());
                } else {
                    errorLiveData.setValue("Error fetching history: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<AccidentHistory>> call, @NonNull Throwable t) {
                errorLiveData.setValue("Network Failure: " + t.getMessage());
            }
        });
    }
}