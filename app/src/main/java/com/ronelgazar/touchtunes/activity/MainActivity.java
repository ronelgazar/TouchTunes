package com.ronelgazar.touchtunes.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.play.integrity.internal.c;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.ronelgazar.touchtunes.R;
import com.ronelgazar.touchtunes.model.Patient;
import com.ronelgazar.touchtunes.model.Song;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ImageButton playButton, stopButton, skipButton, previousButton;
    private Patient patient;
    private Song currentSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        patient = intent.getParcelableExtra("patient");
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);
        playButton = findViewById(R.id.playButton);
        stopButton = findViewById(R.id.stopButton);
        skipButton = findViewById(R.id.skipButton);
        previousButton = findViewById(R.id.prevButton);

        // wait until current song is fetched and show a loading message
        while (patient.getPlaylist().getSong(0) == null) {
            Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show();
        }

        // set the current song
        currentSong = patient.getPlaylist().getSong(0);





         //currentSong = new Song("aaaa", "https://firebasestorage.googleapis.com/v0/b/seniorproj-8f7de.appspot.com/o/01%20%D7%A8%D7%A6%D7%95%D7%A2%D7%94%201.mp3?alt=media&token=3ea70338-95ac-475d-9e34-a1137a1e51bb&_gl=1*1frugqh*_ga*MTM4NTU3OTIwMS4xNjk3MDI4OTE5*_ga_CW55HF8NVT*MTY5ODczNTUxNS45NS4xLjE2OTg3MzY5MjAuNDQuMC4w");
        //currentSong = patient.getPlaylist().getSong(0);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle the user interaction with the navigation menu item
                return true;
            }   
        });

        // Implement the song playback logic
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentSong != null) {
                    currentSong.playSong();
                    toolbar.setTitle(currentSong.getTitle());
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentSong != null) {
                    currentSong.stopSong();
                    toolbar.setTitle("");
                }
            }
        });

        // Implement skip and previous buttons logic here
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                patient.getPlaylist().skipSong();
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                patient.getPlaylist().prevSong();
            }
        });
    }
}