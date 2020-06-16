package it.thetarangers.thetamon.favorites;

import android.content.Context;
import android.util.Log;

import java.util.List;

import it.thetarangers.thetamon.database.DaoThread;
import it.thetarangers.thetamon.model.Pokemon;

public class FavoritesManager {

    Context context;
    DaoThread daoThread;

    public FavoritesManager(Context context) {
        this.context = context;
        this.daoThread = new DaoThread();
    }

    public void addPokemonToFav(Pokemon pokemon) {

        // TODO check if data are present and if its in fav
        // and if not download and set all the data

        // set true to fav
        pokemon.setFavorite(true);
        daoThread.setPokemonFav(context, pokemon, true);
    }

    public void addPokemonToFav(List<Pokemon> pokemons) {
        for (Pokemon pokemon : pokemons)
            this.addPokemonToFav(pokemon);
    }

    public void removePokemonFromFav(Pokemon pokemon) {
        pokemon.setFavorite(false);
        daoThread.setPokemonFav(context, pokemon, false);
    }

    public void removePokemonFromFav(List<Pokemon> pokemons) {
        for (Pokemon pokemon : pokemons)
            this.removePokemonFromFav(pokemon);
    }


}
