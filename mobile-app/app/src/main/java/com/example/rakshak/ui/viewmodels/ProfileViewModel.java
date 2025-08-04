package com.example.rakshak.ui.viewmodels;

import android.app.Application;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.rakshak.model.EmergencyContact;
import com.example.rakshak.repository.EmergencyContactRepository;
import com.example.rakshak.model.User;
import com.example.rakshak.repository.UserRepository;

import java.util.List;
import java.util.Map;

public class ProfileViewModel extends AndroidViewModel {

    private final EmergencyContactRepository contactRepository;
    private final UserRepository userRepository;
    private final String deviceId;

    private final MutableLiveData<List<EmergencyContact>> contactsLiveData = new MutableLiveData<>();
    private final MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        this.contactRepository = new EmergencyContactRepository();
        this.userRepository = new UserRepository();
        this.deviceId = Settings.Secure.getString(application.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public LiveData<List<EmergencyContact>> getContacts() {
        return contactsLiveData;
    }
    public LiveData<User> getUser() {
        return userLiveData;
    }
    public LiveData<String> getError() {
        return errorLiveData;
    }

    public void fetchContacts() {
        contactRepository.getContacts(deviceId, new EmergencyContactRepository.ContactCallback<List<EmergencyContact>>() {
            @Override
            public void onSuccess(List<EmergencyContact> data) {
                contactsLiveData.setValue(data);
            }
            @Override
            public void onFailure(String errorMessage) {
                errorLiveData.setValue(errorMessage);
            }
        });
    }

    public void addContact(EmergencyContact contact) {
        contactRepository.addContact(deviceId, contact, new EmergencyContactRepository.ContactCallback<EmergencyContact>() {
            @Override
            public void onSuccess(EmergencyContact data) {
                fetchContacts(); // Refresh list
            }
            @Override
            public void onFailure(String errorMessage) {
                errorLiveData.setValue(errorMessage);
            }
        });
    }

    public void deleteContact(EmergencyContact contact) {
        contactRepository.deleteContact(contact.getId(), new EmergencyContactRepository.ContactCallback<Map<String, String>>() {
            @Override
            public void onSuccess(Map<String, String> data) {
                fetchContacts(); // Refresh list
            }
            @Override
            public void onFailure(String errorMessage) {
                errorLiveData.setValue(errorMessage);
            }
        });
    }

    public void fetchUser() {
        userRepository.getUser(deviceId, new UserRepository.UserCallback<User>() {
            @Override
            public void onSuccess(User data) {
                userLiveData.setValue(data);
            }
            @Override
            public void onFailure(String errorMessage) {
                errorLiveData.setValue(errorMessage);
            }
        });
    }

    public void updateUser(User user) {
        userRepository.updateUser(user, new UserRepository.UserCallback<Map<String, String>>() {
            @Override
            public void onSuccess(Map<String, String> data) {
                fetchUser(); // Refresh user data
            }
            @Override
            public void onFailure(String errorMessage) {
                errorLiveData.setValue(errorMessage);
            }
        });
    }
}