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
        //Log.d("MainActivity", "onCreate: " + patient.toString());
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);
        playButton = findViewById(R.id.playButton);
        stopButton = findViewById(R.id.stopButton);
        settingsButton = findViewById(R.id.settingsButton);
        skipButton = findViewById(R.id.skipButton);
        previousButton = findViewById(R.id.prevButton);

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
            else {
               currentSong = patient.getPlaylist().getSong(0);
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