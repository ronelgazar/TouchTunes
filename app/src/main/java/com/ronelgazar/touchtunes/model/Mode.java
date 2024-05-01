package com.ronelgazar.touchtunes.model;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.firebase.firestore.DocumentReference;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class Mode implements Parcelable {

    private String name;
    private Map<String, Object> settings;
    private DocumentReference documentReference;


    protected Mode(Parcel in) {
        name = in.readString();
        Bundle settingsBundle = in.readBundle(getClass().getClassLoader());
        if (settingsBundle != null) {
            settings = new HashMap<>();
            for (String key : settingsBundle.keySet()) {
                settings.put(key, settingsBundle.get(key));
            }
        }
    }

    public static final Creator<Mode> CREATOR = new Creator<Mode>() {
        @Override
        public Mode createFromParcel(Parcel in) {
            return new Mode(in);
        }

        @Override
        public Mode[] newArray(int size) {
            return new Mode[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        Bundle settingsBundle = new Bundle();
        if (settings != null) {
            for (String key : settings.keySet()) {
                settingsBundle.putParcelable(key, getObjectAsParcelable(settings.get(key)));
            }
        }
        dest.writeBundle(settingsBundle);
    }

    private Parcelable getObjectAsParcelable(Object object) {
        if (object instanceof Parcelable) {
            return (Parcelable) object;
        }
        return null;
    }

    public Mode() {
        settings = new HashMap<>();
    }

    public Mode(String name, Map<String, Object> settings) {
        this.name = name;
        this.settings = settings;
    }

    public Mode(Map<String, Object> mode) {
        // ... (null checks and type safety as before)

        // Assign values directly to fields from the map
        this.name = (String) mode.get("name");
        this.settings = (Map<String, Object>) mode.get("settings");

        // Iterate through remaining entries and assign to fields
        for (Map.Entry<String, Object> entry : mode.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            try {
                Field field = Mode.class.getDeclaredField(key);
                if (field != null) { // Ensure field exists
                    // Make field accessible for private fields
                    field.setAccessible(true);
                    field.set(this, value);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                // Handle cases where field doesn't exist or is inaccessible
                // You can log a warning or throw an exception
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getSettings() {
        return settings;
    }

    public void setSettings(Map<String, Object> settings) {
        this.settings = settings;
    }

    public String getInteractionDuration() {
        return String.valueOf( settings.get("interaction_duration"));
    }

    public void setInteractionDuration(String interactionDuration) {
        int interactionDurationInt = Integer.parseInt(interactionDuration);
        settings.put("interaction_duration", interactionDurationInt);
    }


    public String getInteractionType() {
        return String.valueOf(settings.get("interaction_type"));
    }

    public void setInteractionType(String interactionType) {
        settings.put("interaction_type", interactionType);
    }

    public String getSessionDuration() {
        return String.valueOf(settings.get("session_duration"));
    }

    public void setSessionDuration(String sessionDuration) {
        settings.put("session_duration", sessionDuration);
    }

    public boolean getSoundInteraction() {
        return String.valueOf(settings.get("sound_interaction")).equals("true");
    }

    public void setSoundInteraction(boolean soundInteraction) {
        settings.put("sound_interaction", soundInteraction);
    }

    public String getVibrationIntensity() {
        return String.valueOf(settings.get("vibration_intensity"));
    }

    public void setVibrationIntensity(String vibrationIntensity) {
        int vibrationIntensityInt = Integer.parseInt(vibrationIntensity);
        settings.put("vibration_intensity", vibrationIntensityInt);
    }

    public String getSessionDurationInMinutes() {
        String[] parts = getSessionDuration().split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        return String.valueOf( hours * 60 + minutes);
    }

    public void printMode() {
        Log.d("Mode", "Mode documentReference: " + documentReference);
        Log.d("Mode", "Mode name: " + name);
        Log.d("Mode", "Mode settings: " + settings);
    }

    public void setSharedPreference(SharedPreferences sharedPreferences) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("interaction_duration", this.getInteractionDuration());
        editor.putString("interaction_type", this.getInteractionType());
        editor.putString("session_duration", this.getSessionDuration());
        editor.putBoolean("sound_interaction", this.getSoundInteraction());
        editor.putString("vibration_intensity", this.getVibrationIntensity());
        editor.apply();
    }
}
