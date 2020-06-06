package it.thetarangers.thetamon.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import it.thetarangers.thetamon.R;
import it.thetarangers.thetamon.database.DaoThread;
import it.thetarangers.thetamon.model.Pokemon;
import it.thetarangers.thetamon.utilities.FileDownloader;
import it.thetarangers.thetamon.utilities.FileUnzipper;
import it.thetarangers.thetamon.utilities.ImageManager;
import it.thetarangers.thetamon.utilities.VolleyPokemon;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean isFirstUse = sharedPreferences.getBoolean("FirstUse", true);

        ImageView ivAnim = findViewById(R.id.ivAnim);
        AnimationDrawable animation = (AnimationDrawable) ivAnim.getBackground();
        animation.start();

        /*if (!isFirstUse) {
            Log.d("POKE", "Bypassed Download");
            Intent intent = new Intent(MainActivity.this, PokedexActivity.class);
            startActivity(intent);
            finish();
            return;
        }*/

        final Handler h = new Handler();
        final Runnable update = () -> {
            //TODO replace with meaningfull text
            ((TextView) findViewById(R.id.tv_hello)).setText("Unzip Completed");
            findViewById(R.id.progressBar).setVisibility(View.GONE);
            editor.putBoolean("FirstUse", false);
            editor.apply();

            Intent intent = new Intent(MainActivity.this, PokedexActivity.class);
            startActivity(intent);
            finish();
        };

        final VolleyPokemon volley = new VolleyPokemon(MainActivity.this) {
            @Override
            public void fill(List<Pokemon> pokemonList) {
                Log.w("POKE", pokemonList.size() + "");

                final DaoThread daoThread = new DaoThread();
                avgColor(pokemonList);
                daoThread.fill(MainActivity.this, pokemonList, h, update);

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

                volley.getPokemonList();
            }
        };

        t.start();

    }

    private void unpack() {
        File file = new File(getApplicationContext().getExternalFilesDir(null),
                "Sprites/sprites.zip");
        FileUnzipper fu = new FileUnzipper();

        if (!fu.unzip(file, getApplicationContext().getFilesDir().getAbsolutePath())) {
            // TODO
        }

        try {
            Log.d("POKE", "Cleaning up");
            FileUtils.forceDelete(Objects.requireNonNull(getApplicationContext()
                    .getExternalFilesDir(null)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void avgColor(List<Pokemon> pokemons) {
        ImageManager imageManager = new ImageManager();

        for (int i = 0; i < pokemons.size(); i++) {
            Bitmap bitmap = imageManager.loadFromDisk(MainActivity.this.getFilesDir() +
                    "/sprites_front", pokemons.get(i).getId() + ".png");

            pokemons.get(i).setAverageColor(imageManager.getDesaturatedColor(bitmap, -1));
        }
    }

}
