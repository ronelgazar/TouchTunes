package com.ronelgazar.touchtunes.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.ronelgazar.touchtunes.R;
import com.ronelgazar.touchtunes.model.Patient;
import com.ronelgazar.touchtunes.util.FirebaseUtil;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> signInLauncher;
    private List<AuthUI.IdpConfig> providers;
    private Intent signInIntent;
    private FirebaseUtil firebaseUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseUtil = new FirebaseUtil();

        // Set the authentication providers.
        providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create the sign-in intent.
        signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build();

        // Set up the sign-in launcher.
        signInLauncher = registerForActivityResult(
                new FirebaseAuthUIActivityResultContract(),
                this::onSignInResult);

        // Start the sign-in flow.
        signInLauncher.launch(signInIntent);
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // The user signed in successfully.
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                Log.d("USER", user.getUid());
                firebaseUtil.getPatient(user.getUid(), patientData -> {
                    if (patientData != null) {
                        Patient patient = new Patient(patientData);
                        patient.setUid(user.getUid());
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.putExtra("patient", patient);
                        startActivity(intent);
                        finish();
                    } else {
                        // Show an error toast.
                        Toast.makeText(this, "Failed to get patient information", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            } else {
                // Show an error toast.
                Toast.makeText(this, "Failed to get user information", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            // The sign-in process failed.
            if (response == null) {
                // User pressed back button
                Toast.makeText(this, "Sign-in cancelled", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            Toast.makeText(this, "Unknown error", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}