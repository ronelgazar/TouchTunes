package com.ronelgazar.touchtunes.model;

import java.util.Map;
import com.google.firebase.firestore.DocumentReference;
import com.ronelgazar.touchtunes.services.FirebaseService;
import com.ronelgazar.touchtunes.services.FirebaseService.DataCallback;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Patient implements Parcelable {

    private String uid;
    private boolean isActive;
    private Mode mode;
    private String name;
    private Playlist playlist;
    private FirebaseService firebaseService;
    private DocumentReference playlistRef;
    private DocumentReference modeRef;

    protected Patient(Parcel in) {
        this.playlist = in.readParcelable(Playlist.class.getClassLoader());
        this.mode = in.readParcelable(Mode.class.getClassLoader());
        this.uid = in.readString();
        this.isActive = in.readByte() != 0;
        this.name = in.readString();
    }
    public Patient()
    {
    }
    public static final Creator<Patient> CREATOR = new Creator<Patient>() {
        @Override
        public Patient createFromParcel(Parcel in) {
            return new Patient(in);
        }

        @Override
        public Patient[] newArray(int size) {
            return new Patient[size];
        }
    };

    public Patient(Object data) {
        Map<String, Object> dataMap = (Map<String, Object>) data;

        firebaseService = new FirebaseService();
        this.uid = (String) dataMap.get("uid");
        this.isActive = (Boolean) dataMap.get("isActive");
        this.name = (String) dataMap.get("name");
        this.modeRef = (DocumentReference) dataMap.get("mode");
        this.playlistRef = (DocumentReference) dataMap.get("playlist");

        firebaseService.getDocRefData(this.playlistRef, new DataCallback() {
            @Override
            public void onCallback(Map<String, Object> data) {
                playlist = new Playlist(data);
            }
        });
        
        firebaseService.getDocRefData(this.modeRef, new DataCallback() {
            @Override
            public void onCallback(Map<String, Object> data) {
                mode = new Mode(data);
            }
        });
    }

    public void save(Context context) {
        Intent intent = new Intent(context, FirebaseService.class);
        intent.setAction("com.ronelgazar.touchtunes.action.CREATE_PATIENT");
        intent.putExtra("patient", this);
        context.startService(intent);
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

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
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
        return this.playlist;
    }

    public void printPatient() {
        Log.d("Patient", "Patient uid: " + uid);
        Log.d("Patient", "Patient isActive: " + isActive);
        Log.d("Patient", "Patient name: " + name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(playlist, flags);
        dest.writeParcelable(mode, flags);
        dest.writeString(uid);
        dest.writeByte((byte) (isActive ? 1 : 0));
        dest.writeString(name);
    }

}