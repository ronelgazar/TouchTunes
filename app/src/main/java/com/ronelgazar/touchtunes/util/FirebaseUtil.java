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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ronelgazar.touchtunes.activity.MainActivity;
import com.ronelgazar.touchtunes.model.Mode;
import com.ronelgazar.touchtunes.model.Patient;
import com.ronelgazar.touchtunes.model.Song;

import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FirebaseUtil {
    //write  a util for creating and getting Patient Data from firestore

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    
    public void createPatient(String uid, boolean isActive, Mode mode, String name, DocumentReference playlist) {
        Patient patient = new Patient(uid, isActive, mode, name, playlist);
        db.collection("Patients").document(uid).set(patient);
    }

    public void getPatient(String uid) {
        DocumentReference docRef = db.collection("Patients").document(uid);
        
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("PATIENT", "DocumentSnapshot data: " + document.getData());
                        
                    } else {
                        Log.d("PATIENT", "No such document");
                    }
                } else {
                    Log.d("PATIENT", "get failed with ", task.getException());
                }
            }
        });
    }

    public void updatePatient(Patient patient) {
        db.collection("Patients").document(patient.getUid()).set(patient);
    }

    public void deletePatient(String uid) {
        db.collection("Patients").document(uid).delete();
    }
}