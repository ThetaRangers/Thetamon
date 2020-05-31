package it.thetarangers.thetamon.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import it.thetarangers.thetamon.model.Pokemon;

@Dao
public interface PokemonDao {
    @Query("SELECT * FROM Pokemon")
    List<Pokemon> getPokemons();

    @Insert
    void insertPokemon(Pokemon pokemon);

    @Query("DELETE FROM Pokemon")
    void deleteAll();

    @Query("SELECT DISTINCT id, name FROM Pokemon WHERE id = :pokemonId")
    Pokemon getPokemonFromId(int pokemonId);

    //TODO ricerca da parte di nome
    @Query("SELECT id, name FROM Pokemon WHERE name LIKE :name")
    List<Pokemon> getPokemonFromName(String name);
}
