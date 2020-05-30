package it.thetarangers.thetamon.utilities;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;

import java.util.Objects;

public class FileDownloader {

    public void downloadFile(Context context, String URL,
                             String path, String name) {

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(URL));
        request.setDescription("ThetaMon");   //appears the same in Notification bar while downloading
        request.setTitle(name);
        request.setVisibleInDownloadsUi(false);

        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setDestinationInExternalFilesDir(context, path, name);

        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Objects.requireNonNull(manager).enqueue(request);
    }

}
