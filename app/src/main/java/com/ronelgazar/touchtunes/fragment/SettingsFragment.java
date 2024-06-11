package com.ronelgazar.touchtunes.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.ronelgazar.touchtunes.R;
import com.ronelgazar.touchtunes.model.Mode;
import com.ronelgazar.touchtunes.model.Mode.InteractionType;

public class SettingsFragment extends PreferenceFragmentCompat {

    private static Mode mode;
    private SharedPreferences sharedPreferences;

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
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        bindPreferences();
    }

    private void bindPreferences() {

        // Interaction Duration (EditTextPreference)
        EditTextPreference interactionDurationPref = findPreference("interaction_duration");
        if (interactionDurationPref != null) {
            interactionDurationPref.setText(String.valueOf(mode.getSessionDurationInMinutes()));
            interactionDurationPref.setOnPreferenceChangeListener((preference, newValue) -> {
                try {
                    double newInteractionDuration = Double.parseDouble(newValue.toString());
                    if (newInteractionDuration < 0) {
                        throw new NumberFormatException("Negative value");
                    }
                    mode.setSessionDuration(newValue.toString());
                    updatePreferenceSummary(preference, newValue.toString());
                    savePreference("interaction_duration", newValue.toString());
                    return true;
                } catch (NumberFormatException e) {
                    // Handle invalid input (e.g., show error message)
                    Log.w("SettingsFragment", "Invalid input for interaction duration");
                    showToast("אנא הזינו מספר חיובי  עבור משך האינטראקציה.");
                    return false;
                }
            });
            updatePreferenceSummary(interactionDurationPref, interactionDurationPref.getText());
        }

        // Interaction Type (ListPreference)
        ListPreference interactionTypePref = findPreference("interaction_type");
        if (interactionTypePref != null) {
            interactionTypePref.setValue(mode.getInteractionType().toString());
            interactionTypePref.setOnPreferenceChangeListener((preference, newValue) -> {
                try {
                    InteractionType interactionType = InteractionType.fromString(newValue.toString());
                    mode.setInteractionType(interactionType.toString());
                    updatePreferenceSummary(preference, interactionType.toString());
                    savePreference("interaction_type", interactionType.toString());
                    return true;
                } catch (IllegalArgumentException e) {
                    // Handle invalid input (e.g., show error message)
                    Log.w("SettingsFragment", "Invalid input for interaction type");
                    showToast("אנא ביחרו סוג אינטראקציה .");
                    return false;
                }
            });
            updatePreferenceSummary(interactionTypePref, interactionTypePref.getValue());
        }

        // Sound Interaction (SwitchPreferenceCompat)
        SwitchPreferenceCompat soundInteractionPref = findPreference("sound_interaction");
        if (soundInteractionPref != null) {
            soundInteractionPref.setChecked(mode.getSoundInteraction());
            soundInteractionPref.setOnPreferenceChangeListener((preference, newValue) -> {
                mode.setSoundInteraction((Boolean) newValue);
                updatePreferenceSummary(preference, String.valueOf(newValue));
                saveBooleanPreference("sound_interaction", (Boolean) newValue);
                return true;
            });
            updatePreferenceSummary(soundInteractionPref, String.valueOf(soundInteractionPref.isChecked()));
        }

        // Vibration Intensity (EditTextPreference)
        EditTextPreference vibrationIntensityPref = findPreference("vibration");
        if (vibrationIntensityPref != null) {
            vibrationIntensityPref.setText(String.valueOf(mode.getVibrationIntensity()));
            vibrationIntensityPref.setOnPreferenceChangeListener((preference, newValue) -> {
                try {
                    int newVibrationIntensity = Integer.parseInt(newValue.toString());
                    if (newVibrationIntensity < 0 || newVibrationIntensity > 100) {
                        throw new NumberFormatException("Out of range value");
                    }
                    mode.setVibrationIntensity(newValue.toString());
                    updatePreferenceSummary(preference, newValue.toString());
                    savePreference("vibration", newValue.toString());
                    return true;
                } catch (NumberFormatException e) {
                    // Handle invalid input (e.g., show error message)
                    Log.w("SettingsFragment", "Invalid input for vibration intensity");
                    showToast("אנא הזינו מספר בין 0 ל-100 לעוצמת הרטט.");
                    return false;
                }
            });
            updatePreferenceSummary(vibrationIntensityPref, vibrationIntensityPref.getText());
        }

        // Lighting (EditTextPreference)
        EditTextPreference lightingPref = findPreference("lighting");
        if (lightingPref != null) {
            lightingPref.setText(String.valueOf(mode.getLightingSettings()));
            lightingPref.setOnPreferenceChangeListener((preference, newValue) -> {
                try {
                    int newLightingIntensity = Integer.parseInt(newValue.toString());
                    if (newLightingIntensity < 0 || newLightingIntensity > 100) {
                        throw new NumberFormatException("Out of range value");
                    }
                    mode.setLightingSettings(String.valueOf(newLightingIntensity));
                    updatePreferenceSummary(preference, newValue.toString());
                    savePreference("lighting", newValue.toString());
                    return true;
                } catch (NumberFormatException e) {
                    // Handle invalid input (e.g., show error message)
                    Log.w("SettingsFragment", "Invalid input for lighting intensity");
                    showToast("אנא הזינו מספר  בין 0 ל-100 לעוצמת התאורה.");
                    return false;
                }
            });
            updatePreferenceSummary(lightingPref, lightingPref.getText());
        }

        // Session Duration (EditTextPreference)
        EditTextPreference sessionDurationPref = findPreference("session_duration");
        if (sessionDurationPref != null) {
            sessionDurationPref.setText(formatSessionDuration(mode.getSessionDuration()));
            sessionDurationPref.setOnPreferenceChangeListener((preference, newValue) -> {
                try {
                    String[] parts = newValue.toString().split(":");
                    if (parts.length != 2) {
                        throw new IllegalArgumentException("Invalid format");
                    }
                    int minutes = Integer.parseInt(parts[0]);
                    int seconds = Integer.parseInt(parts[1]);
                    if (minutes < 0 || seconds < 0 || seconds >= 60) {
                        throw new NumberFormatException("Invalid value");
                    }
                    mode.setSessionDuration(formatSessionDuration(minutes, seconds));
                    updatePreferenceSummary(preference, formatSessionDuration(minutes, seconds));
                    savePreference("session_duration", formatSessionDuration(minutes, seconds));
                    return true;
                } catch (IllegalArgumentException e) {
                    // Handle invalid input (e.g., show error message)
                    Log.w("SettingsFragment", "Invalid input for session duration");
                    showToast("אנא הזינו זמן  בפורמט MM:SS.");
                    return false;
                }
            });
            updatePreferenceSummary(sessionDurationPref, sessionDurationPref.getText());
        }
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

    private void savePreference(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private void saveBooleanPreference(String key, Boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    // Static method to update settings and return a Mode object with all the new settings
    public static Mode updateSettings() {
        return mode;
    }

    // When the window closes, the mode object is returned with all the new settings
    @Override
    public void onStop() {
        super.onStop();
        saveModeSettings();
    }

    private void saveModeSettings() {
        if (mode != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("interaction_duration", mode.getSessionDurationInMinutes());
            editor.putString("interaction_type", mode.getInteractionType().toString());
            editor.putBoolean("sound_interaction", mode.getSoundInteraction());
            editor.putString("vibration", String.valueOf(mode.getVibrationIntensity()));
            editor.putString("lighting", String.valueOf(mode.getLightingSettings()));
            editor.putString("session_duration", mode.getSessionDuration());
            editor.apply();
        }
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private String formatSessionDuration(int minutes, int seconds) {
        return String.format("%02d:%02d", minutes, seconds);
    }

    private String formatSessionDuration(String duration) {
        // Assuming duration is in minutes:seconds format
        return duration;
    }
}
