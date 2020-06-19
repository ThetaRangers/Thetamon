package it.thetarangers.thetadex.favorites;

import android.content.Context;

import java.util.List;

import it.thetarangers.thetadex.database.DaoThread;
import it.thetarangers.thetadex.model.EvolutionDetail;
import it.thetarangers.thetadex.model.Pokemon;
import it.thetarangers.thetadex.utilities.VolleyEvolutionChain;
import it.thetarangers.thetadex.utilities.VolleyPokemonDetail;

public class FavoritesManager {

    Context context;
    DaoThread daoThread;

    public FavoritesManager(Context context) {
        this.context = context;
        this.daoThread = new DaoThread();
    }

    public void addPokemonToFav(Pokemon pokemon) {

        // Set true to fav
        pokemon.setFavorite(true);

        // Download details if not already in db
        if (pokemon.getMovesList() == null) {

            VolleyEvolutionChain volleyEvolutionChain = new VolleyEvolutionChain(context) {
                @Override
                public void fill(EvolutionDetail evolutionDetail) {
                    pokemon.setEvolutionChain(evolutionDetail.toJSON());
                    DaoThread thread = new DaoThread();

                    // Save pokemon when the API is called
                    thread.savePokemon(context, pokemon);
                }
            };

            VolleyPokemonDetail vm = new VolleyPokemonDetail(context, pokemon) {
                @Override
                public void fill(Pokemon pokemonDetail) {
                    pokemon.setAll(pokemonDetail);
                    pokemon.encode();
                    volleyEvolutionChain.getEvolutionChain(pokemon.getUrlEvolutionChain());
                }
            };

            vm.getPokemonDetail();
        } else {
            daoThread.setPokemonFav(context, pokemon, true);
        }
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
