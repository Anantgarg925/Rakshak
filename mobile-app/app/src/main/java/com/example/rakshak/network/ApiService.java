package com.example.rakshak.network;

import com.example.rakshak.model.AccidentHistory;
import com.example.rakshak.model.EmergencyContact;
import com.example.rakshak.model.User;
import com.example.rakshak.model.AccidentReport;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("api/user/register")
    Call<Map<String, String>> registerUser(@Body User user);

    @POST("api/accident/report")
    Call<Map<String, String>> reportAccident(@Body AccidentReport report);

    @GET("api/user")
    Call<User> getUser(@Query("deviceId") String deviceId);

    @PUT("api/user/update")
    Call<Map<String, String>> updateUser(@Body User user);

    @GET("api/contacts")
    Call<List<EmergencyContact>> getContacts(@Query("deviceId") String deviceId);

    @POST("api/contacts")
    Call<EmergencyContact> addContact(@Query("deviceId") String deviceId, @Body EmergencyContact contact);

    @DELETE("api/contacts/{contactId}")
    Call<Map<String, String>> deleteContact(@Path("contactId") String contactId);

    @GET("api/accident/history")
    Call<List<AccidentHistory>> getHistory(@Query("deviceId") String deviceId);
}
