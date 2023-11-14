package com.ronelgazar.touchtunes.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.navigation.NavigationView;
import com.ronelgazar.touchtunes.R;
import com.ronelgazar.touchtunes.fragment.SettingsFragment;
import com.ronelgazar.touchtunes.model.Patient;
import com.ronelgazar.touchtunes.model.Song;
import com.ronelgazar.touchtunes.services.FirebaseService;
import com.ronelgazar.touchtunes.util.DefualtData;
import com.ronelgazar.touchtunes.util.StreamSongTask;

public class MainActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ImageButton playButton, stopButton, skipButton, previousButton, settingsButton;
    private Patient patient;
    private Song currentSong;
    private BroadcastReceiver patientDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                patient = FirebaseService.getParcelableCompat(extras, "patient", Patient.class);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("com.ronelgazar.touchtunes.action.PATIENT_DATA");
        registerReceiver(patientDataReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(patientDataReceiver);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialize views
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);
        playButton = findViewById(R.id.playButton);
        stopButton = findViewById(R.id.stopButton);
        settingsButton = findViewById(R.id.settingsButton);
        skipButton = findViewById(R.id.skipButton);
        previousButton = findViewById(R.id.prevButton);
        // Setup fragment manager
        fragmentManager = getSupportFragmentManager();
        // Hide progress bar
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        if (patient == null) {
            patient = DefualtData.getDefaultPatient();
        }

        // Setup settings button
        settingsButton.setOnClickListener(view -> {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, new SettingsFragment());
            fragmentTransaction.commit();
        });
        
        playButton.setOnClickListener(v -> {
            if (currentSong != null) {
                new StreamSongTask().execute(currentSong);
                toolbar.setTitle(currentSong.getTitle());
            } else {
                if (patient.getPlaylist().getPlaylistSize() > 0) {
                    currentSong = patient.getPlaylist().getSong(0);
                    new StreamSongTask().execute(currentSong);
                    toolbar.setTitle(currentSong.getTitle());
                } else {
                    Toast.makeText(MainActivity.this, "No songs in the playlist", Toast.LENGTH_SHORT).show();
                }
            }
        });

        skipButton.setOnClickListener(v -> {
            patient.getPlaylist().printPlaylist();
            patient.getPlaylist().skipSong(currentSong);
            toolbar.setTitle(currentSong.getTitle());
        });

        previousButton.setOnClickListener(v -> {
            patient.getPlaylist().prevSong();
            toolbar.setTitle(currentSong.getTitle());

        });

        stopButton.setOnClickListener(v -> {
            currentSong = patient.getPlaylist().getSong(0);
            currentSong.stopSong();
            toolbar.setTitle("");
        });
    }
}