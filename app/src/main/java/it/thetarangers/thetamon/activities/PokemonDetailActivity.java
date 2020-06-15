package it.thetarangers.thetamon.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import it.thetarangers.thetamon.R;
import it.thetarangers.thetamon.database.DaoThread;
import it.thetarangers.thetamon.fragments.FragmentAbility;
import it.thetarangers.thetamon.fragments.FragmentMoves;
import it.thetarangers.thetamon.model.Ability;
import it.thetarangers.thetamon.model.EvolutionDetail;
import it.thetarangers.thetamon.model.Move;
import it.thetarangers.thetamon.model.Pokemon;
import it.thetarangers.thetamon.utilities.ImageManager;
import it.thetarangers.thetamon.utilities.StringManager;
import it.thetarangers.thetamon.utilities.TypeTextViewManager;
import it.thetarangers.thetamon.utilities.VolleyEvolutionChain;
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
        final TextView tvAttack;
        final TextView tvDefense;
        final TextView tvSpecialAttack;
        final TextView tvSpecialDefense;
        final TextView tvSpeed;
        final TextView tvFlavorText;
        final LinearLayout llAbilities;

        final TextView tvLoading, tvType1, tvType2;

        final LinearLayout llEvolution1;
        final LinearLayout llEvolution2;
        final LinearLayout llEvolution3;

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
            tvAttack = findViewById(R.id.tvAttack);
            tvDefense = findViewById(R.id.tvDefense);
            tvSpecialAttack = findViewById(R.id.tvSpecialAttack);
            tvSpecialDefense = findViewById(R.id.tvSpecialDefense);
            tvSpeed = findViewById(R.id.tvSpeed);

            tvFlavorText = findViewById(R.id.tvFlavorText);

            tvLoading = findViewById(R.id.tvLoading);
            tvType1 = findViewById(R.id.tvType1);
            tvType2 = findViewById(R.id.tvType2);

            btnMoves = findViewById(R.id.btnMoves);
            btnMoves.setOnClickListener(this);
            btnMoves.setEnabled(false);

            llAbilities = findViewById(R.id.llAbilities);

            llEvolution1 = findViewById(R.id.llEvolution1);
            llEvolution2 = findViewById(R.id.llEvolution2);
            llEvolution3 = findViewById(R.id.llEvolution3);

            imageManager = new ImageManager();

            // set everything before having the detail of the pokemon
            this.beforeDetails();

            VolleyEvolutionChain volleyEvolutionChain = new VolleyEvolutionChain(PokemonDetailActivity.this) {
                @Override
                public void fill(EvolutionDetail evolutionDetail) {
                    fillEvolution(evolutionDetail);
                    Holder.this.afterDetails();
                }
            };

            VolleyPokemonDetail volley = new VolleyPokemonDetail(PokemonDetailActivity.this, pokemon) {
                @Override
                public void fill(Pokemon pokemon) {
                    // Set the reference to pokemon with details and call holder method
                    PokemonDetailActivity.this.pokemon = pokemon;
                    volleyEvolutionChain.getEvolutionChain(pokemon.getUrlEvolutionChain());
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

            Resources res = getResources();

            //Fill text views with pokemon's details
            tvHabitat.setText(String.format(Locale.getDefault(), "%s: %s", res.getString(R.string.label_habitat), StringManager.capitalize(pokemon.getHabitat())));
            tvHp.setText(String.format(Locale.getDefault(), "%s: %d", res.getString(R.string.label_hp), pokemon.getHp()));
            tvAttack.setText(String.format(Locale.getDefault(), "%s: %d", res.getString(R.string.label_attack), pokemon.getAttack()));
            tvDefense.setText(String.format(Locale.getDefault(), "%s: %d", res.getString(R.string.label_defense), pokemon.getDefense()));
            tvSpecialAttack.setText(String.format(Locale.getDefault(), "%s: %d", res.getString(R.string.label_special_attack), pokemon.getSpecialAttack()));
            tvSpecialDefense.setText(String.format(Locale.getDefault(), "%s: %d", res.getString(R.string.label_special_defense), pokemon.getSpecialDefense()));
            tvSpeed.setText(String.format(Locale.getDefault(), "%s: %d", res.getString(R.string.label_speed), pokemon.getSpeed()));
            tvFlavorText.setText(StringManager.format(pokemon.getFlavorText()));

            fillAbilities(pokemon);

            // Start parsing moves from DB
            moves = pokemon.getMovesList();
            DaoThread daoThread = new DaoThread();

            daoThread.getMoveDetails(PokemonDetailActivity.this, moves, handler, this::enableButton);

        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btnMoves) {
                FragmentMoves fragmentMoves = new FragmentMoves();
                Bundle args = new Bundle();
                args.putParcelableArrayList(FragmentMoves.MOVES, (ArrayList<? extends Parcelable>) moves);
                fragmentMoves.setArguments(args);
                fragmentMoves.show(getSupportFragmentManager(), FragmentMoves.TAG);
            } if(v.getId() == R.id.cvAbility) {
                FragmentAbility fragmentAbility = new FragmentAbility();

                TextView tv = v.findViewById(R.id.tvAbility);

                Bundle arg = new Bundle();
                arg.putString("name", StringManager.decapitalize(tv.getText().toString()));
                fragmentAbility.setArguments(arg);
                fragmentAbility.show(getSupportFragmentManager(), FragmentAbility.TAG);
            }
        }

        private void fillAbilities(Pokemon pokemon) {
            List<Ability> abilityList = pokemon.getAbilityList();

            for(int i = 0; i < abilityList.size(); i++) {

                MaterialCardView card = (MaterialCardView) View.inflate(PokemonDetailActivity.this, R.layout.item_ability, null);
                TextView tvAbility = card.findViewById(R.id.tvAbility);

                card.setOnClickListener(this);

                tvAbility.setText(StringManager.capitalize(abilityList.get(i).getName()));

                llAbilities.addView(card);
            }
        }

        private void fillEvolution(EvolutionDetail firstEvolution) {
            MaterialCardView card = (MaterialCardView) View.inflate(PokemonDetailActivity.this, R.layout.item_evolution, null);
            Pokemon firstPokemon = new Pokemon();
            firstPokemon.setName(firstEvolution.getName());

            fillCard(card, firstPokemon);

            llEvolution1.addView(card);

            List<EvolutionDetail> secondEvolutions = firstEvolution.getNextPokemon();

            if (secondEvolutions != null) {
                llEvolution2.setVisibility(View.VISIBLE);

                for (EvolutionDetail secondEvolution : secondEvolutions) {
                    card = (MaterialCardView) View.inflate(PokemonDetailActivity.this, R.layout.item_evolution, null);
                    Pokemon pokemon = new Pokemon();
                    pokemon.setName(secondEvolution.getName());

                    fillCard(card, pokemon);
                    llEvolution2.addView(card);

                    List<EvolutionDetail> thirdEvolutions = secondEvolution.getNextPokemon();

                    if (thirdEvolutions != null) {
                        llEvolution3.setVisibility(View.VISIBLE);

                        for (EvolutionDetail thirdEvolution : thirdEvolutions) {
                            card = (MaterialCardView) View.inflate(PokemonDetailActivity.this, R.layout.item_evolution, null);
                            Pokemon pokemonThird = new Pokemon();
                            pokemonThird.setName(thirdEvolution.getName());

                            fillCard(card, pokemonThird);
                            llEvolution3.addView(card);
                        }
                    }
                }

            }
        }

        private void fillCard(MaterialCardView card, Pokemon pokemon) {
            DaoThread daoThread = new DaoThread();
            Handler handler = new Handler();
            TextView tvName = card.findViewById(R.id.tvName);
            ImageView ivPokemon = card.findViewById(R.id.ivPokemon);

            Runnable runnable = () -> {
                    ivPokemon.setImageBitmap(imageManager.loadFromDisk(
                            PokemonDetailActivity.this.getFilesDir() + PokemonDetailActivity.this.getString(R.string.sprites_front),
                            pokemon.getId() + PokemonDetailActivity.this.getString(R.string.extension)));
                    card.setCardBackgroundColor(Color.parseColor(pokemon.getAverageColor()));

                    card.setOnClickListener(v -> {
                        Intent data = new Intent(PokemonDetailActivity.this, PokemonDetailActivity.class);
                        data.putExtra("pokemon", pokemon);

                        startActivity(data);
                    });
            };

            tvName.setText(StringManager.capitalize(pokemon.getName()));

            daoThread.getPokemonFromName(PokemonDetailActivity.this, pokemon, handler, runnable);
        }
    }
}