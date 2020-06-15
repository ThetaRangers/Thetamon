package it.thetarangers.thetamon.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import it.thetarangers.thetamon.model.Pokemon;
import it.thetarangers.thetamon.utilities.StringManager;

@Dao
public abstract class PokemonDao {
    @Query("SELECT * FROM Pokemon")
    public abstract List<Pokemon> getPokemons();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertPokemon(Pokemon pokemon);

    @Query("DELETE FROM Pokemon")
    public abstract void deleteAll();

    @Query("SELECT * FROM Pokemon WHERE id = :pokemonId")
    public abstract List<Pokemon> getPokemonFromId(int pokemonId);

    @Query("SELECT * FROM Pokemon WHERE name LIKE :name")
    abstract List<Pokemon> getPokemonFromNameInterface(String name);

    public List<Pokemon> getPokemonsFromName(String name){
        return getPokemonFromNameInterface(StringManager.decapitalize(name) + "%");
    }

    @Query("SELECT * FROM Pokemon ORDER BY RANDOM() LIMIT 1;")
    public abstract Pokemon getRandomPokemon();

    @Query("SELECT * FROM Pokemon WHERE name = :name")
    public abstract Pokemon getPokemonFromName(String name);
}
