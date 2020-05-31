package it.thetarangers.thetamon.utilities;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import java.util.Objects;

/*
 * A class used to download files exploiting Android DownloadManager.
 * The method downloadFile is blocking, thus not intended to be called from the UI thread.
 */
public class FileDownloader extends BroadcastReceiver {

    Context context;
    Thread t;
    Boolean check;
    DownloadManager manager;
    long id;

    public FileDownloader(Context context) {
        this.context = context;
        t = Thread.currentThread();
        check = true;
        manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        context.registerReceiver(this,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    public synchronized void downloadFile(String URL,
                             String path, String name) {

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(URL));
        request.setDescription("ThetaMon");
        request.setTitle(name);
        request.setVisibleInDownloadsUi(false);

        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setDestinationInExternalFilesDir(context, path, name);

        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        id = Objects.requireNonNull(manager).enqueue(request);

        /*
         * Sleeping until download completes.
         * While loop ensures that the interrupt is caused by onReceive.
         */
        while (check) {
            try {
                Thread.sleep(Long.MAX_VALUE);
            } catch (InterruptedException ignored) { }
        }
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
                    check = false;
                    t.interrupt();
                    context.unregisterReceiver(this);
                } else {
                    // TODO
                }
            }
        }
    }

}
