package com.ronelgazar.touchtunes.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.ronelgazar.touchtunes.util.DefualtData;
import com.ronelgazar.touchtunes.util.FirebaseUtil;
import com.ronelgazar.touchtunes.util.FirebaseUtil.DataCallback;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Patient implements Parcelable {

    private String uid;
    private boolean isActive;
    private Mode mode;
    private String name;
    private Playlist playlist;
    private static FirebaseUtil firebaseUtil = new FirebaseUtil();


    DocumentReference playlistRef;
    DocumentReference modeRef;

    protected Patient(Parcel in) {
        this.playlist = in.readParcelable(Playlist.class.getClassLoader());
        this.mode = in.readParcelable(Mode.class.getClassLoader());
        this.uid = in.readString();
        this.isActive = in.readByte() != 0;
        this.name = in.readString();
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


        this.uid = (String) dataMap.get("uid");
        this.isActive = (Boolean) dataMap.get("isActive");
        this.name = (String) dataMap.get("name");
        this.modeRef = (DocumentReference) dataMap.get("mode");
        this.playlistRef = (DocumentReference) dataMap.get("playlist");

        firebaseUtil.getDocRefData(this.playlistRef, new DataCallback() {
            @Override
            public void onCallback(Map<String, Object> data) {
                playlist = new Playlist(data);
            }
        });
    
        firebaseUtil.getDocRefData(this.modeRef, new DataCallback() {
            @Override
            public void onCallback(Map<String, Object> data) {
                mode = new Mode(data);
            }
        });
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
        Log.d("AAAAAAA", "Patient uid: " + uid);
        Log.d("AAAAAAA", "Patient isActive: " + isActive);
        Log.d("AAAAAAAAAA", "Patient name: " + name);

        //mode.printMode();
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