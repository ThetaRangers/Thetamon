package it.thetarangers.thetamon.fragments;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceDialogFragmentCompat;
import androidx.preference.PreferenceFragmentCompat;

import java.util.Objects;

import it.thetarangers.thetamon.R;
import it.thetarangers.thetamon.activities.PokedexActivity;

public class FragmentSettings extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Guarantees consistence with PreferencesHandler
        getPreferenceManager().setSharedPreferencesName(requireContext().getString(R.string.preferences_name));
        setPreferencesFromResource(R.xml.settings_pref, rootKey);
        Preference nightMode = findPreference(getString(R.string.night_mode_pref));
        assert nightMode != null;
        nightMode.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.getKey().equals(getString(R.string.night_mode_pref))) // Instantly change theme on preference change
            ((PokedexActivity) requireActivity()).switchTheme((String) newValue);
        return true;
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if (preference.getKey().equals(getString(R.string.first_use_pref))) { // Display custom dialog
            FragmentResetDataDialog dialogFragment = FragmentResetDataDialog
                    .newInstance(preference.getKey());
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(getParentFragmentManager(), null);
        } else {
            super.onDisplayPreferenceDialog(preference);
        }
    }

    public static class FragmentResetDataDialog extends PreferenceDialogFragmentCompat {

        public static FragmentResetDataDialog newInstance(String key) {
            FragmentResetDataDialog fragment = new FragmentResetDataDialog();
            Bundle bundle = new Bundle(1);
            bundle.putString(PreferenceDialogFragmentCompat.ARG_KEY, key);
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public void onDialogClosed(boolean positiveResult) {
            if (positiveResult) {
                ((ActivityManager) Objects.requireNonNull(requireContext()
                        .getSystemService(Context.ACTIVITY_SERVICE))).clearApplicationUserData();
            }
        }

    }

}
