package it.thetarangers.thetamon.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;

import it.thetarangers.thetamon.R;
import it.thetarangers.thetamon.fragments.FragmentPokedex;
import it.thetarangers.thetamon.fragments.FragmentSettings;

public class PokedexActivity extends AppCompatActivity {

    private FragmentPokedex fragmentPokedex;
    private FragmentSettings fragmentSettings;
    private Holder holder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokedex);

        holder = new Holder();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentSettings = new FragmentSettings();
        if (savedInstanceState == null) {
            fragmentPokedex = new FragmentPokedex();
        } else {
            fragmentPokedex = (FragmentPokedex) fragmentManager.getFragment(savedInstanceState, "FragmentPokedex");
            if (fragmentPokedex == null)
                fragmentPokedex = new FragmentPokedex();
        }
        fragmentManager.beginTransaction()
                .replace(R.id.flMain, fragmentPokedex)
                .commit();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle out) {
        if (fragmentPokedex.isAdded())
            getSupportFragmentManager().putFragment(out, "FragmentPokedex", fragmentPokedex);
        super.onSaveInstanceState(out);
    }

    @Override
    public void onBackPressed() {
        if (holder.lastItem.getItemId() != R.id.item_pokedex)
            holder.onNavigationItemSelected(holder.nav_view.getMenu().getItem(0)); // TODO save in holder
        else
            super.onBackPressed();
    }

    void switchFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flMain,
                        fragment)
                .commit();
    }

    public void switchTheme(String val) {
        if (val.equals(getString(R.string.light_enum)))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        else if (val.equals(getString(R.string.dark_enum)))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    class Holder implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

        final private Button btn_open;
        final private DrawerLayout drawerLayout;
        final private NavigationView nav_view;

        //TODO può essere fatto molto meglio
        private MenuItem lastItem;

        public Holder() {
            btn_open = findViewById(R.id.btn_open);
            btn_open.setOnClickListener(this);

            drawerLayout = findViewById(R.id.drawer_layout);

            nav_view = findViewById(R.id.nav_view);
            //TODO manually check the first menu item
            //TODO menu is selected in activity_pokedex.xml
            lastItem = nav_view.getMenu().getItem(0);
            lastItem.setChecked(true);
            lastItem.setEnabled(false);

            //TODO add a header layout for navigation view
            nav_view.setNavigationItemSelectedListener(this);


        }

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            lastItem.setChecked(false);
            lastItem.setEnabled(true);
            item.setChecked(true);
            item.setEnabled(false);
            lastItem = item;
            drawerLayout.closeDrawer(GravityCompat.START);
            switch (item.getItemId()) {
                case R.id.item_pokedex:
                    switchFragment(fragmentPokedex);
                    break;
                case R.id.item_wtp:
                    Log.d("POKE", "It's a Pikachu");
                    break;
                case R.id.item_fav:
                    Log.d("POKE", "go to fav");
                    break;
                case R.id.item_settings:
                    switchFragment(fragmentSettings);
                    break;
                case R.id.item_about:
                    Log.d("POKE", "go to about");
                    break;
                default:
                    break;

            }
            return false;
        }


        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.btn_open) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        }

    }

}
