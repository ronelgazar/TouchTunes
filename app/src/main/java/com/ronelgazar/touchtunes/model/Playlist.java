package com.ronelgazar.touchtunes.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.model.Document;

import android.util.Log;
import androidx.annotation.NonNull;

public class Playlist {
    List<Song> playList = new ArrayList<>();

    public Playlist() {

    }

    public Playlist(List<Song> playList) {
        this.playList = playList;
    }

    public Playlist(DocumentReference documentReference) {
        if (documentReference != null) {
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            List<Map<String, Object>> songs = (List<Map<String, Object>>) document.get("songs");
                            for (Map<String, Object> songData : songs) {
                                String title = (String) songData.get("title");
                                String url = (String) songData.get("url");
                                // Process each song as needed
                                // create the new song object
                                Song song = new Song(title, url);
                                // add the song to the list
                                playList.add(song);
                            }
                        } else {
                            Log.d("Song", "No such document");
                        }
                    } else {
                        Log.d("Song", "get failed with ", task.getException());
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

}
