package com.ronelgazar.touchtunes.activity;

import static com.ronelgazar.touchtunes.util.DefualtData.getDefaultPatient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ronelgazar.touchtunes.R;
import com.ronelgazar.touchtunes.model.Mode;
import com.ronelgazar.touchtunes.model.Patient;
import com.ronelgazar.touchtunes.model.Playlist;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class LoginActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> signInLauncher;
    private List<AuthUI.IdpConfig> providers;
    private Intent signInIntent;
    private FirebaseUser user;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        // Set the authentication providers.
        providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create the sign-in intent.
        signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false)
                .build();

        // Set up the sign-in launcher.
        signInLauncher = registerForActivityResult(
                new FirebaseAuthUIActivityResultContract(),
                this::onSignInResult);

        // Start the sign-in flow.
        signInLauncher.launch(signInIntent);

        // Initialize Firestore instance
        db = FirebaseFirestore.getInstance();
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        if (result.getResultCode() == RESULT_OK) {
            user = FirebaseAuth.getInstance().getCurrentUser();

            if (user != null) {
                // Check if user exists in Firestore
                DocumentReference docRef = db.collection("Patients").document(user.getUid());
                docRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("LoginActivity", "Existing User");
                            Map<String, Object> data = document.getData();
                            DocumentReference modeRef = (DocumentReference) data.get("mode");
                            DocumentReference playlistRef = (DocumentReference) data.get("playlist");
                            fetchModeAndPlaylist(data, modeRef, playlistRef, patient -> {
                                Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                                mainIntent.putExtra("patient", patient);
                                startActivity(mainIntent);
                                finish();
                            });
                        } else {
                            Log.d("LoginActivity", "New User detected");
                            Patient patient = getDefaultPatient();
                            patient.setUid(user.getUid());
                            // Save the patient data locally using SharedPreferences
                            savePatientToSharedPrefs(patient);
                            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                            mainIntent.putExtra("patient", patient);
                            startActivity(mainIntent);
                            finish();
                        }
                    } else {
                        Log.d("LoginActivity", "Error getting user data", task.getException());
                        showToast("Failed to fetch patient data");
                    }
                });
            }
        }
    }

    private void fetchModeAndPlaylist(Map<String, Object> data, DocumentReference modeRef, DocumentReference playlistRef, PatientCallback callback) {
        AtomicReference<Mode> mode = new AtomicReference<>(new Mode()); // Initialize with default values
        AtomicReference<Playlist> playlist = new AtomicReference<>(new Playlist()); // Initialize with default values

        // Fetch mode
        modeRef.get().addOnCompleteListener(modeTask -> {
            if (modeTask.isSuccessful()) {
                DocumentSnapshot modeDoc = modeTask.getResult();
                if (modeDoc.exists()) {
                    mode.set(new Mode(modeDoc.getData()));
                } else {
                    Log.e("LoginActivity", "Mode document not found");
                }
            } else {
                Log.e("LoginActivity", "Error getting mode document", modeTask.getException());
            }

            // Fetch playlist
            playlistRef.get().addOnCompleteListener(playlistTask -> {
                if (playlistTask.isSuccessful()) {
                    DocumentSnapshot playlistDoc = playlistTask.getResult();
                    if (playlistDoc.exists()) {
                        playlist.set(new Playlist(playlistDoc.getData()));
                    } else {
                        Log.e("LoginActivity", "Playlist document not found");
                    }
                } else {
                    Log.e("LoginActivity", "Error getting playlist document", playlistTask.getException());
                }

                // Check if both mode and playlist are fetched
                if (mode.get() != null && playlist.get() != null) {
                    Patient patient = new Patient(data);
                    patient.setMode(mode.get());
                    patient.setPlaylist(playlist.get());
                    callback.onPatientDataLoaded(patient); // Invoke callback with patient object
                }
            });
        });
    }

    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
    }

    private void savePatientToSharedPrefs(Patient patient) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("uid", patient.getUid());
        editor.putString("name", patient.getName());
        editor.putBoolean("isActive", patient.isActive());
        editor.apply();
    }

    public interface PatientCallback {
        void onPatientDataLoaded(Patient patient);
    }
}
