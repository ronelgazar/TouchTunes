package com.ronelgazar.touchtunes.model;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.google.firebase.firestore.DocumentReference;
import com.ronelgazar.touchtunes.util.FirebaseUtil;

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

    public Patient() {
    }

    protected Patient(Parcel in) {
        this.uid = in.readString();
        this.isActive = in.readByte() != 0;
        this.name = in.readString();
        this.mode = in.readParcelable(Mode.class.getClassLoader());
        this.playlist = in.readParcelable(Playlist.class.getClassLoader());
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

    public Patient(String uid, boolean isActive, Mode mode, String name, Playlist playlist) {
        this.uid = uid;
        this.isActive = isActive;
        this.mode = mode;
        this.name = name;
        this.playlist = playlist;
    }

    public Patient(Object data) {
        Map<String, Object> dataMap = (Map<String, Object>) data;

        this.uid = (String) dataMap.get("uid");
        this.isActive = (Boolean) dataMap.get("isActive");
        this.name = (String) dataMap.get("name");
        DocumentReference playlistRef = (DocumentReference) dataMap.get("playlist");
        DocumentReference modeRef = (DocumentReference) dataMap.get("mode");
        CompletableFuture playlistFuture = firebaseUtil.getDocRefData(playlistRef);
        CompletableFuture modeFuture = firebaseUtil.getDocRefData(modeRef);
        // Wait for both the playlist and mode data to be fetched before creating the
        // Patient object.
        CompletableFuture.allOf(playlistFuture, modeFuture).thenAcceptAsync(v -> {
            this.uid = (String) dataMap.get("uid");
            this.isActive = (Boolean) dataMap.get("isActive");
            this.name = (String) dataMap.get("name");
            this.playlist = (Playlist) playlistFuture.getNow(null);
            this.mode = (Mode) modeFuture.getNow(null);
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

        // mode.printMode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeByte((byte) (isActive ? 1 : 0));
        dest.writeString(name);
        dest.writeParcelable(mode, flags);
        dest.writeParcelable(playlist, flags);
    }

}