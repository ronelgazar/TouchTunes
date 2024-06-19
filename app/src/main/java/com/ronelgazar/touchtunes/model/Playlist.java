package com.ronelgazar.touchtunes.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.util.Base64;

public class Playlist implements Parcelable {
    private List<Song> playList;
    private static final String PREFS_NAME = "com.ronelgazar.touchtunes";
    private static final String PLAYLIST_KEY = "playlist";
    private static final long MAX_SIZE = 18 * 1024 * 1024; // 18 MB in bytes

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

    public Playlist() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(playList);
    }

    public Playlist(Playlist playlist) {
        playList = playlist.getPlayList();
    }

    public Playlist(List<Song> playList) {
        this.playList = playList;
    }

    public Playlist(Map<String, Object> playlist) {
        List<Song> songs = new ArrayList<>();
        List<Map<String, Object>> songsMapList = new ArrayList<>();
        if (playlist != null) {
            songsMapList = (List<Map<String, Object>>) playlist.get("songs");
            for (Map<String, Object> songMap : songsMapList) {
                String url = (String) songMap.get("URL");
                String name = (String) songMap.get("name");
                Song song = new Song(name, url);
                songs.add(song);
            }
        } else {
            Log.d("Playlist", "Provided playlist map is null");
        }
        this.playList = songs;
    }

    public List<Song> getPlayList() {
        return playList;
    }

    public void setPlayList(List<Song> playList) {
        this.playList = playList;
    }

    public void addSong(Song song) {
        this.playList.add(song);
    }

    public void removeSong(Song song) {
        this.playList.remove(song);
    }

    public void clearPlaylist() {
        this.playList.clear();
    }

    public Song getSong(int index) {
        return this.playList.get(index);
    }

    public int getPlaylistSize() {
        return playList.size();
    }

    public void setSong(int index, Song song) {
        playList.set(index, song);
    }

    public Song findSong(String title) {
        for (Song song : this.playList) {
            if (song.getTitle().equals(title)) {
                return song;
            }
        }
        return null;
    }

    public String skipSong(Song song) {
        if (!this.playList.isEmpty()) {
            int index = this.playList.indexOf(song);
            if (index != -1) {
                Song currentSong = this.playList.get(index);
                currentSong.stopSong();
                this.playList.remove(index);
                this.playList.add(currentSong);
                currentSong = this.playList.get(0);
                currentSong.playSong();
                return currentSong.getTitle();
            }
        } else {
            Log.d("Playlist", "Playlist is empty");
        }
        return "";
    }

    public String prevSong() {
        if (!this.playList.isEmpty()) {
            Song currentSong = this.playList.get(0);
            currentSong.stopSong();
            this.playList.remove(0);
            this.playList.add(currentSong);
            currentSong = playList.get(0);
            currentSong.playSong();
            return currentSong.getTitle();
        } else {
            Log.d("Playlist", "Playlist is empty");
        }
        return "";
    }

    public void printPlaylist() {
        for (Song song : this.playList) {
            Log.d("Song", "Song title: " + song.getTitle());
            Log.d("Song", "Song url: " + song.getUrl());
        }
    }

    public InputStream getStreamFromUrl(String mp3Url) throws Exception {
        URL url = new URL(mp3Url);
        URLConnection connection = url.openConnection();
        return connection.getInputStream();
    }

    public void savePlaylistToSharedPrefs(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        ExecutorService executor = Executors.newSingleThreadExecutor();

        for (int i = 0; i < playList.size(); i++) {
            final int finalI = i; // Create a final copy of i
            Song song = playList.get(i);
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        InputStream songStream = getStreamFromUrl(song.getUrl());
                        File songFile = new File(context.getFilesDir(), "song_" + finalI + ".mp3");
                        try (FileOutputStream fos = new FileOutputStream(songFile)) {
                            byte[] buffer = new byte[1024];
                            int nRead;
                            while ((nRead = songStream.read(buffer)) != -1) {
                                fos.write(buffer, 0, nRead);
                            }
                        }
                        editor.putString(PLAYLIST_KEY + "_song_file_" + finalI, songFile.getAbsolutePath());
                        editor.apply();
                    } catch (Exception e) {
                        Log.e("Playlist", "Error saving song data", e);
                    }
                }
            });
        }
    }



    public InputStream decodeSongData(Context context, int songIndex) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String songDataAsBase64 = sharedPreferences.getString(PLAYLIST_KEY + "_song_" + songIndex, null);
        String songFilePath = sharedPreferences.getString(PLAYLIST_KEY + "_song_file_" + songIndex, null);

        if (songDataAsBase64 != null) {
            byte[] songData = Base64.decode(songDataAsBase64, Base64.DEFAULT);
            return new ByteArrayInputStream(songData);
        } else if (songFilePath != null) {
            try {
                File songFile = new File(songFilePath);
                byte[] songData = new byte[(int) songFile.length()];
                try (FileInputStream fis = new FileInputStream(songFile)) {
                    fis.read(songData);
                }
                return new ByteArrayInputStream(songData);
            } catch (Exception e) {
                Log.e("Playlist", "Error reading song file", e);
            }
        }
        return null;
    }

}
