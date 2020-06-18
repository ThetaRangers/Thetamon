package it.thetarangers.thetamon.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;

import it.thetarangers.thetamon.R;
import it.thetarangers.thetamon.activities.PokedexActivity;
import it.thetarangers.thetamon.activities.PokemonDetailActivity;
import it.thetarangers.thetamon.adapters.PokedexAdapter;
import it.thetarangers.thetamon.database.DaoThread;
import it.thetarangers.thetamon.listener.SelectorCallback;
import it.thetarangers.thetamon.model.Pokemon;
import it.thetarangers.thetamon.viewmodel.FavoriteListViewModel;

public class FragmentFavorites extends Fragment implements SelectorCallback, PokedexActivity.OnActivityResultCallback {
    Holder holder;
    FavoriteListViewModel favoriteListViewModel;
    DaoThread daoThread;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        holder = new Holder(view);

        favoriteListViewModel = new ViewModelProvider(requireActivity()).get(FavoriteListViewModel.class);
        favoriteListViewModel.getFavorites().observe(getViewLifecycleOwner(),
                holder.adapter::setPokemonList);

        daoThread = new DaoThread();

        daoThread.getFavoritePokemon(getContext(), favoriteListViewModel);
    }

    @Override
    public void onSelect(int size) {

    }

    @Override
    public void onActivityResultCallback(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PokedexAdapter.REQ_CODE) {
            if (data != null) {
                @SuppressWarnings("unchecked")
                HashMap<Integer, Boolean> pokemons = (HashMap<Integer, Boolean>) data
                        .getSerializableExtra(PokemonDetailActivity.POKEMONS);
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
            holder.adapter.setClickable(true);
            daoThread.getFavoritePokemon(getContext(), favoriteListViewModel);
        }

    }

    class Holder {
        final RecyclerView rvFavorites;
        final PokedexAdapter adapter;

        Holder(View view) {
            rvFavorites = view.findViewById(R.id.rvFavorites);

            rvFavorites.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new PokedexAdapter(getActivity(), FragmentFavorites.this);
            rvFavorites.setAdapter(adapter);
        }
    }
}
