package it.thetarangers.thetamon.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import it.thetarangers.thetamon.model.Pokemon;

@Dao
public interface PokemonDao {
    @Query("SELECT * FROM Pokemon")
    public List<Pokemon> getPokemons();

    @Insert
    public void insertPokemon(Pokemon pokemon);

    @Query("DELETE FROM Pokemon")
    public void deleteAll();

}
