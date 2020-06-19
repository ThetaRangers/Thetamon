package it.thetarangers.thetadex.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import it.thetarangers.thetadex.R;
import it.thetarangers.thetadex.model.Ability;
import it.thetarangers.thetadex.model.Move;
import it.thetarangers.thetadex.model.Pokemon;

@Database(entities = {Pokemon.class, Move.class, Ability.class}, version = 1, exportSchema = false)
public abstract class PokemonDb extends RoomDatabase {

    private static PokemonDb instance = null;

    protected PokemonDb() {

    }

    // Single access to database
    static public PokemonDb getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    PokemonDb.class, context.getString(R.string.db_name)).build();
        }
        return instance;
    }

    public abstract PokemonDao pokemonDao();

    public abstract MoveDao moveDao();
}
