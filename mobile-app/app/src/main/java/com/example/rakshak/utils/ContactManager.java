package com.example.rakshak.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.rakshak.model.EmergencyContact;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ContactManager {

    private static final String PREFS_NAME = "emergency_contacts";
    private static final String CONTACTS_KEY = "contacts_list";

    private Context context;
    private SharedPreferences sharedPreferences;
    private Gson gson;

    public ContactManager(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    public List<EmergencyContact> getEmergencyContacts() {
        String contactsJson = sharedPreferences.getString(CONTACTS_KEY, null);
        if (contactsJson != null) {
            Type listType = new TypeToken<List<EmergencyContact>>(){}.getType();
            return gson.fromJson(contactsJson, listType);
        }
        return getDefaultContacts();
    }

    private List<EmergencyContact> getDefaultContacts() {
        List<EmergencyContact> defaultContacts = new ArrayList<>();

        // Add default emergency services
        defaultContacts.add(new EmergencyContact(
                "emergency_services",
                "Emergency Services",
                "108",
                "Emergency Services",
                true
        ));

        // Add sample contacts (in real app, these would be empty initially)
        defaultContacts.add(new EmergencyContact(
                "contact_1",
                "Primary Contact",
                "+91 9876543210",
                "Family",
                true
        ));

        defaultContacts.add(new EmergencyContact(
                "contact_2",
                "Secondary Contact",
                "+91 9876543211",
                "Friend",
                false
        ));

        return defaultContacts;
    }

    public void saveEmergencyContacts(List<EmergencyContact> contacts) {
        String contactsJson = gson.toJson(contacts);
        sharedPreferences.edit()
                .putString(CONTACTS_KEY, contactsJson)
                .apply();
    }

    public void addEmergencyContact(EmergencyContact contact) {
        List<EmergencyContact> contacts = getEmergencyContacts();
        contact.setId(UUID.randomUUID().toString());
        contacts.add(contact);
        saveEmergencyContacts(contacts);
    }

    public void updateEmergencyContact(EmergencyContact updatedContact) {
        List<EmergencyContact> contacts = getEmergencyContacts();
        for (int i = 0; i < contacts.size(); i++) {
            if (contacts.get(i).getId().equals(updatedContact.getId())) {
                contacts.set(i, updatedContact);
                break;
            }
        }
        saveEmergencyContacts(contacts);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void removeEmergencyContact(String contactId) {
        List<EmergencyContact> contacts = getEmergencyContacts();
        contacts.removeIf(contact -> contact.getId().equals(contactId));
        saveEmergencyContacts(contacts);
    }

    public EmergencyContact getPrimaryContact() {
        List<EmergencyContact> contacts = getEmergencyContacts();
        for (EmergencyContact contact : contacts) {
            if (contact.isPrimary() && !contact.getId().equals("emergency_services")) {
                return contact;
            }
        }
        return null;
    }

    public List<String> getEmergencyPhoneNumbers() {
        List<String> phoneNumbers = new ArrayList<>();
        List<EmergencyContact> contacts = getEmergencyContacts();

        for (EmergencyContact contact : contacts) {
            phoneNumbers.add(contact.getPhoneNumber());
        }

        return phoneNumbers;
    }

    public void notify(String s, Object lastKnownLocationString) {
    }
}