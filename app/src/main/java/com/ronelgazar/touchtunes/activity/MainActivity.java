package com.ronelgazar.touchtunes.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import com.google.android.material.navigation.NavigationView;
import com.ronelgazar.touchtunes.R;
import com.ronelgazar.touchtunes.fragment.SettingsFragment;
import com.ronelgazar.touchtunes.model.Patient;
import com.ronelgazar.touchtunes.model.Song;
import com.ronelgazar.touchtunes.services.BackgroundService;
import com.ronelgazar.touchtunes.util.DefualtData;
import com.ronelgazar.touchtunes.util.StreamSongTask;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageButton playButton, stopButton, skipButton, previousButton, settingsButton;
    private Patient patient;
    private BroadcastReceiver flashScreenReceiver;
    private Song currentSong;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private FragmentManager fragmentManager;
    private boolean isReceiverRegistered = false;
    private final BroadcastReceiver patientDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                patient = extras.getParcelable("patient");
                Log.d("MainActivity", "Received patient data: " + patient);
                if (patient != null) {
                    patient.getMode().setSharedPreference(getSharedPreferences("mode", MODE_PRIVATE));
                } else {
                    Log.e("MainActivity", "Received null patient data");
                }
            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        if (!isReceiverRegistered) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                registerReceiver(patientDataReceiver, new IntentFilter("com.ronelgazar.touchtunes.action.PATIENT_DATA"), Context.RECEIVER_NOT_EXPORTED);
            }
            isReceiverRegistered = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isReceiverRegistered) {
            unregisterReceiver(patientDataReceiver);
            isReceiverRegistered = false;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        patient = getIntent().getParcelableExtra("patient"); // this returns a not full object  - it has sub objects that are null

        fragmentManager = getSupportFragmentManager();
        toolbar = findViewById(R.id.toolbar);
        playButton = findViewById(R.id.playButton);
        stopButton = findViewById(R.id.stopButton);
        skipButton = findViewById(R.id.skipButton);
        previousButton = findViewById(R.id.prevButton);
        navigationView = findViewById(R.id.navigation_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        settingsButton = findViewById(R.id.settingsButton);

        if (patient == null) {
            Log.e("MainActivity", "Patient is null");
            patient = DefualtData.getDefaultPatient();
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            Log.d("MainActivity", "onNavigationItemSelected: " + item.getItemId());


            switch (item.getItemId()) {
                case R.id.nav_settings:
                    if (patient != null) {
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("mode", patient.getMode());
                            SettingsFragment settingsFragment = new SettingsFragment();
                            settingsFragment.setArguments(bundle);
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.fragment_container, settingsFragment);
                            transaction.addToBackStack(null);
                            transaction.commit();
                        } else {
                            Log.e("MainActivity", "Patient is null. Cannot create SettingsFragment.");
                        }
                        break;

                        case R.id.nav_logout:
                            // Logout
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                            break;

                        default:
                            throw new IllegalStateException("Unexpected value: " + item.getItemId());
                    }

                    // Close the drawer
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
            });

            playButton.setOnClickListener(v -> {
                if (currentSong != null) {
                    new StreamSongTask().execute(currentSong);
                    toolbar.setTitle(currentSong.getTitle());
                    // Print the mode
                    patient.getMode().printMode();
                    patient.getPlaylist().printPlaylist();
                    // Start the BackgroundService
                    Intent intent = new Intent(MainActivity.this, BackgroundService.class);
                    intent.putExtra("patient", patient);
                    startService(intent);
                } else if (patient.getPlaylist() != null && patient.getPlaylist().getPlaylistSize() > 0) {
                    currentSong = patient.getPlaylist().getSong(0);
                    new StreamSongTask().execute(currentSong);
                    toolbar.setTitle(currentSong.getTitle());
                    // Start the BackgroundService
                    Intent intent = new Intent(MainActivity.this, BackgroundService.class);
                    intent.putExtra("patient", patient);
                    startService(intent);
                } else {
                    Toast.makeText(MainActivity.this, "No songs in the playlist", Toast.LENGTH_SHORT).show();
                }
            });

            stopButton.setOnClickListener(v -> {
                if (patient !=null && currentSong != null) {
                    currentSong = patient.getPlaylist().getSong(0);
                    currentSong.stopSong();
                    toolbar.setTitle("");

                    // Stop the BackgroundService
                    Intent intent = new Intent(MainActivity.this, BackgroundService.class);
                    stopService(intent);
                }
            });

            skipButton.setOnClickListener(v -> {
                if (patient !=null && currentSong != null) {
                    patient.getPlaylist().printPlaylist();
                    String name = patient.getPlaylist().skipSong(currentSong);
                    toolbar.setTitle(name);
                }
            });

            previousButton.setOnClickListener(v -> {
                if (patient !=null && currentSong != null) {
                    String name = patient.getPlaylist().prevSong();
                    toolbar.setTitle(name);
                }


            });

            settingsButton.setOnClickListener(v -> {
                drawerLayout.openDrawer(GravityCompat.START);
            });


        flashScreenReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                WindowManager.LayoutParams params = getWindow().getAttributes();
                params.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
                getWindow().setAttributes(params);

                new Handler().postDelayed(() -> {
                    params.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
                    getWindow().setAttributes(params);
                }, 1000); // Flash for 1 second
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(flashScreenReceiver, new IntentFilter("com.ronelgazar.touchtunes.FLASH_SCREEN"), Context.RECEIVER_NOT_EXPORTED);
        }
    }




    }
