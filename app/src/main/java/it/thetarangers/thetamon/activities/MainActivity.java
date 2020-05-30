package it.thetarangers.thetamon.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import it.thetarangers.thetamon.R;
import it.thetarangers.thetamon.database.PokemonDao;
import it.thetarangers.thetamon.database.PokemonDb;
import it.thetarangers.thetamon.model.Pokemon;
import it.thetarangers.thetamon.utilities.FileDownloader;
import it.thetarangers.thetamon.utilities.FileUnzipper;

public class MainActivity extends AppCompatActivity {

    BroadcastReceiver onComplete;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO TODO TODO refactor and handle orientation change (ffs)
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Handler h = new Handler();
        final Runnable update = new Runnable() {
            @Override
            public void run() {
                ((TextView) findViewById(R.id.tv_hello)).setText("Unzip Completed");
                ((ProgressBar) findViewById(R.id.progressBar)).setVisibility(View.GONE);
            }
        };
        final Thread t = new Thread() {
            @Override
            public void run() {
                unpack();
                h.post(update);
            }
        };

        onComplete = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                ((TextView) findViewById(R.id.tv_hello)).setText("Download Completed");
                // TODO calculate and check md5
                t.start();
            }
        };

        registerReceiver(onComplete,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        FileDownloader fd = new FileDownloader();

        fd.downloadFile(getApplicationContext(), // TODO static strings
                "https://github.com/ThetaRangers/Thetamon/blob/master/sprites.zip?raw=true",
                "Sprites", "sprites.zip");

        PokemonDb db = PokemonDb.getInstance(this.getApplicationContext());

        PokemonDao dao = db.pokemonDao();


        dao.deleteAll();
        dao.insertPokemon(new Pokemon(1, "charmander"));

        Log.w("POKE", dao.getPokemons().get(0).name);
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

    @Override
    public void onDestroy() {
        unregisterReceiver(onComplete);
        super.onDestroy();
    }

}
