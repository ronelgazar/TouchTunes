package com.ronelgazar.touchtunes.model;

import java.util.List;

public class Patient {

    private boolean isActive;
    private Mode mode;
    private String name;
    private List<Song> playlist;

    public Patient() {
    }

    public Patient(boolean isActive, Mode mode, String name, List<Song> playlist) {
        this.isActive = isActive;
        this.mode = mode;
        this.name = name;
        this.playlist = playlist;
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

    public List<Song> getPlaylist() {
        return playlist;
    }

    public void setPlaylist(List<Song> playlist) {
        this.playlist = playlist;
    }
}