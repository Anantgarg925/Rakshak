package com.example.rakshak.repository;

import androidx.annotation.NonNull;
import com.example.rakshak.model.User;
import com.example.rakshak.network.ApiService;
import com.example.rakshak.network.RetrofitClient;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {

    private final ApiService apiService;

    public interface UserCallback<T> {
        void onSuccess(T data);
        void onFailure(String errorMessage);
    }

    public UserRepository() {
        this.apiService = RetrofitClient.getApiService();
    }

    public void registerUser(User user, final UserCallback<Map<String, String>> callback) {
        apiService.registerUser(user).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, String>> call, @NonNull Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure("Registration failed: " + response.code());
                }
            }
            @Override
            public void onFailure(@NonNull Call<Map<String, String>> call, @NonNull Throwable t) {
                callback.onFailure("Network failure: " + t.getMessage());
            }
        });
    }

    public void getUser(String deviceId, final UserCallback<User> callback) {
        apiService.getUser(deviceId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure("Failed to get user: " + response.code());
                }
            }
            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                callback.onFailure("Network failure: " + t.getMessage());
            }
        });
    }

    public void updateUser(User user, final UserCallback<Map<String, String>> callback) {
        apiService.updateUser(user).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, String>> call, @NonNull Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure("Update failed: " + response.code());
                }
            }
            @Override
            public void onFailure(@NonNull Call<Map<String, String>> call, @NonNull Throwable t) {
                callback.onFailure("Network failure: " + t.getMessage());
            }
        });
    }
}