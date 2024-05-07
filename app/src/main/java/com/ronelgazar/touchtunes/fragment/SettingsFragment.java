package com.ronelgazar.touchtunes.fragment;

import android.os.Bundle;
import android.util.Log;

import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.ronelgazar.touchtunes.R;
import com.ronelgazar.touchtunes.model.Mode;

public class SettingsFragment extends PreferenceFragmentCompat {

    private Mode mode;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        mode = getArguments().getParcelable("mode");
        if (mode == null) {
            // Handle the case where mode is null (e.g., display an error message)
            Log.e("SettingsFragment", "Missing mode data in fragment arguments");
            return;
        }
        mode.printMode();
        bindPreferences();
    }

    private void bindPreferences() {

        // Interaction Duration (EditTextPreference)
        EditTextPreference interactionDurationPref = (EditTextPreference) findPreference("interaction_duration");
        interactionDurationPref.setText(String.valueOf(mode.getSessionDurationInMinutes()));
        interactionDurationPref.setOnPreferenceChangeListener((preference, newValue) -> {
            try {
                int newInteractionDuration = Integer.parseInt(newValue.toString());
                mode.setSessionDuration(String.format("%02d:%02d", newInteractionDuration / 60, newInteractionDuration % 60));
                updatePreferenceSummary(preference, newValue.toString());
                return true;
            } catch (NumberFormatException e) {
                // Handle invalid input (e.g., show error message)
                Log.w("SettingsFragment", "Invalid input for interaction duration");
                return false;
            }
        });

        // Interaction Type (ListPreference)
        ListPreference interactionTypePref = (ListPreference) findPreference("interaction_type");
        interactionTypePref.setValue(mode.getInteractionType());
        interactionTypePref.setOnPreferenceChangeListener((preference, newValue) -> {
            mode.setInteractionType(newValue.toString());
            updatePreferenceSummary(preference, newValue.toString());
            return true;
        });

        // Sound Interaction (SwitchPreferenceCompat)
        SwitchPreferenceCompat soundInteractionPref = (SwitchPreferenceCompat) findPreference("sound_interaction");
        soundInteractionPref.setChecked(mode.getSoundInteraction());
        soundInteractionPref.setOnPreferenceChangeListener((preference, newValue) -> {
            mode.setSoundInteraction((Boolean) newValue);
            updatePreferenceSummary(preference, String.valueOf(newValue));
            return true;
        });

        // Vibration Intensity (EditTextPreference)
        EditTextPreference vibrationIntensityPref = (EditTextPreference) findPreference("vibration");
        vibrationIntensityPref.setText(String.valueOf(mode.getVibrationIntensity()));
        vibrationIntensityPref.setOnPreferenceChangeListener((preference, newValue) -> {
            try {
                int newVibrationIntensity = Integer.parseInt(newValue.toString());
                mode.setVibrationIntensity(newValue.toString());
                updatePreferenceSummary(preference, newValue.toString());
                return true;
            } catch (NumberFormatException e) {
                // Handle invalid input (e.g., show error message)
                Log.w("SettingsFragment", "Invalid input for vibration intensity");
                return false;
            }
        });

        // Lighting (Preference - Assuming new preference)
        Preference lightingPref = findPreference("lighting");
        // Set default value or handle lighting preference as needed
    }

    private void updatePreferenceSummary(Preference preference, String value) {
        if (preference instanceof EditTextPreference) {
            preference.setSummary(value);
        } else if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(value);
            if (index >= 0) {
                preference.setSummary(listPreference.getEntries()[index]);
            }
        } else if (preference instanceof SwitchPreferenceCompat) {
            boolean boolValue = Boolean.parseBoolean(value);
            preference.setSummary(boolValue ? "Enabled" : "Disabled");
        }
    }
}
