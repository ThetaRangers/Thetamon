package it.thetarangers.thetamon.database;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.util.List;

import it.thetarangers.thetamon.model.Pokemon;
import it.thetarangers.thetamon.viewmodel.PokemonListViewModel;

public class DaoThread extends Thread {

    private Runnable runnable;
    private PokemonListViewModel pokemonListViewModel;

    public DaoThread() {
    }

    public DaoThread(PokemonListViewModel pokemonListViewModel) {
        this.pokemonListViewModel = pokemonListViewModel;
    }

    @Override
    public void run() {
        runnable.run();
    }

    public void fill(final Context context, final List<Pokemon> pokemons,
                     Handler handler, Runnable update) {
        runnable = () -> {
            PokemonDb db = PokemonDb.getInstance(context);
            final PokemonDao dao = db.pokemonDao();

            dao.deleteAll();
            for (int i = 0; i < pokemons.size(); i++) {
                dao.insertPokemon(pokemons.get(i));
            }

            Log.w("POKE", "Inserted " + dao.getPokemons().size() + " in the database");

            if (handler != null)
                handler.post(update);
        };
        this.start();
    }

    public void getPokemonFromName(final Context context, final String query) {

        runnable = () -> {
            PokemonDb db = PokemonDb.getInstance(context);
            PokemonDao dao = db.pokemonDao();

            List<Pokemon> tempList = dao.getPokemonsFromName(query);

            pokemonListViewModel.setPokemons(tempList);
        };

        this.start();
    }

    public void getPokemonFromId(final Context context, final int id) {

        runnable = () -> {
            PokemonDb db = PokemonDb.getInstance(context);
            PokemonDao dao = db.pokemonDao();

            List<Pokemon> tempList = dao.getPokemonFromId(id);
            Log.w("POKE", "Listona: " + tempList.size());

            pokemonListViewModel.setPokemons(tempList);
        };

        this.start();
    }

}
