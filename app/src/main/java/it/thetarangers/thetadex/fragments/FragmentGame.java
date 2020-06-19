package it.thetarangers.thetadex.fragments;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
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

import it.thetarangers.thetadex.R;
import it.thetarangers.thetadex.database.PokemonDao;
import it.thetarangers.thetadex.database.PokemonDb;
import it.thetarangers.thetadex.model.Pokemon;
import it.thetarangers.thetadex.utilities.ImageManager;
import it.thetarangers.thetadex.utilities.PreferencesHandler;
import it.thetarangers.thetadex.utilities.StringManager;

public class FragmentGame extends Fragment {

    private static String POKEMON = "poke";
    private static String IS_CORRECT = "is_correct";

    Pokemon pokemon;
    Bitmap bitmapNormal;
    Bitmap bitmapObscure;
    Holder holder;
    ImageManager imageManager = new ImageManager();
    MediaPlayer mp;

    boolean isCorrect;

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

        // Start the title jingle
        mp = MediaPlayer.create(getContext(), R.raw.game);

        // Check if the volume is enabled
        if (PreferencesHandler.isVolumeOn(requireContext()))
            mp.setVolume(1.5f, 1.5f);
        else
            mp.setVolume(0f, 0f);

        if (savedInstanceState == null) {
            startGame();
        } else {
            pokemon = savedInstanceState.getParcelable(POKEMON);
            isCorrect = savedInstanceState.getBoolean(IS_CORRECT);
            loadImages(false);
            if (isCorrect) {
                Objects.requireNonNull(holder.tilPokemonName.getEditText())
                        .setText(StringManager.capitalize(pokemon.getName()));
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(POKEMON, pokemon);
        outState.putBoolean(IS_CORRECT, isCorrect);
        super.onSaveInstanceState(outState);
    }

    private void tryName(String name) {
        // Check if the inserted name is correct
        if (pokemon.getName().equals(StringManager.decapitalize(name))) {
            // Reveal the pokemon
            holder.win();
            isCorrect = true;
            Toast.makeText(getContext(), getString(R.string.game_correct), Toast.LENGTH_LONG).show();
            holder.btnReveal.setEnabled(false);
        } else {
            holder.tilPokemonName.setError(getString(R.string.game_error));
        }

    }

    private void loadImages(Boolean newPokemon) {
        Handler handler = new Handler();
        Runnable runnable = () -> {
            if (isCorrect) {
                holder.win();
            } else {
                holder.ivPokemon.setImageBitmap(bitmapObscure);
            }
        };

        Thread thread = new Thread(() -> {

            // Get random pokemon
            if (newPokemon) {
                PokemonDao dao = PokemonDb.getInstance(getContext()).pokemonDao();
                pokemon = dao.getRandomPokemon();
            }

            // Load image from file system
            bitmapNormal = imageManager.loadFromDisk(
                    requireContext().getFilesDir() +
                            requireContext().getString(R.string.images),
                    pokemon.getId() + requireContext().getString(R.string.extension));

            // Darken bitmap
            bitmapObscure = holder.darkenBitmap(bitmapNormal);
            handler.post(runnable);
        });

        thread.start();
    }

    private void startGame() {
        isCorrect = false;
        mp.start();
        loadImages(true);
        Objects.requireNonNull(holder.tilPokemonName.getEditText()).clearFocus();
        Objects.requireNonNull(holder.tilPokemonName.getEditText()).getText().clear();
    }

    class Holder implements View.OnClickListener, EditText.OnEditorActionListener {
        final ImageView ivPokemon;
        final Button btnConfirm;
        final Button btnNext;
        final TextInputLayout tilPokemonName;
        final Button btnReveal;

        Holder(View fv) {
            ivPokemon = fv.findViewById(R.id.ivPokemon);
            btnNext = fv.findViewById(R.id.btnNext);
            btnNext.setOnClickListener(this);

            btnConfirm = fv.findViewById(R.id.btnConfirm);
            btnConfirm.setOnClickListener(this);

            btnReveal = fv.findViewById(R.id.btnReveal);
            btnReveal.setOnClickListener(this);

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
                    btnReveal.setEnabled(true);
                    tilPokemonName.setError(null);
                    Objects.requireNonNull(holder.tilPokemonName.getEditText()).setEnabled(true);
                    startGame();
                    break;
                case R.id.btnConfirm:
                    Objects.requireNonNull(tilPokemonName.getEditText())
                            .onEditorAction(EditorInfo.IME_ACTION_DONE);
                    break;
                case R.id.btnReveal:
                    reveal();
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

        public void win() {
            tilPokemonName.setError(null);
            ivPokemon.setImageBitmap(bitmapNormal);
            btnConfirm.setEnabled(false);
            btnReveal.setEnabled(false);
            Objects.requireNonNull(tilPokemonName.getEditText()).setEnabled(false);

        }

        public void reveal() {
            isCorrect = true;
            Objects.requireNonNull(holder.tilPokemonName.getEditText())
                    .setText(StringManager.capitalize(pokemon.getName()));
            win();
        }


    }
}
