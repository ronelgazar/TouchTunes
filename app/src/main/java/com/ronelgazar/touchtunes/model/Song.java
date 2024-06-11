package com.ronelgazar.touchtunes.model;

import android.media.MediaPlayer;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import java.io.IOException;

public class Song implements Parcelable  {

    private String title;
    private String url;
    private MediaPlayer mediaPlayer;
    public Song() {
        this.mediaPlayer = new MediaPlayer();
    }
    protected Song(Parcel in) {
        title = in.readString();
        url = in.readString();
        
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(url);
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

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;

    }

    public void playSong() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            Log.e("Song", "Failed to play song", e);
        }
    }

    public void stopSong() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void pauseSong() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    public void resumeSong() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    public void printSong() {
        Log.d("Song", "Song title: " + title);
        Log.d("Song", "Song url: " + url);
    }


}