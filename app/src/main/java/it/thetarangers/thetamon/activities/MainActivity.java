package it.thetarangers.thetamon.activities;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import it.thetarangers.thetamon.utilities.FileUnzipper;
import it.thetarangers.thetamon.utilities.ImageManager;
import it.thetarangers.thetamon.utilities.PreferencesHandler;
import it.thetarangers.thetamon.utilities.VolleyPokemon;

public class MainActivity extends AppCompatActivity {

    Holder holder;
    Handler handler;
    Thread thread;
    ReentrantLock lock;
    DownloadManager manager;
    VolleyPokemon volley;
    long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if resources have to be downloaded
        if (!PreferencesHandler.isFirstUse(this)) {
            Intent intent = new Intent(MainActivity.this, PokedexActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        holder = new Holder();

        handler = new Handler();

        // Prepare for download
        manager = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadBroadcastReceiver receiver = new DownloadBroadcastReceiver();
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        // Prepare Volley
        volley = new MainActivityVolleyPokemon(MainActivity.this);

        lock = new ReentrantLock();
        lock.lock(); // Released after Download finishes

        thread = new Thread(new MainActivityRunnable());
        thread.start();

    }

    private void downloadFile(String URL, String path, String name) throws InterruptedException {
        // Blocking method
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(URL));
        request.setDescription(getString(R.string.app_name));
        request.setTitle(name);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setDestinationInExternalFilesDir(this, path, name);

        id = Objects.requireNonNull(manager).enqueue(request);

        // Wait until download completes
        lock.lock();
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException();
        }
    }

    private Boolean unpack() {
        // Prepare file to unzip
        File file = new File(getApplicationContext().getExternalFilesDir(null),
                String.format(Locale.getDefault(), "%s/%s",
                        getString(R.string.sprites_temp_path),
                        getString(R.string.sprites_archive)));
        FileUnzipper fu = new FileUnzipper();

        // Unzip it
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

            pokemons.get(i).setAverageColor(imageManager.getAverageColor(bitmap, 10));
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

    class DownloadBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            // Check if the completed download is the one requested by MainActivity
            if (reference == id) {
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(id);
                Cursor c = manager.query(query);
                if (c.moveToFirst()) {
                    int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    // Check download status
                    if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                        context.unregisterReceiver(this);
                        lock.unlock();
                    } else {
                        context.unregisterReceiver(this);
                        thread.interrupt();
                        lock.unlock();
                        Toast.makeText(MainActivity.this,
                                R.string.error_download, Toast.LENGTH_LONG).show();
                        MainActivity.this.finish();
                    }
                }
                c.close();
            }
        }
    }

    class MainActivityRunnable implements Runnable {
        @Override
        public void run() {

            // Remove resources already in internal memory
            cleanUpExternal();

            // Do the blocking operation
            try {
                downloadFile(getString(R.string.url_sprites),
                        getString(R.string.sprites_temp_path),
                        getString(R.string.sprites_archive));
            } catch (InterruptedException e) {
                return;
            }

            // Notify user when download is complete
            handler.post(() -> holder.setTvLoading(R.string.extracting));

            // Remove resources already in internal memory
            cleanUpInternal();

            // Extract resources in internal memory
            if (!unpack()) {
                handler.post(MainActivity.this::finishAndRemoveTask);
                return;
            }

            // Notify the user when the extraction is complete
            handler.post(() -> holder.setTvLoading(R.string.updating_db));

            // Get the list of Pokemons and complete thread execution
            volley.getPokemonList();
        }
    }

    class MainActivityVolleyPokemon extends VolleyPokemon {

        public MainActivityVolleyPokemon(Context context) {
            super(context);
        }

        @Override
        public void fill(List<Pokemon> pokemonList) {

            Runnable update = () -> {
                PreferencesHandler.setIsFirstUse(MainActivity.this, false);

                Intent intent = new Intent(MainActivity.this, PokedexActivity.class);
                startActivity(intent);
                finish();
            };

            // Let another thread calculate average color
            Thread t = new Thread(() -> {
                avgColor(pokemonList);
                DaoThread daoThread = new DaoThread();
                daoThread.fill(MainActivity.this, pokemonList, handler, update);
            });
            t.start();

        }
    }

}
