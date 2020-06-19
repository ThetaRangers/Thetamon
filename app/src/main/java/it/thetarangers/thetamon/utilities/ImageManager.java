package it.thetarangers.thetamon.utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ImageManager {

    public Bitmap loadFromDisk(String path, String filename) {

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

    public Bitmap loadBitmap(File file) {

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
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

    public String getAverageColor(Bitmap bitmap, int rate) {
        int redColors = 0;
        int greenColors = 0;
        int blueColors = 0;
        int pixelCount = 0;

        for (int y = 0; y < bitmap.getHeight(); y += rate) {
            for (int x = 0; x < bitmap.getWidth(); x += rate) {
                int c = bitmap.getPixel(x, y);
                if (c != Color.TRANSPARENT) {
                    pixelCount++;
                    redColors += Color.red(c);
                    greenColors += Color.green(c);
                    blueColors += Color.blue(c);
                }
            }
        }

        int red = (redColors / pixelCount);
        int green = (greenColors / pixelCount);
        int blue = (blueColors / pixelCount);

        return "#" + Integer.toHexString(Color.rgb(red, green, blue)).substring(2);

    }
}
