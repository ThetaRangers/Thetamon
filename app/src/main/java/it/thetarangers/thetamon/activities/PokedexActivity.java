package it.thetarangers.thetamon.activities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import it.thetarangers.thetamon.R;
import it.thetarangers.thetamon.fragments.FragmentPokedex;

public class PokedexActivity extends AppCompatActivity {
    private FragmentPokedex fragmentPokedex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokedex);

        FragmentManager fragmentManager = getSupportFragmentManager();

        if (savedInstanceState == null)
            fragmentPokedex = new FragmentPokedex();
        else
            fragmentPokedex = (FragmentPokedex) fragmentManager.getFragment(savedInstanceState, "FragmentPokedex");
        fragmentManager.beginTransaction()
                .replace(R.id.flMain, fragmentPokedex)
                .commit();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle out) {
        getSupportFragmentManager().putFragment(out, "FragmentPokedex", fragmentPokedex);
        super.onSaveInstanceState(out);
    }

}
