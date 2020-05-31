package it.thetarangers.thetamon.utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ImageManager {

    public Bitmap loadFromDisk (String path, String filename) {
        try {
            File f = new File(path, filename);
            return BitmapFactory.decodeStream(new BufferedInputStream(new FileInputStream(f)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
