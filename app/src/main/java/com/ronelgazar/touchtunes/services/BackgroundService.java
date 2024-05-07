package com.ronelgazar.touchtunes.services;
import com.ronelgazar.touchtunes.model.Patient;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Vibrator;
import android.view.WindowManager;
import android.os.Handler;

import java.util.Objects;
import java.util.Random;

public class BackgroundService extends Service {

    private Vibrator vibrator;
    private WindowManager.LayoutParams params;
    private Handler handler;
    private Random random;

    @Override
    public void onCreate() {
        super.onCreate();
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        params = new WindowManager.LayoutParams();
        handler = new Handler();
        random = new Random();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Get the patient data from the intent
        Patient patient = intent.getParcelableExtra("patient");
        long[] pattern = {0, 1000, 1000};
        vibrator.vibrate(pattern,Integer.parseInt(patient.getMode().getVibrationIntensity()));

        // Occasionally flash the screen
        if (random.nextInt(10) < 2) { // 20% chance to flash the screen
            flashScreen();
        }

        return START_STICKY;
    }

    private void flashScreen() {
        Intent intent = new Intent("com.ronelgazar.touchtunes.FLASH_SCREEN");
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        vibrator.cancel();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}