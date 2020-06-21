package it.thetarangers.thetadex.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

import java.util.HashMap;
import java.util.Objects;

import it.thetarangers.thetadex.R;
import it.thetarangers.thetadex.fragments.FragmentAbout;
import it.thetarangers.thetadex.fragments.FragmentFavorites;
import it.thetarangers.thetadex.fragments.FragmentGame;
import it.thetarangers.thetadex.fragments.FragmentPokedex;
import it.thetarangers.thetadex.fragments.FragmentSettings;
import it.thetarangers.thetadex.utilities.FragmentStateHelper;

public class PokedexActivity extends AppCompatActivity {

    private static final String HELPER = "helper";
    private static final String CHECKED_ITEM = "checked_item";

    private HashMap<Integer, Fragment> fragments;
    private FragmentStateHelper fragmentStateHelper;
    private Integer checkedItemId;
    private Holder holder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokedex);

        // Create all fragments and store them in the HashMap
        fragments = new HashMap<>();
        fragments.put(R.id.item_pokedex, new FragmentPokedex());
        fragments.put(R.id.item_favorites, new FragmentFavorites());
        fragments.put(R.id.item_game, new FragmentGame());
        fragments.put(R.id.item_settings, new FragmentSettings());
        fragments.put(R.id.item_about, new FragmentAbout());

        fragmentStateHelper = new FragmentStateHelper(getSupportFragmentManager());

        holder = new Holder();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        // If the activity is first started
        if (savedInstanceState == null) {
            switchFragment(fragments.get(R.id.item_pokedex));
            checkedItemId = R.id.item_pokedex;
        } else {
            // Get the attached Fragment
            checkedItemId = savedInstanceState.getInt(CHECKED_ITEM);
            Fragment currentFragment = getSupportFragmentManager()
                    .findFragmentById(R.id.flMain);
            fragments.put(checkedItemId, currentFragment);

            // Restore the state of all fragments
            Bundle helperState = savedInstanceState.getBundle(HELPER);
            assert helperState != null;
            fragmentStateHelper.restoreHelperState(helperState);
        }
        holder.navView.setCheckedItem(checkedItemId);
        Objects.requireNonNull(holder.navView.getCheckedItem()).setEnabled(false);

        holder.actionBarDrawerToggle.syncState();
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        // Save current Fragment's state
        saveCurrentState();
        // Save the state of all Fragments
        outState.putBundle(HELPER, fragmentStateHelper.saveHelperState());
        // Save the state of NavigationDrawer
        outState.putInt(CHECKED_ITEM, checkedItemId);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        // If the drawer is open close it
        if (holder.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            holder.drawerLayout.closeDrawer(GravityCompat.START);
        } else if (checkedItemId != R.id.item_pokedex) {
            // Goes back to the Pokedex and update drawer
            // Easier than handling BackStack
            holder.onNavigationItemSelected(holder.navView.getMenu().findItem(R.id.item_pokedex));
            holder.navView.setCheckedItem(R.id.item_pokedex);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Forward activity result to child fragments
        for (HashMap.Entry<Integer, Fragment> entry : fragments.entrySet()) {
            if (entry.getValue().isAdded()) {
                if (entry.getKey() == R.id.item_pokedex) {
                    OnActivityResultCallback callback = (OnActivityResultCallback) entry.getValue();
                    assert callback != null;
                    callback.onActivityResultCallback(requestCode, resultCode, data);
                }
                break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void saveCurrentState() {
        Fragment current = fragments.get(checkedItemId);
        if (current != null)
            fragmentStateHelper.saveState(current, checkedItemId);
    }

    private void switchFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.fade_in,
                        R.animator.fade_out, 0, 0)
                .replace(R.id.flMain, fragment)
                .commitNowAllowingStateLoss();
    }

    // Used to change the Theme of the app
    public void switchTheme(String val) {
        if (val.equals(getString(R.string.light_enum)))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        else if (val.equals(getString(R.string.dark_enum)))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    public void lockDrawer() {
        holder.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    public void unlockDrawer() {
        holder.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        if (imm != null)
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public interface OnActivityResultCallback {
        void onActivityResultCallback(int requestCode, int resultCode, @Nullable Intent data);
    }

    class Holder implements NavigationView.OnNavigationItemSelectedListener {

        private final NavigationView navView;
        private final DrawerLayout drawerLayout;
        private final ActionBarDrawerToggle actionBarDrawerToggle;

        public Holder() {

            drawerLayout = findViewById(R.id.drawer_layout);
            Toolbar toolbar = findViewById(R.id.toolbar);

            actionBarDrawerToggle = new ActionBarDrawerToggle(PokedexActivity.this,
                    drawerLayout,
                    toolbar,
                    R.string.open_drawer_toggle,
                    R.string.close_drawer_toggle);

            navView = findViewById(R.id.navView);

            navView.setNavigationItemSelectedListener(this);

        }

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            // Enable previous MenuItem
            Objects.requireNonNull(navView.getCheckedItem()).setEnabled(true);

            // Disable current MenuItem
            item.setEnabled(false);

            // Save current Fragment's state
            saveCurrentState();

            // Prepare Fragment and restore its state
            Fragment newFragment = fragments.get(item.getItemId());
            assert newFragment != null;
            fragmentStateHelper.restoreState(newFragment, item.getItemId());

            // Commit Transaction
            switchFragment(newFragment);

            // Close keyboard if open
            hideKeyboard();

            // Close drawer
            drawerLayout.closeDrawer(GravityCompat.START);

            checkedItemId = item.getItemId();

            return true;
        }
    }

}
