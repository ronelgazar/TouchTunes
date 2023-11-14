package com.ronelgazar.touchtunes.services;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.MemoryCacheSettings;
import com.google.firebase.firestore.PersistentCacheSettings;
import com.google.firebase.firestore.SetOptions;
import com.ronelgazar.touchtunes.model.Patient;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcelable;
import android.util.Log;
import java.util.Map;

public class FirebaseService extends Service {
    private FirebaseFirestore db;
    private FirebaseFirestoreSettings settings;

    public interface DataCallback {
        void onCallback(Map<String, Object> data);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        db = FirebaseFirestore.getInstance();
        settings = new FirebaseFirestoreSettings.Builder(db.getFirestoreSettings())
                .setLocalCacheSettings(MemoryCacheSettings.newBuilder().build())
                .setLocalCacheSettings(PersistentCacheSettings.newBuilder().build())
                .build();

        db.setFirestoreSettings(settings);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if ("com.ronelgazar.touchtunes.action.CREATE_PATIENT".equals(action)) {
            Patient patient = getParcelableCompat(intent.getExtras(), "patient", Patient.class);
            if (patient != null) {
                createPatient(patient);
            }
        } else if ("com.ronelgazar.touchtunes.action.GET_PATIENT".equals(action)) {
            String userId = intent.getStringExtra("userId");
            if (userId != null) {
                getPatient(userId);
            }
        }
        return START_STICKY;
    }
    
    public void getPatient(String userId) {
        DocumentReference docRef = db.collection("Patients").document(userId);
        getDocRefData(docRef, data -> {
            if (data != null) {
                Patient patient = new Patient(data);
                Intent intent = new Intent("com.ronelgazar.touchtunes.action.PATIENT_DATA");
                intent.putExtra("patient", patient);
                sendBroadcast(intent);
            }
        });
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public FirebaseFirestore getDb() {
        return db;
    }

    public static <T extends Parcelable> T getParcelableCompat(Bundle bundle, String key, Class<T> clazz) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return bundle.getParcelable(key, clazz);
        } else {
            return (T) bundle.getParcelable(key);
        }
    }

    public void createPatient(Patient patient) {
        db.collection("Patients").document(patient.getUid()).set(patient);
    }


    public void updatePatient(Patient patient) {
        DocumentReference docRef = db.collection("Patients").document(patient.getUid());
        docRef.set(patient, SetOptions.merge());
    }

    public void getDocRefData(DocumentReference docRef, DataCallback callback) {
        Handler handler = new Handler(Looper.getMainLooper());

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Map<String, Object> data = task.getResult().getData();
                    handler.post(() -> callback.onCallback(data));
                } else {
                    Log.d("FirebaseUtil", "Error getting document", task.getException());
                    callback.onCallback(null);
                }
            }
        });
    }
}