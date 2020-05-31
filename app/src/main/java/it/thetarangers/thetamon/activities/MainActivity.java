package it.thetarangers.thetamon.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

        final Handler h = new Handler();
        final Runnable update = new Runnable() {
            @Override
            public void run() {
                ((TextView) findViewById(R.id.tv_hello)).setText("Unzip Completed");
                findViewById(R.id.progressBar).setVisibility(View.GONE);
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

        t.start();

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
                        h.post(update);
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
