package it.thetarangers.thetamon.utilities;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;

import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

/*
 * A class used to download files exploiting Android DownloadManager.
 * The method downloadFile is blocking, thus not intended to be called from the UI thread.
 */
public abstract class FileDownloader extends BroadcastReceiver {

    Context context;
    Thread t;
    ReentrantLock lock;
    DownloadManager manager;
    long id;

    public FileDownloader(Context context, ReentrantLock lock) {
        this.context = context;
        t = Thread.currentThread();
        this.lock = lock;
        manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        context.registerReceiver(this,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    protected abstract void handleError();

    public synchronized void downloadFile(String URL,
                                          String path, String name) {

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(URL));
        request.setDescription("ThetaMon");
        request.setTitle(name);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setDestinationInExternalFilesDir(context, path, name);

        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        id = Objects.requireNonNull(manager).enqueue(request);

        /*
         * Sleeping until download completes.
         * While loop ensures that the interrupt is caused by onReceive.
         */
        lock.lock();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        if (reference == id) {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(id);
            Cursor c = manager.query(query);
            if (c.moveToFirst()) {
                int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                    lock.unlock();
                    context.unregisterReceiver(this);
                } else {
                    context.unregisterReceiver(this);
                    lock.unlock();
                    this.handleError();
                }
            }
            c.close();
        }
    }

}
