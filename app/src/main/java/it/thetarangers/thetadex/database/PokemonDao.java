package it.thetarangers.thetadex.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import it.thetarangers.thetadex.model.Pokemon;
import it.thetarangers.thetadex.utilities.StringManager;

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
    abstract List<Pokemon> getPokemonsFromNameInterface(String name);

    public List<Pokemon> getPokemonsFromName(String name) {
        // Add special char % that is used to search part of name
        return getPokemonsFromNameInterface(StringManager.decapitalize(name) + "%");
    }

    @Query("SELECT * FROM Pokemon ORDER BY RANDOM() LIMIT 1;")
    public abstract Pokemon getRandomPokemon();

    @Query("SELECT * FROM Pokemon WHERE name LIKE :name ORDER BY LENGTH(name) LIMIT 1;")
    public abstract Pokemon getPokemonFromNameInterface(String name);

    public Pokemon getPokemonFromName(String name) {
        // Add special char % that is used to search part of name
        return getPokemonFromNameInterface(name + "%");
    }

    @Query("UPDATE Pokemon SET isFavorite = :fav WHERE id = :pid;")
    public abstract void setPokemonFav(int pid, boolean fav);

    @Query("SELECT * FROM Pokemon WHERE isFavorite = 1")
    public abstract List<Pokemon> getFavoritePokemons();
}
