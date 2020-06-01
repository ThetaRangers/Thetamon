package it.thetarangers.thetamon.utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ImageManager {

    public Bitmap loadFromDisk (String path, String filename) {

        File f = new File(path, filename);
        try (FileInputStream fileInputStream = new FileInputStream(f)) {
            try (BufferedInputStream bufferedInputStream =
                         new BufferedInputStream(fileInputStream)) {
                return BitmapFactory.decodeStream(bufferedInputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
