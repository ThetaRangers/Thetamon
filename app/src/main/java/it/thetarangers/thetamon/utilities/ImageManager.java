package it.thetarangers.thetamon.utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

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

    public String getDesaturatedColor(Bitmap bitmap, float desaturation){
        int redColors = 0;
        int greenColors = 0;
        int blueColors = 0;
        int pixelCount = 0;

        for (int y = 0; y < bitmap.getHeight(); y++)
        {
            for (int x = 0; x < bitmap.getWidth(); x++)
            {
                int c = bitmap.getPixel(x, y);
                if (c != Color.TRANSPARENT) {
                    pixelCount++;
                    redColors += Color.red(c);
                    greenColors += Color.green(c);
                    blueColors += Color.blue(c);
                }
            }
        }

        int red = (redColors/pixelCount);
        int green = (greenColors/pixelCount);
        int blue = (blueColors/pixelCount);

        if (desaturation != -1) {
            float L = (float) (0.3 * red + 0.6 * green + 0.1 * blue);
            float new_r = red + desaturation * (L - red);
            float new_g = green + desaturation * (L - green);
            float new_b = blue + desaturation * (L - blue);

            int color = Color.rgb(new_r, new_g, new_b);
            return "#" + Integer.toHexString(color).substring(2);
        } else {
            return "#" + Integer.toHexString(Color.rgb(red, green, blue)).substring(2);
        }
    }
}
