package com.ronelgazar.touchtunes.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.play.integrity.internal.c;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.ronelgazar.touchtunes.R;
import com.ronelgazar.touchtunes.fragment.SettingsFragment;
import com.ronelgazar.touchtunes.model.Patient;
import com.ronelgazar.touchtunes.model.Playlist;
import com.ronelgazar.touchtunes.model.Song;
import com.ronelgazar.touchtunes.util.FirebaseUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ImageButton playButton, stopButton, skipButton, previousButton,settingsButton;
    private Patient patient;
    private Song currentSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Log.d("MainActivity", "onCreate: " + bundle.toString());
        patient = FirebaseUtil.getParcelableCompat(bundle,"patient", Patient.class);

        Log.d("MainActivity", "onCreate: " + patient.toString());
      
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);
        playButton = findViewById(R.id.playButton);
        stopButton = findViewById(R.id.stopButton);
        settingsButton = findViewById(R.id.settingsButton);
        skipButton = findViewById(R.id.skipButton);
        previousButton = findViewById(R.id.prevButton);
         Song nextSong = new Song("bbbb", "https://firebasestorage.googleapis.com/v0/b/seniorproj-8f7de.appspot.com/o/01%20%D7%A8%D7%A6%D7%95%D7%A2%D7%94%201.mp3?alt=media&token=3ea70338-95ac-475d-9e34-a1137a1e51bb&_gl=1*jb9spm*_ga*MTM4NTU3OTIwMS4xNjk3MDI4OTE5*_ga_CW55HF8NVT*MTY5OTM5MDAzNS4xMDUuMC4xNjk5MzkwMDM1LjYwLjAuMA..");
         Song prev = new Song("CCCC", "https://firebasestorage.googleapis.com/v0/b/seniorproj-8f7de.appspot.com/o/06%20%D7%A8%D7%A6%D7%95%D7%A2%D7%94%206.mp3?alt=media&token=04e13043-6a09-4a40-b003-3feee83a605e&_gl=1*yz4e2p*_ga*MTM4NTU3OTIwMS4xNjk3MDI4OTE5*_ga_CW55HF8NVT*MTY5OTM5MDAzNS4xMDUuMS4xNjk5MzkwMjMxLjYwLjAuMA..");
         currentSong = new Song("aaaa", "https://firebasestorage.googleapis.com/v0/b/seniorproj-8f7de.appspot.com/o/01%20%D7%A8%D7%A6%D7%95%D7%A2%D7%94%201.mp3?alt=media&token=3ea70338-95ac-475d-9e34-a1137a1e51bb&_gl=1*1frugqh*_ga*MTM4NTU3OTIwMS4xNjk3MDI4OTE5*_ga_CW55HF8NVT*MTY5ODczNTUxNS45NS4xLjE2OTg3MzY5MjAuNDQuMC4w");

        List<Song> songs = new ArrayList<>();
        songs.add(nextSong);
        songs.add(prev);
        songs.add(currentSong);


         Playlist playlist = new Playlist(songs);
         patient.setPlaylist(playlist);
        fragmentManager = getSupportFragmentManager();

        // Get the button that opens the SettingsFragment.

        // Set the onClick listener for the button.
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a FragmentTransaction object.
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // Replace the current fragment with the SettingsFragment.
                fragmentTransaction.replace(R.id.fragment_container, new SettingsFragment());

                // Commit the FragmentTransaction.
                fragmentTransaction.commit();
            }
        });


        // Implement the song playback logic
        playButton.setOnClickListener(v -> {
            if (currentSong != null) {
                currentSong.playSong();
                toolbar.setTitle(currentSong.getTitle());
            }
        });

        stopButton.setOnClickListener(v -> {
            if (currentSong != null) {
                currentSong.stopSong();
                toolbar.setTitle("");

            }
        });

        // Implement skip and previous buttons logic here
        skipButton.setOnClickListener(v -> {
            patient.getPlaylist().printPlaylist();
            patient.getPlaylist().skipSong(currentSong);
            toolbar.setTitle(currentSong.getTitle());
        });

        previousButton.setOnClickListener(v -> {
            patient.getPlaylist().prevSong();
            toolbar.setTitle(currentSong.getTitle());

        });
    }
}