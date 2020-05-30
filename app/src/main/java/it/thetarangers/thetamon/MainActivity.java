package it.thetarangers.thetamon;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import it.thetarangers.thetamon.database.PokemonDao;
import it.thetarangers.thetamon.database.PokemonDb;
import it.thetarangers.thetamon.model.Pokemon;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PokemonDb db = PokemonDb.getInstance(this.getApplicationContext());

        PokemonDao dao = db.pokemonDao();


        dao.deleteAll();
        dao.insertPokemon(new Pokemon(1, "charmander"));

        Log.w("POKE", dao.getPokemons().get(0).name);
    }
}
