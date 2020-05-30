package it.thetarangers.thetamon.database;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import it.thetarangers.thetamon.model.Pokemon;

@Database(entities = {Pokemon.class}, version = 1, exportSchema = false)
public abstract class PokemonDb extends RoomDatabase {

    private static PokemonDb instance = null;

    public abstract PokemonDao pokemonDao();

    protected PokemonDb(){

    }

    static public PokemonDb getInstance(Context context){
        if(instance == null){
            //TODO remove allowMainThreadQueries()
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    PokemonDb.class, "pokemonDb").allowMainThreadQueries().build();
        }
        return instance;
    }
}
