package com.ronelgazar.touchtunes.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
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

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Vibrator vibrator;
    private ImageButton playButton, stopButton, skipButton, previousButton, settingsButton;

    private Patient patient;

    private MediaPlayer buttonSoundPlayer;

    private Song currentSong;

    private NavigationView navigationView;

    private DrawerLayout drawerLayout;

    private FragmentManager fragmentManager;

    private boolean isReceiverRegistered = false;

    private CountDownTimer sessionTimer, interactionTimer;

    private boolean isInteractionActive = false;

    private boolean isMusicPlaying = false;

    private long sessionDurationInMillis, interactionDurationInMillis;
    private final Handler flashHandler = new Handler();
    private final Runnable flashRunnable = new Runnable() {
        @Override
        public void run() {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
            getWindow().setAttributes(params);
            new Handler().postDelayed(() -> {
                params.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_OFF;
                getWindow().setAttributes(params);
            }, 500L * ((int)(Double.parseDouble(patient.getMode().getLightingSettings())))); // Flash for 100 milliseconds
            // Schedule the next flash
            scheduleNextFlash();
        }
    };

    private final Handler settingsUpdateHandler = new Handler();
    private final Runnable settingsUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            updatePatientSettings();
            settingsUpdateHandler.postDelayed(this, 1000); // Update every second
        }
    };

    private void scheduleNextFlash() {
        int delay = (int) (Math.random() * 5000) + 500; // Random delay between 500 and 5500 milliseconds
        flashHandler.postDelayed(flashRunnable, delay);
    }

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
        registerReceivers();
        scheduleNextFlash();
        settingsUpdateHandler.post(settingsUpdateRunnable); // Start updating settings
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isReceiverRegistered) {
            unregisterReceivers();
        }
        flashHandler.removeCallbacks(flashRunnable);
        settingsUpdateHandler.removeCallbacks(settingsUpdateRunnable); // Stop updating settings
    }

    private void registerReceivers() {
        IntentFilter patientDataFilter = new IntentFilter("com.ronelgazar.touchtunes.action.PATIENT_DATA");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(patientDataReceiver, patientDataFilter, Context.RECEIVER_NOT_EXPORTED);
        }
        isReceiverRegistered = true;
    }

    private void unregisterReceivers() {
        if (isReceiverRegistered) {
            unregisterReceiver(patientDataReceiver);
            isReceiverRegistered = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        patient = getIntent().getParcelableExtra("patient");
        buttonSoundPlayer = MediaPlayer.create(this, R.raw.buttonsfx);
        fragmentManager = getSupportFragmentManager();
        toolbar = findViewById(R.id.toolbar);
        playButton = findViewById(R.id.playButton);
        stopButton = findViewById(R.id.stopButton);
        skipButton = findViewById(R.id.skipButton);
        previousButton = findViewById(R.id.prevButton);
        settingsButton = findViewById(R.id.settingsButton);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (patient == null) {
            Log.e("MainActivity", "Patient is null");
            patient = DefualtData.getDefaultPatient();
        }

        Intent bgintent = new Intent(MainActivity.this, BackgroundService.class);
        bgintent.putExtra("patient", patient);
        startService(bgintent);

        patient.getMode().setSharedPreference(PreferenceManager.getDefaultSharedPreferences(this));
        patient.getPlaylist().savePlaylistToSharedPrefs(this);

        // Start vibrating the screen every 5 seconds
        new Handler().postDelayed(() -> {
            if (vibrator != null && vibrator.hasVibrator()) {
                long[] vibrationPattern = {0, 500L * ((int)(Double.parseDouble(patient.getMode().getVibrationIntensity())))/4, 500L * ((int)(Double.parseDouble(patient.getMode().getVibrationIntensity())))/2, 500L * ((int)(Double.parseDouble(patient.getMode().getVibrationIntensity())))/4}; // Pattern: 0ms off, 500ms on, 100ms off, 500ms on
                vibrator.vibrate(vibrationPattern, 0); // Repeat indefinitely
            }
        }, 5000); // Start after 5 seconds

        navigationView.setNavigationItemSelectedListener(item -> {
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
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + item.getItemId());
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        playButton.setOnClickListener(v -> {
            playButton.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            if (patient.getMode().getSoundInteraction()) {
                buttonSoundPlayer.start();
            }
            if (currentSong != null) {
                new StreamSongTask().execute(currentSong);
                toolbar.setTitle(currentSong.getTitle());
                patient.getMode().printMode();
                patient.getPlaylist().printPlaylist();
                Intent intent = new Intent(MainActivity.this, BackgroundService.class);
                intent.putExtra("patient", patient);
                startService(intent);
            } else if (patient.getPlaylist() != null && patient.getPlaylist().getPlaylistSize() > 0) {
                currentSong = patient.getPlaylist().getSong(0);
                new StreamSongTask().execute(currentSong);
                toolbar.setTitle(currentSong.getTitle());
                Intent intent = new Intent(MainActivity.this, BackgroundService.class);
                intent.putExtra("patient", patient);
                intent.putExtra("song", currentSong);
                startService(intent);
            } else {
                Toast.makeText(MainActivity.this, "No songs in the playlist", Toast.LENGTH_SHORT).show();
            }
            startSessionTimer();
            resetInteractionTimer();
        });

        stopButton.setOnClickListener(v -> {
            stopButton.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            if (patient.getMode().getSoundInteraction()) {
                buttonSoundPlayer.start();
            }
            if (patient != null && currentSong != null) {
                currentSong.stopSong();
                toolbar.setTitle("");
                Intent intent = new Intent(MainActivity.this, BackgroundService.class);
                stopService(intent);
            }
            stopSessionTimer();
            stopInteractionTimer();
        });

        skipButton.setOnClickListener(v -> {
            skipButton.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            if (patient.getMode().getSoundInteraction()) {
                buttonSoundPlayer.start();
            }
            if (patient != null && currentSong != null) {
                patient.getPlaylist().printPlaylist();
                String name = patient.getPlaylist().skipSong(currentSong);
                toolbar.setTitle(name);
            }
            resetInteractionTimer();
        });

        previousButton.setOnClickListener(v -> {
            previousButton.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            if (patient.getMode().getSoundInteraction()) {
                buttonSoundPlayer.start();
            }
            if (patient != null && currentSong != null) {
                String name = patient.getPlaylist().prevSong();
                toolbar.setTitle(name);
            }
            resetInteractionTimer();
        });

        settingsButton.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });
    }

    private void startSessionTimer() {
        String sessionDuration = patient.getMode().getSessionDurationInMinutes();
        sessionDurationInMillis = Long.parseLong(sessionDuration) * 60 * 1000; // Convert minutes to milliseconds
        sessionTimer = new CountDownTimer(sessionDurationInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Update UI or handle tick events if needed
            }
            @Override
            public void onFinish() {
                if (isMusicPlaying) {
                    stopMusic();
                }
            }
        }.start();
    }

    private void stopSessionTimer() {
        if (sessionTimer != null) {
            sessionTimer.cancel();
        }
    }

    private void startInteractionTimer() {
        String interactionDuration = patient.getMode().getInteractionDuration();
        interactionDurationInMillis = (long) (Double.parseDouble(interactionDuration) * 1000); // Convert seconds to milliseconds
        interactionTimer = new CountDownTimer(interactionDurationInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Update UI or handle tick events if needed
            }
            @Override
            public void onFinish() {
                if (isMusicPlaying) {
                    stopMusic();
                }
            }
        }.start();
    }

    private void resetInteractionTimer() {
        if (interactionTimer != null) {
            interactionTimer.cancel();
        }
        startInteractionTimer();
    }

    private void stopInteractionTimer() {
        if (interactionTimer != null) {
            interactionTimer.cancel();
        }
    }

    private void stopMusic() {
        if (currentSong != null) {
            currentSong.stopSong();
            toolbar.setTitle("");
        }
        stopSessionTimer();
        stopInteractionTimer();
        isMusicPlaying = false;
    }

    private void updatePatientSettings() {
        if (patient != null) {
            patient.getMode().setSharedPreference(PreferenceManager.getDefaultSharedPreferences(this));
            patient.getPlaylist().savePlaylistToSharedPrefs(this);
            // Update any other patient settings as needed
            Log.d("MainActivity", "Patient settings updated");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isReceiverRegistered) {
            unregisterReceivers();
        }
        if (buttonSoundPlayer != null) {
            buttonSoundPlayer.release();
            buttonSoundPlayer = null;
        }
        flashHandler.removeCallbacks(flashRunnable);
        settingsUpdateHandler.removeCallbacks(settingsUpdateRunnable); // Stop updating settings
    }

    private long getSessionDurationMillis() {
        String mode = String.valueOf(patient.getMode().getInteractionType());
        switch (mode) {
            case "fast":
                return TimeUnit.MINUTES.toMillis(10);
            case "slow":
                return TimeUnit.MINUTES.toMillis(30);
            default:
                return TimeUnit.MINUTES.toMillis(20);
        }
    }

    private long getInteractionDurationMillis() {
        return TimeUnit.MINUTES.toMillis(5);
    }
}
