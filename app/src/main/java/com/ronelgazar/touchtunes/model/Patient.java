package com.ronelgazar.touchtunes.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
        uid = in.readString();
        isActive = in.readByte() != 0;
        name = in.readString();
        playlist = in.readParcelable(Playlist.class.getClassLoader());
        mode = in.readParcelable(Mode.class.getClassLoader());
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

    public Patient(DocumentReference documentReference, FirebaseUtil.DataCallback callback) {
        if (documentReference != null) {
            firebaseUtil.getDocRefData(documentReference, new FirebaseUtil.DataCallback() {
                @Override
                public void onCallback(Map<String, Object> data) {
                    if (data != null) {
                        isActive = (Boolean) data.get("isActive");
                        DocumentReference modeRef = (DocumentReference) data.get("mode");
                        if (modeRef != null) {
                            firebaseUtil.getDocRefData(modeRef, new FirebaseUtil.DataCallback() {
                                @Override
                                public void onCallback(Map<String, Object> data) {
                                    mode = new Mode(data);
                                }
                            });
                        }
                        name = (String) data.get("name");
                        DocumentReference playlistRef = (DocumentReference) data.get("playlist");
                        if (playlistRef != null) {
                            firebaseUtil.getDocRefData(playlistRef, new FirebaseUtil.DataCallback() {
                                @Override
                                public void onCallback(Map<String, Object> data) {
                                    playlist = new Playlist(data);
                                }
                            });
                        }
                    } else {
                        Log.d("Patient", "No such document");
                    }
                }
            });
        }
    }

    public Patient(Map<String, Object> data) {
        this.uid = (String) data.get("uid");
        this.isActive = (Boolean) data.get("isActive");
        this.name = (String) data.get("name");
        DocumentReference modeRef = (DocumentReference) data.get("mode");
        if (modeRef != null) {
            firebaseUtil.getDocRefData(modeRef, new FirebaseUtil.DataCallback() {
                @Override
                public void onCallback(Map<String, Object> data) {
                    mode = new Mode(data);

                }
            });
        }
        DocumentReference playlistRef = (DocumentReference) data.get("playlist");
        if (playlistRef != null) {
            firebaseUtil.getDocRefData(playlistRef, new FirebaseUtil.DataCallback() {
                @Override
                public void onCallback(Map<String, Object> data) {
                    Log.d("Patient", "Playlist data: " + data);
                    playlist = new Playlist(data);
                }
            });
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
        Log.d("Patient", "Patient name: " + name);
        // playlist.printPlaylist();
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
    }

}