package it.thetarangers.thetamon.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.simplecityapps.recyclerview_fastscroll.interfaces.OnFastScrollStateChangeListener;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.List;
import java.util.Objects;

import it.thetarangers.thetamon.R;
import it.thetarangers.thetamon.adapter.FilterAdapter;
import it.thetarangers.thetamon.adapter.PokedexAdapter;
import it.thetarangers.thetamon.database.DaoThread;
import it.thetarangers.thetamon.model.Pokemon;
import it.thetarangers.thetamon.viewmodel.PokemonListViewModel;

public class FragmentPokedex extends Fragment {

    private PokemonListViewModel pokemonListViewModel;
    private Holder holder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pokedex, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pokemonListViewModel = new ViewModelProvider(requireActivity()).get(PokemonListViewModel.class);
        pokemonListViewModel.getPokemons().observe(getViewLifecycleOwner(),
                pokemons -> holder.typeAdapter.setPokemonList(pokemons));   // Observe the LiveData

        pokemonListViewModel.getFilteredPokemons().observe(getViewLifecycleOwner(),
                pokemons -> holder.adapter.setPokemonList(pokemons));   // Observe the LiveData

        holder = new Holder(view);
        if(savedInstanceState != null) {
            String type1 = savedInstanceState.getString("type1");
            String type2 = savedInstanceState.getString("type2");

            if(type1 != null){
                holder.typeAdapter.setFilter(type1);
            }

            if(type2 != null){
                holder.typeAdapter.setFilter(type2);
            }
        }

        List<Pokemon> tmp = pokemonListViewModel.getPokemonList();
        if (tmp == null)
            search(""); // Load all pokemons
    }

    @Override
    public void onStop() {
        pokemonListViewModel.setPokemonsSynchronous(holder.typeAdapter.getPokemonList());
        pokemonListViewModel.setFilterList(holder.adapter.getPokemonList());

        super.onStop();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        List<String> tmp = holder.typeAdapter.getCheckedTypes();

        switch (tmp.size()) {
            case 1:
                outState.putString("type1", tmp.get(0));
                break;
            case 2:
                outState.putString("type2", tmp.get(1));
                break;
            default:
                break;
        }

        super.onSaveInstanceState(outState);
    }

    private void search(String query) {
        DaoThread daoThread = new DaoThread(pokemonListViewModel);

        try {
            // If the searched string is an int search by id
            daoThread.getPokemonFromId(getContext(), Integer.parseInt(query));
        } catch (NumberFormatException e) {
            // Otherwise search by name
            daoThread.getPokemonFromName(getContext(), query);
        }
    }

    class Holder implements View.OnClickListener, EditText.OnEditorActionListener, OnFastScrollStateChangeListener {
        final FastScrollRecyclerView rvPokedex;
        final FloatingActionButton fabSearch;
        final ImageView ivClose;
        final TextInputLayout tilSearch;
        final ImageView ivSearch;
        final RecyclerView rvType;
        final PokedexAdapter adapter;
        final FilterAdapter typeAdapter;

        Holder(View fp) {

            fabSearch = fp.findViewById(R.id.fabSearch);
            fabSearch.setOnClickListener(this);

            ivClose = fp.findViewById(R.id.ivClose);
            ivClose.setOnClickListener(this);

            rvPokedex = fp.findViewById(R.id.rvPokedex);
            rvPokedex.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new PokedexAdapter(getContext());
            adapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);

            rvPokedex.setAdapter(adapter);
            rvPokedex.setOnFastScrollStateChangeListener(this);

            tilSearch = fp.findViewById(R.id.tilSearch);
            Objects.requireNonNull(tilSearch.getEditText()).setOnEditorActionListener(this);

            ivSearch = fp.findViewById(R.id.ivSearch);
            ivSearch.setOnClickListener(this);

            rvType = fp.findViewById(R.id.rvType);
            typeAdapter = new FilterAdapter(getContext(), pokemonListViewModel);
            rvType.setLayoutManager(new GridLayoutManager(getContext(), 4));
            rvType.setAdapter(typeAdapter);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.fabSearch) {
                fabSearch.setExpanded(true);
            } else if (v.getId() == R.id.ivClose) {
                fabSearch.setExpanded(false);
            } else if (v.getId() == R.id.ivSearch) {
                Objects.requireNonNull(tilSearch.getEditText()).onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        }

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            search(Objects.requireNonNull(tilSearch.getEditText()).getText().toString());

            fabSearch.setExpanded(false);
            return false;
        }

        @Override
        public void onFastScrollStart() {
            fabSearch.setVisibility(View.GONE);
        }

        @Override
        public void onFastScrollStop() {
            fabSearch.setVisibility(View.VISIBLE);
        }
    }
}
