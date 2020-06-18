package it.thetarangers.thetamon.fragments;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;

import it.thetarangers.thetamon.R;
import it.thetarangers.thetamon.activities.PokedexActivity;
import it.thetarangers.thetamon.activities.PokemonDetailActivity;
import it.thetarangers.thetamon.adapters.PokedexAdapter;
import it.thetarangers.thetamon.database.DaoThread;
import it.thetarangers.thetamon.favorites.FavoritesManager;
import it.thetarangers.thetamon.listener.SelectorCallback;
import it.thetarangers.thetamon.model.Pokemon;
import it.thetarangers.thetamon.viewmodel.FavoriteListViewModel;
import android.view.ActionMode;

public class FragmentFavorites extends Fragment implements SelectorCallback, PokedexActivity.OnActivityResultCallback {
    Holder holder;
    FavoriteListViewModel favoriteListViewModel;
    DaoThread daoThread;
    ActionMode actionMode = null;

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
        if(actionMode != null){
            if(size == 0)
                actionMode.finish();
            else
                actionMode.setTitle(getString(R.string.menu_start_title)+" "+size + getString(R.string.menu_end_title));
            return;
        }

        actionMode = requireActivity().startActionMode(new FragmentFavorites.FavoritesCallback());

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

    class FavoritesCallback implements android.view.ActionMode.Callback{

        FavoritesManager favoritesManager = new FavoritesManager(requireContext());

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu){
            mode.getMenuInflater().inflate(R.menu.menu_action_mode, menu);
            ((PokedexActivity)requireActivity()).lockDrawer();
            //TODO hardcoded string
            MenuItem itemAdd = menu.getItem(0);
            itemAdd.setEnabled(false);
            mode.setTitle(getString(R.string.menu_start_title)+ " 1" + getString(R.string.menu_end_title));
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu){
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem menuItem){
            List<Pokemon> sel = holder.adapter.getSelected();
            switch(menuItem.getItemId()){
                case R.id.item_deselect:
                    mode.finish();
                    break;

                case R.id.item_removeAll:
                    favoritesManager.removePokemonFromFav(sel);
                    mode.finish();
                    //TODO optional reload recycler view

                    break;
                default:
                    break;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode){
            holder.adapter.deselectAll();
            ((PokedexActivity)requireActivity()).unlockDrawer();
            actionMode = null;
        }
    }

    class Holder {
        final RecyclerView rvFavorites;
        final PokedexAdapter adapter;

        Holder(View view) {
            rvFavorites = view.findViewById(R.id.rvFavorites);


            int orientation = getResources().getConfiguration().orientation;
            int columns;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                // In landscape
                columns = 2;
            } else {
                // In portrait
                columns = 1;
            }

            rvFavorites.setLayoutManager(new GridLayoutManager(getContext(), columns));
            adapter = new PokedexAdapter(getActivity(), FragmentFavorites.this);
            rvFavorites.setAdapter(adapter);
        }
    }
}
