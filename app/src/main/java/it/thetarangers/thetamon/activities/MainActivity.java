package it.thetarangers.thetamon.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import it.thetarangers.thetamon.R;
import it.thetarangers.thetamon.database.PokemonDao;
import it.thetarangers.thetamon.database.PokemonDb;
import it.thetarangers.thetamon.model.Pokemon;
import it.thetarangers.thetamon.utilities.FileDownloader;
import it.thetarangers.thetamon.utilities.FileUnzipper;
import it.thetarangers.thetamon.utilities.VolleyPokemon;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        Boolean isFirstUse = sharedPreferences.getBoolean("FirstUse", true);

        if (!isFirstUse) {
            Log.d("POKE", "Bypassed Download");
            Intent intent = new Intent(MainActivity.this, PokedexActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        final Handler h = new Handler();
        final Runnable update = new Runnable() {
            @Override
            public void run() {
                ((TextView) findViewById(R.id.tv_hello)).setText("Unzip Completed");
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                editor.putBoolean("FirstUse", false);
                editor.apply();

                Intent intent = new Intent(MainActivity.this, PokedexActivity.class);
                startActivity(intent);
                finish();
            }
        };

        final Thread t = new Thread() {
            @Override
            public void run() {
                FileDownloader fd = new FileDownloader(MainActivity.this);

                fd.downloadFile(// TODO static strings
                        "https://github.com/ThetaRangers/Thetamon/blob/master/sprites.zip?raw=true",
                        "Sprites", "sprites.zip");
                unpack();
                h.post(update);
            }
        };

        VolleyPokemon volley = new VolleyPokemon(MainActivity.this) {
            @Override
            public void fill(List<Pokemon> pokemonList) {
                Log.w("POKE", pokemonList.size() + "");

                final List<Pokemon> pokemons = pokemonList;
                final Thread tDao = new Thread(){
                    @Override
                    public void run() {
                        PokemonDb db = PokemonDb.getInstance(MainActivity.this.getApplicationContext());
                        final PokemonDao dao = db.pokemonDao();

                        dao.deleteAll();
                        for(int i = 0; i < pokemons.size(); i++){
                            dao.insertPokemon(pokemons.get(i));
                        }

                        Log.w("POKE", "Inserted " + dao.getPokemons().size() + " in the database");
                        t.start();
                    }
                };

                tDao.start();
            }
        };

        volley.getPokemonList();

    }

    private void unpack() {
        File file = new File(getApplicationContext().getExternalFilesDir(null),
                "Sprites/sprites.zip");
        FileUnzipper fu = new FileUnzipper();
        fu.unzip(file, getApplicationContext().getFilesDir().getAbsolutePath());
        try {
            Log.d("POKE", "Cleaning up");
            FileUtils.forceDelete(Objects.requireNonNull(getApplicationContext()
                    .getExternalFilesDir(null)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
