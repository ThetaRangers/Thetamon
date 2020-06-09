package it.thetarangers.thetamon.fragments;

import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import it.thetarangers.thetamon.R;
import it.thetarangers.thetamon.activities.PokedexActivity;

public class FragmentSettings extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        getPreferenceManager().setSharedPreferencesName(requireContext().getString(R.string.preferences_name));
        setPreferencesFromResource(R.xml.settings_pref, rootKey);
        Preference nightMode = findPreference(getString(R.string.night_mode));
        assert nightMode != null;
        nightMode.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.getKey().equals(getString(R.string.night_mode)))
            ((PokedexActivity) requireActivity()).switchTheme((String) newValue);
        return true;
    }
}
