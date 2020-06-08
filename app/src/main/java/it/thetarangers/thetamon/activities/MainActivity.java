package it.thetarangers.thetamon.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

import it.thetarangers.thetamon.R;
import it.thetarangers.thetamon.database.DaoThread;
import it.thetarangers.thetamon.model.Pokemon;
import it.thetarangers.thetamon.utilities.FileDownloader;
import it.thetarangers.thetamon.utilities.FileUnzipper;
import it.thetarangers.thetamon.utilities.ImageManager;
import it.thetarangers.thetamon.utilities.VolleyPokemon;

public class MainActivity extends AppCompatActivity {

    Holder holder;
    Handler handler;
    FileDownloader fd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean isFirstUse = sharedPreferences.getBoolean("FirstUse", true);

        if (!isFirstUse) {
            Log.d("POKE", "Bypassed Download");
            Intent intent = new Intent(MainActivity.this, PokedexActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        holder = new Holder();

        handler = new Handler();
        final Runnable update = () -> {
            editor.putBoolean("FirstUse", false);
            editor.apply();

            Intent intent = new Intent(MainActivity.this, PokedexActivity.class);
            startActivity(intent);
            finish();
        };

        final VolleyPokemon volley = new VolleyPokemon(MainActivity.this) {
            @Override
            public void fill(List<Pokemon> pokemonList) {

                Thread t = new Thread() {
                    @Override
                    public void run() {
                        avgColor(pokemonList);
                        DaoThread daoThread = new DaoThread();
                        daoThread.fill(MainActivity.this, pokemonList, handler, update);
                    }
                };

                t.start();

            }
        };

        final Thread thread = new Thread() {
            @Override
            public void run() {
                cleanUpExternal();
                // Blocking operation
                try {
                    fd.downloadFile(getString(R.string.url_sprites),
                            getString(R.string.sprites_temp_path),
                            getString(R.string.sprites_archive));
                } catch (InterruptedException e) {
                    return;
                }

                handler.post(() -> holder.setTvLoading(R.string.extracting));

                if (!unpack()) {
                    handler.post(() -> finishAndRemoveTask());
                    return;
                }

                handler.post(() -> holder.setTvLoading(R.string.updating_db));

                volley.getPokemonList();
            }
        };


        ReentrantLock lock = new ReentrantLock();
        lock.lock();

        fd = new FileDownloader(MainActivity.this, lock) {
            @Override
            protected void handleError() {
                Toast.makeText(MainActivity.this,
                        R.string.error_download, Toast.LENGTH_LONG).show();
                MainActivity.this.finish();
            }
        };

        thread.start();

    }

    private Boolean unpack() {
        File file = new File(getApplicationContext().getExternalFilesDir(null),
                String.format(Locale.getDefault(), "%s/%s",
                        getString(R.string.sprites_temp_path),
                        getString(R.string.sprites_archive)));
        FileUnzipper fu = new FileUnzipper();

        // Remove resources already in internal memory
        cleanUpInternal();

        if (!fu.unzip(file, getApplicationContext().getFilesDir().getAbsolutePath())) {
            handler.post(() -> Toast.makeText(this, getString(R.string.error_unzip),
                    Toast.LENGTH_LONG).show());
            cleanUpExternal();
            return false;
        }

        cleanUpExternal();
        return true;
    }

    private void cleanUpExternal() {
        try {
            FileUtils.forceDelete(Objects.requireNonNull(getApplicationContext()
                    .getExternalFilesDir(null)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cleanUpInternal() {
        File file = new File(getApplicationContext()
                .getFilesDir(), getString(R.string.sprites_front));
        if (file.exists()) {
            try {
                FileUtils.forceDelete(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void avgColor(List<Pokemon> pokemons) {
        ImageManager imageManager = new ImageManager();

        for (int i = 0; i < pokemons.size(); i++) {
            Bitmap bitmap = imageManager.loadFromDisk(MainActivity.this.getFilesDir() +
                    getString(R.string.sprites_front), pokemons.get(i).getId() +
                    getString(R.string.extension));

            pokemons.get(i).setAverageColor(imageManager.getAverageColor(bitmap, 5));
        }
    }

    class Holder {

        ImageView ivAnim;
        TextView tvLoading;

        Holder() {
            ivAnim = findViewById(R.id.ivAnim);
            AnimationDrawable animation = (AnimationDrawable) ivAnim.getBackground();
            animation.start();

            tvLoading = findViewById(R.id.tvLoading);
        }

        public void setTvLoading(int resId) {
            tvLoading.setText(resId);
        }
    }

}
