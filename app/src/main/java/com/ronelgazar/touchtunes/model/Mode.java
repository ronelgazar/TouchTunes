package com.ronelgazar.touchtunes.model;

import java.util.HashMap;
import java.util.Map;

import com.google.firebase.firestore.DocumentReference;
import com.ronelgazar.touchtunes.util.FirebaseUtil;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Mode implements Parcelable {

    private String name;
    private Map<String, Object> settings;
    private DocumentReference documentReference;

     protected Mode(Parcel in) {
        name = in.readString();
        settings = new HashMap<>();
        in.readMap(settings, Object.class.getClassLoader());
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
        dest.writeMap(settings);
    }


    public Mode() {
    }

    public Mode(String name, Map<String, Object> settings) {
        this.name = name;
        this.settings = settings;
    }

    public Mode(Map<String,Object> mode)
    {
        this.name = (String) mode.get("name");
        this.settings = (Map<String, Object>) mode.get("settings");
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

    public int getInteractionDuration() {
        return (int) settings.get("interaction_duration");
    }

    public void setInteractionDuration(String interactionDuration) {
        settings.put("interaction_duration", interactionDuration);
    }

    public String getInteractionType() {
        return (String) settings.get("interaction_type");
    }

    public void setInteractionType(String interactionType) {
        settings.put("interaction_type", interactionType);
    }

    public int getLighting() {
        return (int) settings.get("lighting");
    }

    public void setLighting(String lighting) {
        settings.put("lighting", lighting);
    }

    public String getSessionDuration() {
        return (String) settings.get("session_duration");
    }

    public void setSessionDuration(String sessionDuration) {
        settings.put("session_duration", sessionDuration);
    }

    public boolean getSoundInteraction() {
        return  (boolean) settings.get("sound_interaction");
    }

    public void setSoundInteraction(String soundInteraction) {
        settings.put("sound_interaction", soundInteraction);
    }

    public int getVibration() {
        return (int) settings.get("vibration");
    }

    public void setVibration(String vibration) {
        settings.put("vibration", vibration);
    }

    public void printMode() {
        Log.d("Patient", "Mode documentReference: " + documentReference);
        Log.d("Patient", "Mode name: " + name);
        Log.d("Patient", "Mode settings: " + settings);
    }
}