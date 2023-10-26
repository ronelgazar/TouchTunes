package com.ronelgazar.touchtunes.model;

import java.util.Map;

import com.google.firebase.firestore.DocumentReference;

import android.util.Log;

public class Mode {

    private String name;
    private Map<String, String> settings;
    private DocumentReference documentReference;

    public Mode() {
    }

    public Mode(String name, Map<String, String> settings) {
        this.name = name;
        this.settings = settings;
    }

    public Mode(DocumentReference documentReference) {
        this.documentReference = documentReference;
        // get the name and settings from the documentReference
        if (documentReference !=null)
        {
            name = documentReference.getId();
            settings = (Map<String, String>) documentReference.get();
            Log.d("Mode", "Mode name: " + name);
            Log.d("Mode", "Mode settings: " + settings);
        }
        else
        {
            Log.d("Mode", "Mode documentReference is null");
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getSettings() {
        return settings;
    }

    public void setSettings(Map<String, String> settings) {
        this.settings = settings;
    }

    public String getInteractionDuration() {
        return settings.get("interaction_duration");
    }

    public void setInteractionDuration(String interactionDuration) {
        settings.put("interaction_duration", interactionDuration);
    }

    public String getInteractionType() {
        return settings.get("interaction_type");
    }

    public void setInteractionType(String interactionType) {
        settings.put("interaction_type", interactionType);
    }

    public String getLighting() {
        return settings.get("lighting");
    }

    public void setLighting(String lighting) {
        settings.put("lighting", lighting);
    }

    public String getSessionDuration() {
        return settings.get("session_duration");
    }

    public void setSessionDuration(String sessionDuration) {
        settings.put("session_duration", sessionDuration);
    }

    public String getSoundInteraction() {
        return settings.get("sound_interaction");
    }

    public void setSoundInteraction(String soundInteraction) {
        settings.put("sound_interaction", soundInteraction);
    }

    public String getVibration() {
        return settings.get("vibration");
    }

    public void setVibration(String vibration) {
        settings.put("vibration", vibration);
    }
}