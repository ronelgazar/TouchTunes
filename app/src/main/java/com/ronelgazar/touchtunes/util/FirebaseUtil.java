package com.ronelgazar.touchtunes.util;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.ronelgazar.touchtunes.activity.MainActivity;
import com.ronelgazar.touchtunes.model.Mode;
import com.ronelgazar.touchtunes.model.Patient;
import com.ronelgazar.touchtunes.model.Song;

import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FirebaseUtil {

    public interface DataCallback {
        void onCallback(Map<String, Object> data);
    }

    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    public FirebaseFirestore getDb()
    {
        return db;
    }


    public void createPatient(Patient patient) {
        db.collection("Patients").document(patient.getUid()).set(patient);
    }

    public void getPatient(String uid, DataCallback callback) {
        DocumentReference docRef = db.collection("Patients").document(uid);
        getDocRefData(docRef, callback);
    }

    public void updatePatient(Patient patient) {
        DocumentReference docRef = db.collection("Patients").document(patient.getUid());
        docRef.set(patient, SetOptions.merge());
    }
    public void getDocRefData(DocumentReference docRef, DataCallback callback) {
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Map<String, Object> data = task.getResult().getData();
                    Log.d("FirebaseUtil", "DocumentSnapshot data: " + data);
                    callback.onCallback(data);
                } else {
                    Log.d("FirebaseUtil", "Error getting document", task.getException());
                }
            }
        });
    }
}