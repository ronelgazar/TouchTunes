package com.ronelgazar.touchtunes.util;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.ronelgazar.touchtunes.model.Patient;

import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class FirebaseUtil {

    public interface DataCallback {
        void onCallback(Map<String, Object> data);
    }

    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    public FirebaseFirestore getDb()
    {
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

    public CompletableFuture<Patient> getPatient(String uid) {
        DocumentReference docRef = db.collection("Patients").document(uid);
        getDocRefData(docRef);

        return getDocRefData(docRef).thenApply(data -> {
            Patient patient = new Patient(data);
            return patient;
        });


    }

    public void updatePatient(Patient patient) {
        DocumentReference docRef = db.collection("Patients").document(patient.getUid());
        docRef.set(patient, SetOptions.merge());
    }
    public CompletableFuture<Map<String, Object>> getDocRefData(DocumentReference docRef) {
        CompletableFuture<Map<String, Object>> completableFuture = new CompletableFuture<>();

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Map<String, Object> data = task.getResult().getData();
                    Log.d("FirebaseUtil", "DocumentSnapshot data: " + data);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        completableFuture.complete(data);
                    }
                } else {
                    Log.d("FirebaseUtil", "Error getting document", task.getException());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        completableFuture.completeExceptionally(task.getException());
                    }
                }
            }
        });

        return completableFuture;
    }


    


}