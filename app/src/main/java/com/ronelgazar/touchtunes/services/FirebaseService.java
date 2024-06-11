package com.ronelgazar.touchtunes.services;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcelable;
import android.util.Log;

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

import java.util.Map;

public class FirebaseService extends Service {

    private static final String TAG = "FirebaseService";
    private static final String PATIENTS_COLLECTION = "Patients";
    private static FirebaseFirestore db;
    private static FirebaseFirestoreSettings settings;
    private DataCallback callback;

    public interface DataCallback {
        void onCallback(Map<String, Object> data);
    }

    public class LocalBinder extends android.os.Binder {
        public FirebaseService getService() {
            return FirebaseService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initializeFirestore();
    }

    private void initializeFirestore() {
        if (db == null) {
            settings = new FirebaseFirestoreSettings.Builder()
                    // Set persistence and cache settings (optional)
                    .build();
            db = FirebaseFirestore.getInstance();
            db.setFirestoreSettings(settings);
        }
        else{
            db = FirebaseFirestore.getInstance();
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }

    public static <T extends Parcelable> T getParcelableCompat(Bundle bundle, String key, Class<T> clazz) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return bundle.getParcelable(key, clazz);
        } else {
            return (T) bundle.getParcelable(key);
        }
    }

    public void getPatient(String userId, DataCallback callback) {
        this.callback = callback; // Store callback for later use
        db.collection(PATIENTS_COLLECTION).document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            callback.onCallback(document.getData());
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                        if (callback != null) {
                            callback.onCallback(null);
                        }
                    }
                    this.callback = null; // Reset callback after use
                });
    }

    public void createPatient(Patient patient) {
        db.collection(PATIENTS_COLLECTION).document(patient.getUid()).set(patient);
        Log.d(TAG, "createPatient: " + patient.getUid() + " " + patient.getName() + " " + patient.isActive());
    }

    public void updatePatient(Patient patient) {
        DocumentReference docRef = db.collection(PATIENTS_COLLECTION).document(patient.getUid());
        docRef.set(patient, SetOptions.merge());
    }

    private void getDocRefData(DocumentReference docRef) {
        Handler handler = new Handler(Looper.getMainLooper());

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Map<String, Object> data = task.getResult().getData();
                handler.post(() -> {
                    if (callback != null) {
                        callback.onCallback(data);
                        callback = null; // Reset callback after use
                    }
                });
            } else {
                Log.e(TAG, "Error getting document", task.getException());
                if (callback != null) {
                    callback.onCallback(null);
                    callback = null; // Reset callback after use
                }
            }
        });
    }

    public void getDocRefData(DocumentReference docRef, DataCallback callback) {
        Log.d(TAG, "Getting data for document: " + docRef.getPath());

        Handler handler = new Handler(Looper.getMainLooper());

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Map<String, Object> data = task.getResult().getData();
                Log.d(TAG, "Data retrieved from Firebase: " + data);

                handler.post(() -> callback.onCallback(data));
            } else {
                Log.d(TAG, "Error getting document", task.getException());
                Log.e(TAG, "Failed to get data for document: " + docRef.getPath(), task.getException());

                callback.onCallback(null);
            }
        });
    }
}
