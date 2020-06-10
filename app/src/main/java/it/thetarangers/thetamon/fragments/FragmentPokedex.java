package it.thetarangers.thetamon.fragments;

import android.os.Bundle;


import android.util.Log;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.simplecityapps.recyclerview_fastscroll.interfaces.OnFastScrollStateChangeListener;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.thetarangers.thetamon.R;
import it.thetarangers.thetamon.activities.PokedexActivity;
import it.thetarangers.thetamon.adapters.PokedexAdapter;
import it.thetarangers.thetamon.database.DaoThread;
import it.thetarangers.thetamon.listener.SelectorCallback;
import it.thetarangers.thetamon.model.Pokemon;
import it.thetarangers.thetamon.viewmodel.PokemonListViewModel;

public class FragmentPokedex extends Fragment implements SelectorCallback {

    private PokemonListViewModel pokemonListViewModel;
    private Holder holder;

    private ActionMode actionMode =null;
    @Override
    public void onSelect(int size) {
        if (actionMode != null) {

            if (size == 0) {
                actionMode.finish();
            }
            return;

        }

        actionMode = getActivity().startActionMode(new android.view.ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.menu_navigation_view, menu);
                ((PokedexActivity)getActivity()).lockDrawer();
                Log.d("POKE","inflated");
                return true;
            }

            @Override
            public boolean onPrepareActionMode(android.view.ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(android.view.ActionMode mode) {
                holder.adapter.deselectAll();
                ((PokedexActivity)getActivity()).unlockDrawer();
                actionMode = null;
            }
        });

    }

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

    private void fill(Boolean isFirstTime) {
        DaoThread daoThread = new DaoThread(pokemonListViewModel);
        daoThread.getPokemonFromName(getContext(), "");

        if (!isFirstTime)
            pokemonListViewModel.setFilters(new ArrayList<>()); // Clear filters
    }

    private void filter(List<String> filters) {
        // TODO refactor please i'm dirty
        // TODO put this in FragmentFilter and save filtered list in ViewModel ?
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

            if (type2 != null) {
                if ((type1.equals(pokemon.getType1()) && type2.equals(pokemon.getType2())) ||
                        (type2.equals(pokemon.getType1()) && type1.equals(pokemon.getType2()))) {
                    tmp.add(pokemon);
                }
            } else {
                if (type1.equals(pokemon.getType1()) || type1.equals(pokemon.getType2())) {
                    tmp.add(pokemon);
                }
            }
        }

        holder.adapter.setPokemonList(tmp);
    }

    class Holder implements View.OnClickListener, OnFastScrollStateChangeListener {

        final FastScrollRecyclerView rvPokedex;
        final PokedexAdapter adapter;
        final FloatingActionButton fabAdd;
        final FloatingActionButton fabSearch;
        final FloatingActionButton fabFilter;
        final FloatingActionButton fabReset;
        boolean isOpen;

        Holder(View fp) {

            isOpen = false;

            fabAdd = fp.findViewById(R.id.fabAdd);
            fabAdd.setOnClickListener(this);

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
            rvPokedex.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new PokedexAdapter(getActivity(), FragmentPokedex.this::onSelect);
            adapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);

            rvPokedex.setAdapter(adapter);
            rvPokedex.setOnFastScrollStateChangeListener(this);

        }

        public boolean rotateFab(final View v, boolean rotate) {
            v.animate().setDuration(200).rotation(rotate ? 135f : 0f);
            return rotate;
        }

        public void showIn(final View v) {
            v.setAlpha(0f);
            v.setTranslationY(v.getHeight());
            v.animate().setDuration(200).translationY(0).alpha(1f).start();
        }

        public void showOut(final View v) {
            v.setAlpha(1f);
            v.setTranslationY(0);
            v.animate().setDuration(200).translationY(v.getHeight()).alpha(0f).start();
        }

        public void init(final View v) {
            v.setTranslationY(0);
            v.setAlpha(0f);
        }

        public void collapseFab() {
            showOut(fabFilter);
            showOut(fabSearch);
            showOut(fabReset);
            isOpen = rotateFab(fabAdd, !isOpen);
        }

        public void openFab() {
            showIn(fabFilter);
            showIn(fabSearch);
            showIn(fabReset);
            isOpen = rotateFab(fabAdd, !isOpen);
        }

        @Override
        public void onClick(View v) {
            if(actionMode!= null) actionMode.finish();
            switch (v.getId()) {
                case R.id.fabAdd:
                    if (isOpen) {
                        collapseFab();
                    } else {
                        openFab();
                    }
                    break;
                case R.id.fabFilter:
                    new FragmentFilter().show(getParentFragmentManager(), FragmentFilter.TAG);
                    collapseFab();
                    break;
                case R.id.fabSearch:
                    new FragmentSearch().show(getParentFragmentManager(), FragmentSearch.TAG);
                    collapseFab();
                    break;
                case R.id.fabReset:
                    fill(false);
                    collapseFab();
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onFastScrollStart() {
            fabAdd.setVisibility(View.GONE);
            fabFilter.setVisibility(View.GONE);
            fabSearch.setVisibility(View.GONE);
            fabReset.setVisibility(View.GONE);
        }

        @Override
        public void onFastScrollStop() {
            fabAdd.setVisibility(View.VISIBLE);
            fabFilter.setVisibility(View.VISIBLE);
            fabSearch.setVisibility(View.VISIBLE);
            fabReset.setVisibility(View.VISIBLE);
        }

    }

}
