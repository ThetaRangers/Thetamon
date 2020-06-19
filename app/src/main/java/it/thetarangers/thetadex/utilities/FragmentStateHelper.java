package it.thetarangers.thetadex.utilities;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.HashMap;

public class FragmentStateHelper {

    private HashMap<Integer, Fragment.SavedState> fragmentSavedStates;
    private FragmentManager fragmentManager;

    public FragmentStateHelper(FragmentManager fragmentManager) {
        fragmentSavedStates = new HashMap<>();
        this.fragmentManager = fragmentManager;
    }

    // Restores a single Fragment's state
    public void restoreState(Fragment fragment, Integer key) {
        if (!fragment.isAdded()) {
            Fragment.SavedState savedState = fragmentSavedStates.get(key);
            fragment.setInitialSavedState(savedState);
        }
    }

    // Saves a single Fragment's state
    public void saveState(Fragment fragment, Integer key) {
        if (fragment.isAdded()) {
            fragmentSavedStates.put(key, fragmentManager.saveFragmentInstanceState(fragment));
        }
    }

    // Returns a bundle with all the SavedStates in it
    public Bundle saveHelperState() {
        Bundle state = new Bundle();

        for (HashMap.Entry<Integer, Fragment.SavedState> entry : fragmentSavedStates.entrySet()) {
            state.putParcelable(entry.getKey().toString(), entry.getValue());
        }

        return state;
    }

    // Restores all SavedStates from a bundle
    public void restoreHelperState(Bundle savedState) {
        fragmentSavedStates.clear();
        for (String key : savedState.keySet()) {
            fragmentSavedStates.put(Integer.parseInt(key), savedState.getParcelable(key));
        }
    }
}