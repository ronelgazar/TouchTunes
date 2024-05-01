package com.ronelgazar.touchtunes.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Playlist implements Parcelable {
    private List<Song> playList;

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

}
