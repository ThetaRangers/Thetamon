package it.thetarangers.thetamon.utilities;

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

    public void restoreState(Fragment fragment, Integer key) {
        if (!fragment.isAdded()) {
            Fragment.SavedState savedState = fragmentSavedStates.get(key);
            fragment.setInitialSavedState(savedState);
        }
    }

    public void saveState(Fragment fragment, Integer key) {
        if (fragment.isAdded()) {
            fragmentSavedStates.put(key, fragmentManager.saveFragmentInstanceState(fragment));
        }
    }

    public Bundle saveHelperState() {
        Bundle state = new Bundle();

        for (Integer key : fragmentSavedStates.keySet()) {
            state.putParcelable(key.toString(), fragmentSavedStates.get(key));
        }

        return state;
    }

    public void restoreHelperState(Bundle savedState) {
        fragmentSavedStates.clear();
        for (String key : savedState.keySet()) {
            fragmentSavedStates.put(Integer.parseInt(key), savedState.getParcelable(key));
        }
    }
}