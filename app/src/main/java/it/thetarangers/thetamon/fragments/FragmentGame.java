package it.thetarangers.thetamon.fragments;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import it.thetarangers.thetamon.R;
import it.thetarangers.thetamon.database.PokemonDao;
import it.thetarangers.thetamon.database.PokemonDb;
import it.thetarangers.thetamon.model.Pokemon;
import it.thetarangers.thetamon.utilities.ImageManager;
import it.thetarangers.thetamon.utilities.StringManager;

public class FragmentGame extends Fragment {
    Pokemon pokemon;
    Bitmap bitmapNormal;
    Bitmap bitmapObscure;
    Holder holder;
    Thread t;
    ImageManager imageManager = new ImageManager();
    MediaPlayer mp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        holder = new Holder(view);
        mp = MediaPlayer.create(getContext(), R.raw.game);
        mp.setVolume((float)1.5,(float)1.5);
        startGame();
    }

    private void tryName(String name) {

        Log.d("POKE", "searched " + holder.tilPokemonName.getEditText().getText());
        if(pokemon.getName().equals(StringManager.decapitalize(name))){
            Log.d("POKE","correct");
            holder.tilPokemonName.setError(null);
            holder.ivPokemon.setImageBitmap(bitmapNormal);
            holder.btnConfirm.setEnabled(false);
            Objects.requireNonNull(holder.tilPokemonName.getEditText()).setEnabled(false);
            Toast.makeText(getContext(),"It' s correct",Toast.LENGTH_LONG).show();

        }else{
            Log.d("POKE", "you're wrong");
            holder.tilPokemonName.setError("OPS you' re wrong");
        }


    }


    private void startGame() {
        mp.start();
        Objects.requireNonNull(holder.tilPokemonName.getEditText()).clearFocus();
        Objects.requireNonNull(holder.tilPokemonName.getEditText()).getText().clear();
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
            Log.d("POKE", "poke :" + pokemon.getName());
            bitmapNormal = imageManager.loadFromDisk(
                    getContext().getFilesDir() + getContext().getString(R.string.sprites_front),
                    pokemon.getId() + getContext().getString(R.string.extension));

            bitmapObscure = holder.darkenBitmap(bitmapNormal);
            handler.post(runnable);
        });

        thread.start();
    }


    class Holder implements View.OnClickListener, EditText.OnEditorActionListener {
        final ImageView ivPokemon;
        final Button btnConfirm;
        final Button btnNext;
        final TextInputLayout tilPokemonName;

        Holder(View fv) {
            ivPokemon = fv.findViewById(R.id.ivPokemon);
            btnNext = fv.findViewById(R.id.btnNext);
            btnNext.setOnClickListener(this);

            btnConfirm = fv.findViewById(R.id.btnConfirm);
            btnConfirm.setOnClickListener(this);

            tilPokemonName = fv.findViewById(R.id.tilPokemonName);
            Objects.requireNonNull(tilPokemonName.getEditText()).setOnEditorActionListener(this);
        }

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                Objects.requireNonNull(tilPokemonName.getEditText()).clearFocus();
                tryName(tilPokemonName.getEditText().getText().toString());
            }
            return false;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnNext:
                    btnConfirm.setEnabled(true);
                    tilPokemonName.setError(null);
                    Objects.requireNonNull(holder.tilPokemonName.getEditText()).setEnabled(true);
                    startGame();
                    break;
                case R.id.btnConfirm:
                    Objects.requireNonNull(tilPokemonName.getEditText())
                            .onEditorAction(EditorInfo.IME_ACTION_DONE);
                    break;
                default:
                    break;
            }
        }


        public Bitmap darkenBitmap(Bitmap bitmap) {
            int row = bitmap.getHeight();
            int col = bitmap.getWidth();

            Bitmap darkenedBitmap = Bitmap.createBitmap(row, col, Bitmap.Config.ARGB_8888);

            for (int i = 0; i < row; i++) {
                for (int j = 0; j < col; j++) {
                    int pixel = bitmap.getPixel(i, j);

                    if (pixel != Color.TRANSPARENT) {
                        darkenedBitmap.setPixel(i, j, Color.BLACK);
                    }
                }
            }

            return darkenedBitmap;
        }
    }
}
