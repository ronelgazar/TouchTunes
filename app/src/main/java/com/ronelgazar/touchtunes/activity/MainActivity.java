package com.ronelgazar.touchtunes.activity;

import android.content.Intent;
import android.os.Bundle;
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
        this.patient = (Patient) intent.getSerializableExtra("patient");
        this.patient.printPatient();
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);
        playButton = findViewById(R.id.playButton);
        stopButton = findViewById(R.id.stopButton);
        skipButton = findViewById(R.id.skipButton);
        previousButton = findViewById(R.id.prevButton);
        currentSong = patient.getPlaylist().getPlayList().get(0);
        // Implement the navigation menu logic
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
                // Implement logic to skip to the next song
                // Update the toolbar title with the new song title
                
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement logic to go to the previous song
                // Update the toolbar title with the new song title
            }
        });
    }
}