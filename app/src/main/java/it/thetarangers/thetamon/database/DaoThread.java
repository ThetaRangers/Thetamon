package it.thetarangers.thetamon.database;

import android.content.Context;
import android.os.Handler;

import java.util.List;

import it.thetarangers.thetamon.model.Move;
import it.thetarangers.thetamon.model.Pokemon;
import it.thetarangers.thetamon.viewmodel.PokemonListViewModel;

public class DaoThread {

    private PokemonListViewModel pokemonListViewModel;

    public DaoThread() {
    }

    public DaoThread(PokemonListViewModel pokemonListViewModel) {
        this.pokemonListViewModel = pokemonListViewModel;
    }

    public void fill(final Context context, final List<Pokemon> pokemons,
                     Handler handler, Runnable update) {
        Runnable runnable = () -> {
            PokemonDb db = PokemonDb.getInstance(context);
            final PokemonDao dao = db.pokemonDao();

            for (int i = 0; i < pokemons.size(); i++) {
                dao.insertPokemon(pokemons.get(i));
            }

            if (handler != null)
                handler.post(update);
        };
        new Thread(runnable).start();
    }

    public void fillMoves(final Context context, final List<Move> moves,
                     Handler handler, Runnable update) {
        Runnable runnable = () -> {
            PokemonDb db = PokemonDb.getInstance(context);
            final MoveDao dao = db.moveDao();

            dao.insertAll(moves);

            if (handler != null)
                handler.post(update);
        };
        new Thread(runnable).start();
    }

    public void getMoveType(final Context context, List<Move> moves, Handler handler, Runnable update){
        Runnable runnable = () -> {
            PokemonDb db = PokemonDb.getInstance(context);
            MoveDao dao = db.moveDao();
            for (int i = 0; i < moves.size(); i++) {
                String temp = dao.getMoveType(moves.get(i).getName());
                moves.get(i).setType(temp);
            }

            if (handler != null) {
                handler.post(update);
            }

            };

        new Thread(runnable).start();
    }

    public void getPokemonFromName(final Context context, final String query) {

        Runnable runnable = () -> {
            PokemonDb db = PokemonDb.getInstance(context);
            PokemonDao dao = db.pokemonDao();

            List<Pokemon> tempList = dao.getPokemonsFromName(query);

            pokemonListViewModel.setPokemonsAsynchronous(tempList);
        };

        new Thread(runnable).start();
    }

    public void getPokemonFromId(final Context context, final int id) {

        Runnable runnable = () -> {
            PokemonDb db = PokemonDb.getInstance(context);
            PokemonDao dao = db.pokemonDao();

            List<Pokemon> tempList = dao.getPokemonFromId(id);

            pokemonListViewModel.setPokemonsAsynchronous(tempList);
        };

        new Thread(runnable).start();
    }

}
