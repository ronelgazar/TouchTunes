package com.ronelgazar.touchtunes.fragment;


import com.ronelgazar.touchtunes.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.ronelgazar.touchtunes.model.Mode;

public class SettingsFragment extends Fragment {

    private Mode settings;

    private Spinner interactionDurationSpinner;
    private Spinner interactionTypeSpinner;
    private Spinner lightingSpinner;
    private Spinner sessionDurationSpinner;
    private Spinner soundInteractionSpinner;
    private Spinner vibrationSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        settings = new Mode();

        interactionDurationSpinner = view.findViewById(R.id.interaction_duration_spinner);
        interactionTypeSpinner = view.findViewById(R.id.interaction_type_spinner);
        lightingSpinner = view.findViewById(R.id.lighting_spinner);
        sessionDurationSpinner = view.findViewById(R.id.session_duration_spinner);
        soundInteractionSpinner = view.findViewById(R.id.sound_interaction_spinner);
        vibrationSpinner = view.findViewById(R.id.vibration_spinner);

        // Update the UI elements to reflect the current settings values.
        interactionDurationSpinner.setSelection(Integer.parseInt(settings.getInteractionDuration()));
        interactionTypeSpinner.setSelection(Integer.parseInt(settings.getInteractionType()));
        lightingSpinner.setSelection(Integer.parseInt(settings.getLighting()));
        sessionDurationSpinner.setSelection(Integer.parseInt(settings.getSessionDuration()));
        soundInteractionSpinner.setSelection(Integer.parseInt(settings.getSoundInteraction()));
        vibrationSpinner.setSelection(Integer.parseInt(settings.getVibration()));

        // Add listeners to the UI elements to handle changes to the settings values.
        interactionDurationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                settings.setInteractionDuration(String.valueOf(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        interactionTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                settings.setInteractionType(String.valueOf(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        lightingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                settings.setLighting(String.valueOf(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        sessionDurationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                settings.setSessionDuration(String.valueOf(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        soundInteractionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                settings.setSoundInteraction(String.valueOf(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        vibrationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                settings.setVibration(String.valueOf(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return view;
    }
}
