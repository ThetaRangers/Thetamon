package it.thetarangers.thetamon.database;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import it.thetarangers.thetamon.model.Pokemon;

public class DaoThread extends Thread {

    private Runnable runnable;
    private List<Pokemon> list;

    public DaoThread() {
        list = new ArrayList<>();
    }

    @Override
    public void run() {
        runnable.run();
    }

    public List<Pokemon> getList() {
        return list;
    }

    public void fill(final Context context, final List<Pokemon> pokemons, final Handler handler,
                     final Runnable update) {
        runnable = new Runnable() {
            @Override
            public void run() {
                PokemonDb db = PokemonDb.getInstance(context);
                final PokemonDao dao = db.pokemonDao();

                dao.deleteAll();
                for (int i = 0; i < pokemons.size(); i++) {
                    dao.insertPokemon(pokemons.get(i));
                }

                Log.w("POKE", "Inserted " + dao.getPokemons().size() + " in the database");

                if (handler != null)
                    handler.post(update);
            }
        };
        this.start();
    }

    public void getPokemons(final Context context, final Handler handler,
                                     final Runnable update) {
        runnable = new Runnable() {
            @Override
            public void run() {
                PokemonDb db = PokemonDb.getInstance(context);
                PokemonDao dao = db.pokemonDao();

                List<Pokemon> tempList = dao.getPokemons();

                list.addAll(tempList);

                Log.w("POKE", list.size() + "");

                if (handler != null)
                    handler.post(update);
            }
        };
        this.start();
    }

}
