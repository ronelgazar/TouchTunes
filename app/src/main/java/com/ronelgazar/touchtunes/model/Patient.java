package com.ronelgazar.touchtunes.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class Patient implements Parcelable {

    private String uid;
    private boolean isActive;
    private Mode mode;
    private String name;
    private Playlist playlist;

    public interface PatientCallback {
        void onPatientDataLoaded(Patient patient);
    }

    protected Patient(Parcel in) {
        this.uid = in.readString();
        this.isActive = in.readByte() != 0;
        this.name = in.readString();
        this.mode = in.readParcelable(Mode.class.getClassLoader());
        this.playlist = in.readParcelable(Playlist.class.getClassLoader());

    }

    public Patient() {
        // Default constructor
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


    private void loadMode(DocumentReference modeRef) {
        if (modeRef != null) {
            modeRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        this.mode = new Mode(Objects.requireNonNull(document.getData()));
                    } else {
                        Log.e("Patient", "Mode document not found");
                    }
                } else {
                    Log.e("Patient", "Error getting mode document", task.getException());
                }
            });
        } else {
            Log.e("Patient", "Invalid mode reference");
        }
    }

    private void loadPlaylist(DocumentReference playlistRef) {
        if (playlistRef != null) {
            playlistRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        this.playlist = new Playlist(document.getData());
                    } else {
                        Log.e("Patient", "Playlist document not found");
                    }
                } else {
                    Log.e("Patient", "Error getting playlist document", task.getException());
                }
            });
        } else {
            Log.e("Patient", "Invalid playlist reference");
        }
    }



    public void save() {
        // Implementation for saving patient data
    }

    // Getters and setters



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeByte((byte) (isActive ? 1 : 0));
        dest.writeString(name);
        // add mode and playlist
        dest.writeParcelable(mode, flags);
        dest.writeParcelable(playlist, flags);
    }

    public Patient(Map<String, Object> data) {
        this.uid = (String) data.get("uid");
        this.isActive = (Boolean) data.get("isActive");
        this.name = (String) data.get("name");
        loadMode((DocumentReference) data.get("mode"));
        loadPlaylist((DocumentReference) data.get("playlist"));
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
        return this.mode;
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

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }
}
