package it.thetarangers.thetamon.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import it.thetarangers.thetamon.R;
import it.thetarangers.thetamon.database.PokemonDao;
import it.thetarangers.thetamon.database.PokemonDb;
import it.thetarangers.thetamon.model.Pokemon;
import it.thetarangers.thetamon.utilities.FileDownloader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                ((TextView) findViewById(R.id.tv_hello)).setText("Download Completed");
            }
        };

        registerReceiver(onComplete,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        FileDownloader fd = new FileDownloader();

        fd.downloadFile(getApplicationContext(),
                "https://github.com/ThetaRangers/Thetamon/blob/master/sprites.zip?raw=true",
                "Sprites", "sprites.zip");

        PokemonDb db = PokemonDb.getInstance(this.getApplicationContext());

        PokemonDao dao = db.pokemonDao();


        dao.deleteAll();
        dao.insertPokemon(new Pokemon(1, "charmander"));

        Log.w("POKE", dao.getPokemons().get(0).name);
    }

}
