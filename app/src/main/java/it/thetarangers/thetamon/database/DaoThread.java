package it.thetarangers.thetamon.database;

import android.content.Context;
import android.os.Handler;

import java.util.List;

import it.thetarangers.thetamon.model.Move;
import it.thetarangers.thetamon.model.Pokemon;
import it.thetarangers.thetamon.viewmodel.FavoriteListViewModel;
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

    public void savePokemon(final Context context, final Pokemon pokemon) {
        Runnable runnable = () -> {
            PokemonDb db = PokemonDb.getInstance(context);
            final PokemonDao dao = db.pokemonDao();

            dao.insertPokemon(pokemon);

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

    /*public void getMoveType(final Context context, List<Move> moves, Handler handler, Runnable update){
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
    }*/

    public void getMoveDetails(final Context context, List<Move> moves, Handler handler, Runnable update) {
        Runnable runnable = () -> {
            PokemonDb db = PokemonDb.getInstance(context);
            MoveDao dao = db.moveDao();
            for (int i = 0; i < moves.size(); i++) {
                Move temp = dao.getMoveDetails(moves.get(i).getName());
                moves.get(i).setType(temp.getType());
                moves.get(i).setDamageClass(temp.getDamageClass());
                moves.get(i).setId(temp.getId());
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

    public void getPokemonFromName(final Context context, final Pokemon pokemon, Handler handler, Runnable update) {

        Runnable runnable = () -> {
            PokemonDb db = PokemonDb.getInstance(context);
            PokemonDao dao = db.pokemonDao();

            Pokemon tmp = dao.getPokemonFromName(pokemon.getName());

            pokemon.setAverageColor(tmp.getAverageColor());
            pokemon.setName(tmp.getName());
            pokemon.setId(tmp.getId());
            pokemon.setUrl(tmp.getUrl());
            pokemon.setType1(tmp.getType1());
            pokemon.setType2(tmp.getType2());

            if (handler != null) {
                handler.post(update);
            }
        };

        new Thread(runnable).start();
    }

    public void setPokemonFav(final Context context, final Pokemon pokemon, final boolean fav) {

        Runnable runnable = () -> {
            PokemonDb db = PokemonDb.getInstance(context);
            PokemonDao dao = db.pokemonDao();

            dao.setPokemonFav(pokemon.getId(), fav);
        };

        new Thread(runnable).start();
    }

    public void getFavoritePokemon(final Context context, FavoriteListViewModel viewModel) {

        Runnable runnable = () -> {
            PokemonDb db = PokemonDb.getInstance(context);
            PokemonDao dao = db.pokemonDao();

            List<Pokemon> tempList = dao.getFavoritePokemons();

            viewModel.setFavoritesAsynchronous(tempList);
        };

        new Thread(runnable).start();
    }
}
