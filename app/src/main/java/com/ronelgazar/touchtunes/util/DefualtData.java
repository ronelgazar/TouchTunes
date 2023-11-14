package com.ronelgazar.touchtunes.util;

import com.ronelgazar.touchtunes.model.Mode;
import com.ronelgazar.touchtunes.model.Patient;
import com.ronelgazar.touchtunes.model.Playlist;
import com.ronelgazar.touchtunes.model.Song;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefualtData {

    public static Playlist getDefaultPlaylist()
    {
        Song nextSong = new Song("bbbb", "https://firebasestorage.googleapis.com/v0/b/seniorproj-8f7de.appspot.com/o/01%20%D7%A8%D7%A6%D7%95%D7%A2%D7%94%201.mp3?alt=media&token=3ea70338-95ac-475d-9e34-a1137a1e51bb&_gl=1*jb9spm*_ga*MTM4NTU3OTIwMS4xNjk3MDI4OTE5*_ga_CW55HF8NVT*MTY5OTM5MDAzNS4xMDUuMC4xNjk5MzkwMDM1LjYwLjAuMA..");
        Song prev = new Song("CCCC", "https://firebasestorage.googleapis.com/v0/b/seniorproj-8f7de.appspot.com/o/06%20%D7%A8%D7%A6%D7%95%D7%A2%D7%94%206.mp3?alt=media&token=04e13043-6a09-4a40-b003-3feee83a605e&_gl=1*yz4e2p*_ga*MTM4NTU3OTIwMS4xNjk3MDI4OTE5*_ga_CW55HF8NVT*MTY5OTM5MDAzNS4xMDUuMS4xNjk5MzkwMjMxLjYwLjAuMA..");
        Song currentSong = new Song("aaaa", "https://firebasestorage.googleapis.com/v0/b/seniorproj-8f7de.appspot.com/o/01%20%D7%A8%D7%A6%D7%95%D7%A2%D7%94%201.mp3?alt=media&token=3ea70338-95ac-475d-9e34-a1137a1e51bb&_gl=1*1frugqh*_ga*MTM4NTU3OTIwMS4xNjk3MDI4OTE5*_ga_CW55HF8NVT*MTY5ODczNTUxNS45NS4xLjE2OTg3MzY5MjAuNDQuMC4w");
        List<Song> songs = new ArrayList<>();
        songs.add(nextSong);
        songs.add(prev);
        songs.add(currentSong);
        Playlist playlist = new Playlist(songs);
        return playlist;

    }

    public static Mode getDefaultMode()
    {
        Map<String, Object> settings = new HashMap<>();
        settings.put("interaction_type", "slow");
        settings.put("vibration", 5);
        settings.put("interaction_duration", 5);
        settings.put("lighting", 5);
        settings.put("session_duration", "53:00");
        settings.put("sound_interaction", true);
        Mode mode1 = new Mode("mode1", settings);
        return mode1;

    }


    public static Patient getDefaultPatient()
    {
        Patient patient = new Patient();
        patient.setActive(true);
        patient.setName("ronel");
        patient.setMode(getDefaultMode());
        patient.setPlaylist(getDefaultPlaylist());
        return patient;
    }


}
