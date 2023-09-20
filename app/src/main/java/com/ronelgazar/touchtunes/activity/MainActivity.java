package com.ronelgazar.touchtunes.activity;

import java.io.IOException;
import java.util.List;

import com.google.android.material.navigation.NavigationView;
import com.ronelgazar.touchtunes.adapter.SongsAdapter;
import com.ronelgazar.touchtunes.model.Song;
import com.ronelgazar.touchtunes.util.FirebaseUtil;

import com.ronelgazar.touchtunes.R;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private GridView songsGridView;

    private MediaPlayer mediaPlayer;
    private List<Song> songs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        GridView songsGridView = findViewById(R.id.songs_grid_view);

        // Implement the navigation menu logic
        NavigationView.OnNavigationItemSelectedListener listener = new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle the user interaction with the navigation menu item
                return true;
            }
        };
        navigationView.setNavigationItemSelectedListener(listener);


        // Implement the songs grid view logic
        songs = (List<Song>) FirebaseUtil.getDatabaseReference("songs").get();
        SongsAdapter songsAdapter = new SongsAdapter(this, songs);
        songsGridView.setAdapter(songsAdapter);

        // Implement the song playback logic
        songsGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Song song = songs.get(position);

                if (mediaPlayer != null) {
                    mediaPlayer.release();
                }

                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(song.getUrl());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                mediaPlayer.start();
            }
        });
    }
}