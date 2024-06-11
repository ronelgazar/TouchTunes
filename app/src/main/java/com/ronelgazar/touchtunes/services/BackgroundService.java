package com.ronelgazar.touchtunes.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.view.WindowManager;

import com.ronelgazar.touchtunes.model.Patient;

public class BackgroundService extends Service {

    private Patient patient;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("BackgroundService", "Service created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        patient = intent.getParcelableExtra("patient");
        if (patient != null) {
            Log.d("BackgroundService", "Patient data received");
            applySettings();
        } else {
            Log.e("BackgroundService", "Patient data is null");
        }
        return START_STICKY;
    }

    private void applySettings() {
        // Apply lighting settings (flashing the screen)
        if (patient.getMode().getLightingSettings() != "0") {
            Intent flashScreenIntent = new Intent("com.ronelgazar.touchtunes.FLASH_SCREEN");
            sendBroadcast(flashScreenIntent);
        }

        // Apply vibration settings
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            int vibrationIntensity = Integer.parseInt(patient.getMode().getVibrationIntensity());
            vibrator.vibrate(vibrationIntensity);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("BackgroundService", "Service destroyed");
    }
}
