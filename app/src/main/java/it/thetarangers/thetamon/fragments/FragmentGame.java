package it.thetarangers.thetamon.fragments;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import it.thetarangers.thetamon.R;
import it.thetarangers.thetamon.activities.MainActivity;
import it.thetarangers.thetamon.database.DaoThread;
import it.thetarangers.thetamon.database.PokemonDao;
import it.thetarangers.thetamon.database.PokemonDb;
import it.thetarangers.thetamon.model.Pokemon;
import it.thetarangers.thetamon.utilities.ImageManager;

public class FragmentGame extends Fragment {
    Pokemon pokemon;
    Bitmap bitmapNormal;
    Bitmap bitmapObscure;
    Holder holder;
    ImageManager imageManager = new ImageManager();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        startGame();
        holder = new Holder(view);
    }

    class Holder implements View.OnClickListener{
        final ImageView ivPokemon;
        final Button btnReveal;

        Holder(View fv) {
            ivPokemon = fv.findViewById(R.id.ivPokemon);
            btnReveal = fv.findViewById(R.id.btnReveal);

            btnReveal.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            startGame();
        }
    }

    public void startGame(){
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                holder.ivPokemon.setImageBitmap(bitmapObscure);
            }
        };
        Thread thread = new Thread(() -> {
            PokemonDao dao = PokemonDb.getInstance(getContext()).pokemonDao();
            pokemon = dao.getRandomPokemon();
            bitmapNormal = imageManager.loadFromDisk(
                    getContext().getFilesDir() + getContext().getString(R.string.sprites_front),
                    pokemon.getId() + getContext().getString(R.string.extension));

            bitmapObscure = darkenBitmap(bitmapNormal);
            handler.post(runnable);
        });

        thread.start();
    }

    private Bitmap darkenBitmap(Bitmap bitmap) {
        int row = bitmap.getHeight();
        int col = bitmap.getWidth();

        Bitmap darkenedBitmap = Bitmap.createBitmap(row, col, Bitmap.Config.ARGB_8888);

        for(int i = 0; i < row; i++) {
            for(int j = 0; j < col; j++){
                int pixel = bitmap.getPixel(i, j);

                if (pixel != Color.TRANSPARENT) {
                    darkenedBitmap.setPixel(i, j, Color.BLACK);
                }
            }
        }

        return darkenedBitmap;
    }
}
