package it.thetarangers.thetadex.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.simplecityapps.recyclerview_fastscroll.interfaces.OnFastScrollStateChangeListener;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.thetarangers.thetadex.R;
import it.thetarangers.thetadex.activities.PokedexActivity;
import it.thetarangers.thetadex.activities.PokemonDetailActivity;
import it.thetarangers.thetadex.adapters.PokedexAdapter;
import it.thetarangers.thetadex.database.DaoThread;
import it.thetarangers.thetadex.favorites.FavoritesManager;
import it.thetarangers.thetadex.listener.SelectorCallback;
import it.thetarangers.thetadex.model.Pokemon;
import it.thetarangers.thetadex.viewmodel.PokemonListViewModel;

public class FragmentPokedex extends Fragment implements SelectorCallback, PokedexActivity.OnActivityResultCallback {

    private PokemonListViewModel pokemonListViewModel;
    private Holder holder;

    private ActionMode actionMode = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pokedex, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        holder = new Holder(view);

        // Observe the LiveData
        pokemonListViewModel = new ViewModelProvider(requireActivity()).get(PokemonListViewModel.class);
        pokemonListViewModel.getPokemons().observe(getViewLifecycleOwner(),
                holder.adapter::setPokemonList);
        pokemonListViewModel.getFilters().observe(getViewLifecycleOwner(), this::filter);

        List<Pokemon> tmp = pokemonListViewModel.getPokemonList();
        if (tmp == null) // Application is first started
            fill(true); // Load all pokemons
    }

    //reload all pokedex and clear filters
    private void fill(Boolean isFirstTime) {
        DaoThread daoThread = new DaoThread(pokemonListViewModel);
        daoThread.getPokemonsFromName(getContext(), "");

        if (!isFirstTime)
            pokemonListViewModel.setFilters(new ArrayList<>()); // Clear filters
    }

    //filter pokemons by type
    private void filter(List<String> filters) {
        List<Pokemon> tmp = new ArrayList<>();
        List<Pokemon> pokemonList = pokemonListViewModel.getPokemonList();
        String type1;
        String type2 = null;

        switch (filters.size()) {
            case 2:
                type2 = filters.get(1).toLowerCase();
            case 1:
                type1 = filters.get(0).toLowerCase();
                break;
            default:
                holder.adapter.setPokemonList(pokemonList);
                return;
        }


        for (int i = 0; i < pokemonList.size(); i++) {
            Pokemon pokemon = pokemonList.get(i);
            //if selected only a type
            if (type2 != null) {
                if ((type1.equals(pokemon.getType1()) && type2.equals(pokemon.getType2())) ||
                        (type2.equals(pokemon.getType1()) && type1.equals(pokemon.getType2()))) {
                    tmp.add(pokemon);
                }
            } else { //if selected two types
                if (type1.equals(pokemon.getType1()) || type1.equals(pokemon.getType2())) {
                    tmp.add(pokemon);
                }
            }
        }

        holder.adapter.setPokemonList(tmp);
    }

    //implements callback interface
    @Override
    public void onSelect(int size) {
        //if actionMode already exists check if size is 0 and close actionMode or just change actionMode title
        if (actionMode != null) {
            if (size == 0)
                actionMode.finish();
            else
                actionMode.setTitle(getString(R.string.menu_start_title) + " " + size + getString(R.string.menu_end_title));
            return;

        }
        //if actionMode does't exist create it
        actionMode = requireActivity().startActionMode(new PokedexCallback());

    }

    //implements pokedex activity callback
    @Override
    public void onActivityResultCallback(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PokedexAdapter.REQ_CODE) {
            if (data != null) {
                //get favorite changes of child activities
                @SuppressWarnings("unchecked")
                HashMap<Integer, Boolean> pokemons = (HashMap<Integer, Boolean>) data
                        .getSerializableExtra(PokemonDetailActivity.POKEMONS);
                //apply them on fragment recycler view
                if (pokemons != null) {
                    for (HashMap.Entry<Integer, Boolean> entry : pokemons.entrySet()) {
                        for (Pokemon pokemon2 : holder.adapter.pokemonList) {
                            if (entry.getKey() == pokemon2.getId()) {
                                pokemon2.setFavorite(entry.getValue());
                            }
                        }
                    }
                    holder.adapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void onResume() {
        //reset recycler view as clickable
        holder.adapter.setClickable(true);
        super.onResume();
    }

    //inner class that implements the ActionMode.Callback for ActionMode managment
    class PokedexCallback implements android.view.ActionMode.Callback {

        FavoritesManager favoritesManager = new FavoritesManager(requireContext());

        @Override
        public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
            //inflate menu on action mode creation
            mode.getMenuInflater().inflate(R.menu.menu_action_mode, menu);
            ((PokedexActivity) requireActivity()).lockDrawer();
            holder.onFastScrollStart();
            mode.setTitle(getString(R.string.menu_start_title) + " 1" + getString(R.string.menu_end_title));
            return true;
        }

        @Override
        public boolean onPrepareActionMode(android.view.ActionMode mode, Menu menu) {
            return false;
        }


        //Manage click on menu items
        @Override
        public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {
            List<Pokemon> sel = holder.adapter.getSelected();
            switch (item.getItemId()) {
                // deselect all
                case R.id.item_deselect:
                    mode.finish();
                    break;

                case R.id.item_addAll:
                    favoritesManager.addPokemonToFav(sel);
                    mode.finish();
                    break;

                case R.id.item_removeAll:
                    favoritesManager.removePokemonFromFav(sel);
                    mode.finish();
                    break;

                default:
                    break;
            }

            return false;
        }

        //Called when ActionMode is destroyed
        @Override
        public void onDestroyActionMode(android.view.ActionMode mode) {
            holder.adapter.deselectAll();
            ((PokedexActivity) requireActivity()).unlockDrawer();
            holder.onFastScrollStop();
            actionMode = null;
        }
    }

    class Holder implements View.OnClickListener, OnFastScrollStateChangeListener {

        final FastScrollRecyclerView rvPokedex;
        final PokedexAdapter adapter;
        final FloatingActionButton fabAdd;
        final FloatingActionButton fabSearch;
        final FloatingActionButton fabFilter;
        final FloatingActionButton fabReset;
        boolean isOpen;
        boolean isClickable;

        Holder(View fp) {
            //if floating buttons are showed or not
            isOpen = false;

            isClickable = true;

            //set all view elements
            fabAdd = fp.findViewById(R.id.fabAdd);
            fabAdd.setOnClickListener(this);
            fabAdd.animate().setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    isClickable = true;
                    super.onAnimationEnd(animation);
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    isClickable = false;
                    super.onAnimationStart(animation);
                }
            });

            fabFilter = fp.findViewById(R.id.fabFilter);
            fabFilter.setOnClickListener(this);
            init(fabFilter);

            fabSearch = fp.findViewById(R.id.fabSearch);
            fabSearch.setOnClickListener(this);
            init(fabSearch);

            fabReset = fp.findViewById(R.id.fabReset);
            fabReset.setOnClickListener(this);
            init(fabReset);

            rvPokedex = fp.findViewById(R.id.rvPokedex);

            int orientation = getResources().getConfiguration().orientation;
            int columns;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                // In landscape
                columns = 2;
            } else {
                // In portrait
                columns = 1;
            }

            //set adpater, layout manager and fastScrollBar manager to recycler view
            rvPokedex.setLayoutManager(new GridLayoutManager(getContext(), columns));
            adapter = new PokedexAdapter(getActivity(), FragmentPokedex.this);
            adapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);

            rvPokedex.setAdapter(adapter);
            rvPokedex.setOnFastScrollStateChangeListener(this);

        }

        // Main floating button rotation
        private boolean rotateFab(final View v, boolean rotate) {
            v.animate().setDuration(200).rotation(rotate ? 135f : 0f);

            return rotate;
        }

        // Make button visible with animation
        private void showIn(final View v) {
            v.setVisibility(View.VISIBLE);
            v.setAlpha(0f);
            v.setTranslationY(v.getHeight());
            v.animate()
                    .setDuration(200)
                    .translationY(0)
                    .alpha(1f);
        }

        // Hide with animation
        private void showOut(final View v) {
            v.setAlpha(1f);
            v.setTranslationY(0);
            v.animate()
                    .setDuration(200)
                    .translationY(v.getHeight())
                    .alpha(0f);
        }

        // Init button visibility and listener
        private void init(final View v) {
            v.animate().setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (!isOpen)
                        v.setVisibility(View.GONE);
                }
            });
            showOut(v);
        }

        //show out all buttons
        private void collapseFab() {
            showOut(fabFilter);
            showOut(fabSearch);
            showOut(fabReset);
            isOpen = rotateFab(fabAdd, !isOpen);
        }

        //show in all buttons
        private void openFab() {
            showIn(fabFilter);
            showIn(fabSearch);
            showIn(fabReset);
            isOpen = rotateFab(fabAdd, !isOpen);
        }

        @Override
        public void onClick(View v) {
            if (!isClickable)
                return;
            switch (v.getId()) {
                case R.id.fabAdd:
                    if (isOpen) { //open or collapse fab
                        collapseFab();
                    } else {
                        openFab();
                    }
                    break;
                case R.id.fabFilter: //create filter fragment
                    new FragmentFilter().show(getParentFragmentManager(), FragmentFilter.TAG);
                    collapseFab();
                    break;
                case R.id.fabSearch: //create search fragment
                    new FragmentSearch().show(getParentFragmentManager(), FragmentSearch.TAG);
                    collapseFab();
                    break;
                case R.id.fabReset: //reset search and filters
                    fill(false);
                    collapseFab();
                    break;
                default:
                    break;
            }
        }

        //hide all buttons on fast scroll
        @Override
        public void onFastScrollStart() {
            fabAdd.setVisibility(View.GONE);
            fabFilter.setVisibility(View.GONE);
            fabSearch.setVisibility(View.GONE);
            fabReset.setVisibility(View.GONE);
        }

        //show all buttons when fast scroll stop
        @Override
        public void onFastScrollStop() {
            fabAdd.setVisibility(View.VISIBLE);
            fabFilter.setVisibility(View.VISIBLE);
            fabSearch.setVisibility(View.VISIBLE);
            fabReset.setVisibility(View.VISIBLE);
        }

    }

}
