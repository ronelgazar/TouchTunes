package com.ronelgazar.touchtunes.model;

import android.media.MediaPlayer;
import android.util.Log;
import androidx.annotation.NonNull;
import java.io.IOException;
public class Song {

    private String title;
    private String url;
    private MediaPlayer mediaPlayer;


    public Song() {
    }

    public Song(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void playSong() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            Log.e("Song", "Failed to play song", e);
            // Toast.makeText(MainActivity.this, "Failed to play song",
            // Toast.LENGTH_SHORT).show();
        }
    }

    public void stopSong() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    
}