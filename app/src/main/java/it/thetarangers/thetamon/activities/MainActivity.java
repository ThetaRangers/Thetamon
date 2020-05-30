package it.thetarangers.thetamon.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import it.thetarangers.thetamon.R;
import it.thetarangers.thetamon.utilities.FileDownloader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FileDownloader fd = new FileDownloader();

        fd.downloadFile(getApplicationContext(),
                "https://github.com/ThetaRangers/Thetamon/blob/master/sprites.zip?raw=true",
                "Sprites", "sprites.zip");
    }
}
