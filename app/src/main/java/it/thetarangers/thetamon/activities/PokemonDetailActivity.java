package it.thetarangers.thetamon.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;

import java.util.List;

import it.thetarangers.thetamon.R;
import it.thetarangers.thetamon.database.DaoThread;
import it.thetarangers.thetamon.fragments.FragmentMoves;
import it.thetarangers.thetamon.model.Move;
import it.thetarangers.thetamon.model.Pokemon;
import it.thetarangers.thetamon.utilities.ImageManager;
import it.thetarangers.thetamon.utilities.StringManager;
import it.thetarangers.thetamon.utilities.TypeTextViewManager;
import it.thetarangers.thetamon.utilities.VolleyPokemonDetail;

public class PokemonDetailActivity extends AppCompatActivity {
    Pokemon pokemon;
    Handler handler;
    List<Move> moves;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_detail);

        pokemon = getIntent().getParcelableExtra("pokemon");

        handler = new Handler();
        new Holder();
    }

    class Holder implements View.OnClickListener {
        final ImageView ivSprite;
        final ShapeableImageView ivOverlay;
        final ShapeableImageView ivBackground;
        final TextView tvName;
        final TextView tvID;
        final TextView tvHabitat;
        final TextView tvHp;

        final TextView tvLoading, tvType1, tvType2;

        final Button btnMoves;

        ImageManager imageManager;

        Holder() {
            tvID = findViewById(R.id.tvId);
            tvHabitat = findViewById(R.id.tvHabitat);
            tvName = findViewById(R.id.tvName);
            ivBackground = findViewById(R.id.ivBackground);
            ivSprite = findViewById(R.id.ivSprite);
            ivOverlay = findViewById(R.id.ivOverlay);
            tvHp = findViewById(R.id.tvHp);

            tvLoading = findViewById(R.id.tvLoading);
            tvType1 = findViewById(R.id.tvType1);
            tvType2 = findViewById(R.id.tvType2);

            btnMoves = findViewById(R.id.btnMoves);
            btnMoves.setOnClickListener(this);
            btnMoves.setEnabled(false);

            imageManager = new ImageManager();

            // set everything before having the detail of the pokemon
            this.beforeDetails();

            VolleyPokemonDetail volley = new VolleyPokemonDetail(PokemonDetailActivity.this, pokemon) {
                @Override
                public void fill(Pokemon pokemon) {
                    // Set the reference to pokemon with details and call holder method
                    PokemonDetailActivity.this.pokemon = pokemon;
                    Holder.this.afterDetails();
                }
            };

            // Get the detail of the pokemon
            volley.getPokemonDetail();
        }

        private void beforeDetails() {

            //set textViews
            tvID.setText(String.format("#%d", pokemon.getId()));
            tvName.setText(StringManager.capitalize(pokemon.getName()));

            //set the type textViews
            TypeTextViewManager typeTextViewManager = new TypeTextViewManager(pokemon, tvType1, tvType2);
            typeTextViewManager.setup();


            //set the sprite part
            ivBackground.setBackgroundColor(Color.parseColor(pokemon.getAverageColor()));

            ivSprite.setImageBitmap(imageManager.loadFromDisk(
                    PokemonDetailActivity.this.getFilesDir() + PokemonDetailActivity.this.getString(R.string.sprites_front),
                    pokemon.getId() + PokemonDetailActivity.this.getString(R.string.extension)));

            // programmatically resize the height of ivSprite based on screen height
            double resize = 0.25;
            int screenHeight = PokemonDetailActivity.this.getResources().getDisplayMetrics().heightPixels;
            ivSprite.getLayoutParams().height = (int) (screenHeight * resize);

            float radius = PokemonDetailActivity.this.getResources().getDimension(R.dimen.pokemon_detail_image_radius);

            ivOverlay.setShapeAppearanceModel(ivOverlay.getShapeAppearanceModel()
                    .toBuilder()
                    .setBottomLeftCorner(CornerFamily.ROUNDED, radius)
                    .setBottomRightCorner(CornerFamily.ROUNDED, radius)
                    .build());

            ivBackground.setShapeAppearanceModel(ivOverlay.getShapeAppearanceModel()
                    .toBuilder()
                    .setBottomLeftCorner(CornerFamily.ROUNDED, radius)
                    .setBottomRightCorner(CornerFamily.ROUNDED, radius)
                    .build());


            //set the loading textView to visible
            tvLoading.setVisibility(View.VISIBLE);
        }

        void enableButton() {
            btnMoves.setEnabled(true);
        }

        void afterDetails() {

            // set the loading textView to invisible
            tvLoading.setVisibility(View.INVISIBLE);
            tvLoading.setText("");

            //Fill text views with pokemon's details
            tvHabitat.setText(StringManager.capitalize(pokemon.getHabitat()));
            tvHp.setText("Base HP: " + pokemon.getHp());

            // Start parsing moves from DB
            moves = pokemon.getMovesList();

            DaoThread daoThread = new DaoThread();

            daoThread.getMoveType(PokemonDetailActivity.this, moves, handler, this::enableButton);

        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btnMoves) {
                FragmentMoves fragmentMoves = new FragmentMoves();
                fragmentMoves.setMoveList(moves);
                fragmentMoves.show(getSupportFragmentManager(), FragmentMoves.TAG);
            }
        }
    }

}