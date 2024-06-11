package com.ronelgazar.touchtunes.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class FlashScreenReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Send a broadcast to the activity indicating that the screen should flash
        Intent flashIntent = new Intent("com.ronelgazar.touchtunes.FLASH_SCREEN");
        context.sendBroadcast(flashIntent);
    }
}
