package it.thetarangers.thetamon.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import it.thetarangers.thetamon.model.Pokemon;

@Database(entities = {Pokemon.class}, version = 1)
public abstract class PokemonDb extends RoomDatabase {

    private PokemonDb instance = null;

    private PokemonDb(){

    }

    public PokemonDb getInstance(Context context){
        if(instance == null){

            //TODO remove allowMainThreadQueries()
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    PokemonDb.class, "pokemonDb").allowMainThreadQueries().build();
        }
        return instance;
    }
}
