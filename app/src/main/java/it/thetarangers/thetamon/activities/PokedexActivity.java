package it.thetarangers.thetamon.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import it.thetarangers.thetamon.R;
import it.thetarangers.thetamon.fragments.FragmentPokedex;

public class PokedexActivity extends AppCompatActivity {
    private FragmentPokedex fragmentPokedex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokedex);

        fragmentPokedex = new FragmentPokedex(this);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flMain, fragmentPokedex)
                .commit();
    }
}
