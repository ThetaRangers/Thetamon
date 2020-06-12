package it.thetarangers.thetamon.activities;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;

import java.util.List;

import it.thetarangers.thetamon.R;
import it.thetarangers.thetamon.model.Move;
import it.thetarangers.thetamon.model.Pokemon;
import it.thetarangers.thetamon.utilities.ImageManager;
import it.thetarangers.thetamon.utilities.StringManager;
import it.thetarangers.thetamon.utilities.VolleyMove;
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
        final TextView tvHabitat;
        final TextView tvHp;

        ImageManager imageManager = new ImageManager();

        Holder(Context context){
            tvID = findViewById(R.id.tvId);
            tvHabitat = findViewById(R.id.tvHabitat);
            tvName = findViewById(R.id.tvName);
            ivBackground = findViewById(R.id.ivBackground);
            ivSprite = findViewById(R.id.ivSprite);
            ivOverlay = findViewById(R.id.ivOverlay);
            tvHp = findViewById(R.id.tvHp);

            VolleyPokemonDetail volley = new VolleyPokemonDetail(PokemonDetailActivity.this, pokemon) {
                @Override
                public void fill(Pokemon pokemon) {
                    //Fill text views with pokemon's details
                    tvHabitat.setText(pokemon.getHabitat());
                    tvHp.setText(pokemon.getHp() + "");
                }
            };

            volley.getPokemonDetail();  //Fill information of the pokemon

            tvID.setText(String.format("#%d", pokemon.getId()));

            tvName.setText(StringManager.capitalize(pokemon.getName()));

            ivBackground.setBackgroundColor(Color.parseColor(pokemon.getAverageColor()));

            ivSprite.setImageBitmap(imageManager.loadFromDisk(
                    context.getFilesDir() + context.getString(R.string.sprites_front),
                    pokemon.getId() + context.getString(R.string.extension)));

            float radius = context.getResources().getDimension(R.dimen.pokemon_detail_image_radius);

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