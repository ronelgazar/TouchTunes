package com.ronelgazar.touchtunes.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.ronelgazar.touchtunes.model.Patient;

public class PatientDataReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            Patient patient = extras.getParcelable("patient");
            Log.d("PatientDataReceiver", "Received patient data: " + patient);
            if (patient != null) {
                patient.getMode().setSharedPreference(context.getSharedPreferences("mode", Context.MODE_PRIVATE));
            } else {
                Log.e("PatientDataReceiver", "Received null patient data");
            }

            // Send a broadcast to the activity indicating that patient data has been received
            Intent patientDataIntent = new Intent("com.ronelgazar.touchtunes.PATIENT_DATA_RECEIVED");
            patientDataIntent.putExtra("patient", patient);
            context.sendBroadcast(patientDataIntent);
        }
    }
}
