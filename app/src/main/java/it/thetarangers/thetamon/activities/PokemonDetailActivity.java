package it.thetarangers.thetamon.activities;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;

import it.thetarangers.thetamon.R;
import it.thetarangers.thetamon.model.Pokemon;
import it.thetarangers.thetamon.utilities.ImageManager;
import it.thetarangers.thetamon.utilities.StringManager;
import it.thetarangers.thetamon.utilities.VolleyPokemonDetail;

public class PokemonDetailActivity extends AppCompatActivity {
    Pokemon pokemon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_detail);

        pokemon = getIntent().getParcelableExtra("pokemon");

        new Holder(PokemonDetailActivity.this);
    }

    class Holder {
        final ImageView ivSprite;
        final ShapeableImageView ivOverlay;
        final ShapeableImageView ivBackground;
        final TextView tvName;
        final TextView tvID;
        ImageManager imageManager = new ImageManager();

        VolleyPokemonDetail volley = new VolleyPokemonDetail(PokemonDetailActivity.this) {
            @Override
            public Pokemon fill(Pokemon pokemon) {
                return null;
            }
        };

        Holder(Context context){
            volley.getPokemonDetail(pokemon);

            tvID = findViewById(R.id.tvId);
            tvID.setText(String.format("#%d", pokemon.getId()));

            tvName = findViewById(R.id.tvName);
            tvName.setText(StringManager.capitalize(pokemon.getName()));

            ivBackground = findViewById(R.id.ivBackground);
            ivBackground.setBackgroundColor(Color.parseColor(pokemon.getAverageColor()));

            ivSprite = findViewById(R.id.ivSprite);
            ivSprite.setImageBitmap(imageManager.loadFromDisk(
                    context.getFilesDir() + context.getString(R.string.sprites_front),
                    pokemon.getId() + context.getString(R.string.extension)));

            ivOverlay = findViewById(R.id.ivOverlay);
            float radius = 100;

            ivOverlay.setShapeAppearanceModel(ivOverlay.getShapeAppearanceModel()
                    .toBuilder()
                    .setBottomLeftCorner(CornerFamily.ROUNDED,radius)
                    .setBottomRightCorner(CornerFamily.ROUNDED,radius)
                    .build());

            ivBackground.setShapeAppearanceModel(ivOverlay.getShapeAppearanceModel()
                    .toBuilder()
                    .setBottomLeftCorner(CornerFamily.ROUNDED,radius)
                    .setBottomRightCorner(CornerFamily.ROUNDED,radius)
                    .build());
        }
    }
}