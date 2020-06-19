package it.thetarangers.thetamon.fragments;

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

import java.util.List;

import it.thetarangers.thetamon.R;
import it.thetarangers.thetamon.activities.PokedexActivity;
import it.thetarangers.thetamon.adapters.PokedexAdapter;
import it.thetarangers.thetamon.database.DaoThread;
import it.thetarangers.thetamon.favorites.FavoritesManager;
import it.thetarangers.thetamon.listener.SelectorCallback;
import it.thetarangers.thetamon.model.Pokemon;
import it.thetarangers.thetamon.viewmodel.FavoriteListViewModel;
import it.thetarangers.thetamon.viewmodel.PokemonListViewModel;

public class FragmentFavorites extends Fragment implements SelectorCallback {
    private Holder holder;
    private FavoriteListViewModel favoriteListViewModel;
    private PokemonListViewModel pokemonListViewModel;
    private DaoThread daoThread;
    private ActionMode actionMode = null;

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

        pokemonListViewModel = new ViewModelProvider(requireActivity()).get(PokemonListViewModel.class);

        favoriteListViewModel = new ViewModelProvider(requireActivity()).get(FavoriteListViewModel.class);
        favoriteListViewModel.getFavorites().observe(getViewLifecycleOwner(),
                holder.adapter::setFavoriteList);

        daoThread = new DaoThread();

        daoThread.getFavoritePokemon(getContext(), favoriteListViewModel);
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
        actionMode = requireActivity().startActionMode(new FragmentFavorites.FavoritesCallback());

    }

    @Override
    public void onResume() {
        //if fragment is resumed reload favorites
        daoThread.getFavoritePokemon(getContext(), favoriteListViewModel);
        holder.adapter.setClickable(true);
        super.onResume();
    }

    @Override
    public void onDetach() {
        //if fragment is detached notify fragment pokedex favorites changes
        notifyPokedex();
        super.onDetach();
    }

    //update pokemon list view model with favorite list changes
    private void notifyPokedex() {
        List<Pokemon> pokemons = pokemonListViewModel.getPokemonList();
        List<Pokemon> favorites = favoriteListViewModel.getFavoriteList();
        for (Pokemon pokemon : pokemons) {
            boolean fav = false;
            for (Pokemon favorite : favorites) {
                if (pokemon.getId() == favorite.getId()) {
                    fav = favorite.getFavorite();
                    break;
                }
            }
            pokemon.setFavorite(fav);
        }
        pokemonListViewModel.setPokemons(pokemons);
    }

    //inner class that implements the ActionMode.Callback for ActionMode managment
    class FavoritesCallback implements android.view.ActionMode.Callback {

        FavoritesManager favoritesManager = new FavoritesManager(requireContext());


        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            //inflate menu on action mode creation
            mode.getMenuInflater().inflate(R.menu.menu_action_mode, menu);
            ((PokedexActivity) requireActivity()).lockDrawer();
            MenuItem itemAdd = menu.getItem(0);
            itemAdd.setEnabled(false);
            mode.setTitle(getString(R.string.menu_start_title) + " 1" + getString(R.string.menu_end_title));
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        //Manage click on menu items
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem menuItem) {
            List<Pokemon> sel = holder.adapter.getSelected();
            switch (menuItem.getItemId()) {
                case R.id.item_deselect:
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
        public void onDestroyActionMode(ActionMode mode) {
            holder.adapter.deselectAll();
            ((PokedexActivity) requireActivity()).unlockDrawer();
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

            //set layout manager and adapter to recycler view
            rvFavorites.setLayoutManager(new GridLayoutManager(getContext(), columns));
            adapter = new PokedexAdapter(getActivity(), FragmentFavorites.this);
            rvFavorites.setAdapter(adapter);
        }
    }
}
