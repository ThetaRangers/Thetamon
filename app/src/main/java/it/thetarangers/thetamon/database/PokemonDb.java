package it.thetarangers.thetamon.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import it.thetarangers.thetamon.model.Ability;
import it.thetarangers.thetamon.model.Move;
import it.thetarangers.thetamon.model.Pokemon;

@Database(entities = {Pokemon.class, Move.class, Ability.class}, version = 1, exportSchema = false)
public abstract class PokemonDb extends RoomDatabase {

    private static PokemonDb instance = null;

    protected PokemonDb() {

    }

    static public PokemonDb getInstance(Context context) {
        if (instance == null) {
            //TODO remove allowMainThreadQueries()
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    PokemonDb.class, "pokemonDb").build();
        }
        return instance;
    }

    public abstract PokemonDao pokemonDao();

    public abstract MoveDao moveDao();
}
