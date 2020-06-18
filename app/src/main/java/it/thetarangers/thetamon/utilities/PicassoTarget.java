package it.thetarangers.thetamon.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;

public class PicassoTarget implements Target {
    private String url;
    private Context context;

    public PicassoTarget(String url, Context context) {
        this.url = url;
        this.context = context;
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        try {
            String pathname = context.getExternalFilesDir(null).getAbsolutePath() + url;
            Log.d("Roma", "Pathname is: " + pathname);
            File file = new File(pathname);

            FileOutputStream ostream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
            ostream.flush();
            ostream.close();
            Log.d("Roma", "Save complete");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }
}
