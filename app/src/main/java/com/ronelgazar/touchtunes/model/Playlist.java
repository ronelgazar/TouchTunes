package com.ronelgazar.touchtunes.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.integrity.e;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.model.Document;
import com.ronelgazar.touchtunes.util.FirebaseUtil;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;

public class Playlist implements Parcelable {
    List<Song> playList = new ArrayList<>();


    protected Playlist(Parcel in) {
        playList = in.createTypedArrayList(Song.CREATOR);
    }

    public static final Creator<Playlist> CREATOR = new Creator<Playlist>() {
        @Override
        public Playlist createFromParcel(Parcel in) {
            return new Playlist(in);
        }

        @Override
        public Playlist[] newArray(int size) {
            return new Playlist[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(playList);
    }

    public Playlist() {

    }

    public Playlist(List<Song> playList) {
        this.playList = playList;
    }

    public Playlist(Map<String, Object> playlist) {
        List<Map<String, Object>> songsMapList = (List<Map<String, Object>>) playlist.get("songs");
        List<Song> songs = new ArrayList<>();
        for (Map<String, Object> songMap : songsMapList) {
            String url = (String) songMap.get("URL");
            String name = (String) songMap.get("name");
            Song song = new Song(name, url);
            songs.add(song);
            song.printSong();
        }

        this.playList = songs;

    }

    public Playlist(DocumentReference documentReference, FirebaseUtil.DataCallback callback) {
        if (documentReference != null) {
            FirebaseUtil firebaseUtil = new FirebaseUtil();
            firebaseUtil.getDocRefData(documentReference, new FirebaseUtil.DataCallback() {
                @Override
                public void onCallback(Map<String, Object> data) {
                    if (data != null) {
                        List<Map<String, Object>> songs = (List<Map<String, Object>>) data.get("songs");
                        for (Map<String, Object> songData : songs) {
                            String title = (String) songData.get("name");
                            String url = (String) songData.get("url");
                            Song song = new Song(title, url);
                            // add the song to the list
                            playList.add(song);
                        }
                        callback.onCallback(data);
                    } else {
                        Log.d("Song", "No such document");
                    }
                }
            });
        }
    }

    public List<Song> getPlayList() {
        return playList;
    }

    public void setPlayList(List<Song> playList) {
        this.playList = playList;
    }

    public void addSong(Song song) {
        playList.add(song);
    }

    public void removeSong(Song song) {
        playList.remove(song);
    }

    public void clearPlaylist() {
        playList.clear();
    }

    public Song getSong(int index) {
        return playList.get(index);
    }

    public int getPlaylistSize() {
        return playList.size();
    }

    public void setSong(int index, Song song) {
        playList.set(index, song);
    }

    public Song findSong(String title) {
        for (Song song : playList) {
            if (song.getTitle().equals(title)) {
                return song;
            }
        }
        return null;
    }

    public void skipSong() {
        if (playList.size() > 0) {
            Song currentSong = playList.get(0);
            currentSong.stopSong();
            playList.remove(0);
            currentSong = playList.get(0);
            currentSong.playSong();
        } else {
            Log.d("Playlist", "Playlist is empty");
        }

    }

    public void prevSong() {
        if (playList.size() > 0) {
            Song currentSong = playList.get(0);
            currentSong.stopSong();
            playList.remove(0);
            playList.add(currentSong);
            currentSong = playList.get(0);
            currentSong.playSong();
        } else {
            Log.d("Playlist", "Playlist is empty");
        }

    }

    public void printPlaylist() {
        for (Song song : playList) {
            Log.d("Patient", "Song title: " + song.getTitle());
            Log.d("Patient", "Song url: " + song.getUrl());
        }
    }

}
