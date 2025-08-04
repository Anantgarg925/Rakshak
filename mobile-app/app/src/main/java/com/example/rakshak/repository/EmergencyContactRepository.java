package com.example.rakshak.repository;

import androidx.annotation.NonNull;
import com.example.rakshak.model.EmergencyContact;
import com.example.rakshak.network.ApiService;
import com.example.rakshak.network.RetrofitClient;

import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmergencyContactRepository {

    private final ApiService apiService;

    public interface ContactCallback<T> {
        void onSuccess(T data);
        void onFailure(String errorMessage);
    }

    public EmergencyContactRepository() {
        this.apiService = RetrofitClient.getApiService();
    }

    public void getContacts(String deviceId, final ContactCallback<List<EmergencyContact>> callback) {
        apiService.getContacts(deviceId).enqueue(new Callback<List<EmergencyContact>>() {
            @Override
            public void onResponse(@NonNull Call<List<EmergencyContact>> call, @NonNull Response<List<EmergencyContact>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure("Error fetching contacts: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<EmergencyContact>> call, @NonNull Throwable t) {
                callback.onFailure("Network failure: " + t.getMessage());
            }
        });
    }

    public void addContact(String deviceId, EmergencyContact contact, final ContactCallback<EmergencyContact> callback) {
        apiService.addContact(deviceId, contact).enqueue(new Callback<EmergencyContact>() {
            @Override
            public void onResponse(@NonNull Call<EmergencyContact> call, @NonNull Response<EmergencyContact> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure("Error adding contact: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<EmergencyContact> call, @NonNull Throwable t) {
                callback.onFailure("Network failure: " + t.getMessage());
            }
        });
    }

    public void deleteContact(String contactId, final ContactCallback<Map<String, String>> callback) {
        apiService.deleteContact(contactId).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, String>> call, @NonNull Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure("Error deleting contact: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, String>> call, @NonNull Throwable t) {
                callback.onFailure("Network failure: " + t.getMessage());
            }
        });
    }
}
