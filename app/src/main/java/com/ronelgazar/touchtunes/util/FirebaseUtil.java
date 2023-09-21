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
import com.ronelgazar.touchtunes.activity.MainActivity;
import com.ronelgazar.touchtunes.model.Patient;

import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

public class FirebaseUtil {

    private static FirebaseDatabase firebaseDatabase;

    public FirebaseUtil() {
        firebaseDatabase = FirebaseDatabase.getInstance();
    }

    public static DatabaseReference getDatabaseReference(String collectionPath) {
        return firebaseDatabase.getReference(collectionPath);
    }

/*    public void loginWithGmail(String email, String password, Context context) {
        AuthCredential credential = GoogleAuthProvider.getCredential(email, password);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // The user is logged in
                            Intent intent = new Intent(context, MainActivity.class);
                            context.startActivity(intent);
                        } else {
                            // An error occurred
                        }
                    }
                });
    }*/

    public void loginWithEmailAndPassword(String email, String password) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // The user is logged in
                        } else {
                            // An error occurred
                        }
                    }
                });
    }

    public List<Patient> getPatients() {
        DatabaseReference patientsReference = firebaseDatabase.getReference("Patients");
        List<Patient> patients = new ArrayList<>();

        patientsReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        Patient patient = childSnapshot.getValue(Patient.class);
                        patients.add(patient);
                    }
                } else {
                    // An error occurred
                }
            }
        });

        return patients;
    }
}