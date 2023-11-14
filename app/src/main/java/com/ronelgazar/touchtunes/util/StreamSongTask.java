package com.ronelgazar.touchtunes.util;

import com.ronelgazar.touchtunes.model.Song;
import android.os.AsyncTask;
import android.util.Log;

public class StreamSongTask extends AsyncTask<Song, Void, Void> {
    @Override
    protected Void doInBackground(Song... songs) {
        if (songs != null && songs.length > 0) {
            Song song = songs[0];
            try {
                song.playSong(); // This will stream the song
            } catch (Exception e) {
                Log.e("StreamSongTask", "Error streaming song", e);
            }
        }
        return null;
    }
}