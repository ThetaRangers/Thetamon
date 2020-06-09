package it.thetarangers.thetamon.fragments;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import it.thetarangers.thetamon.R;

public class FragmentSettings extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        getPreferenceManager().setSharedPreferencesName(requireContext().getString(R.string.preferences_name));
        setPreferencesFromResource(R.xml.settings_pref, rootKey);
    }

}
