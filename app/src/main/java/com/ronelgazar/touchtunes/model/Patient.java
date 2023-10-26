package com.ronelgazar.touchtunes.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import android.util.Log;

public class Patient  implements Serializable {

    private String uid;
    private boolean isActive;
    private Mode mode;
    private String name;
    private Playlist playlist;

    public Patient() {
    }

    public Patient(String uid, boolean isActive, Mode mode, String name, DocumentReference playlist) {
        this.uid = uid;
        this.isActive = isActive;
        this.mode = mode;
        this.name = name;
        this.playlist = new Playlist(playlist);
    }

    // create a constructor that accepts a document or documentReference as needed
    // and populate the fields
    public Patient(Map<String, Object> patientData) {
        if (patientData != null) {
            uid = (String) patientData.get("uid");
            isActive = (boolean) patientData.get("isActive");
            mode = new Mode((DocumentReference) patientData.get("mode"));
            name = (String) patientData.get("name");
            playlist = new Playlist((DocumentReference) patientData.get("playlist"));
        }
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Playlist getPlaylist() {
        return playlist;
    }


    public void printPatient() {
        Log.d("Patient", "Patient uid: " + uid);
        Log.d("Patient", "Patient isActive: " + isActive);
        Log.d("Patient", "Patient mode: " + mode);
        Log.d("Patient", "Patient name: " + name);
        Log.d("Patient", "Patient playlist: " + playlist);
    }


}