package it.thetarangers.thetamon.activities;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;

import java.util.List;

import it.thetarangers.thetamon.R;
import it.thetarangers.thetamon.adapters.PokedexAdapter;
import it.thetarangers.thetamon.database.DaoThread;
import it.thetarangers.thetamon.model.Move;
import it.thetarangers.thetamon.model.Pokemon;
import it.thetarangers.thetamon.utilities.ImageManager;
import it.thetarangers.thetamon.utilities.StringManager;
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
        final RecyclerView rvMoves;

        ImageManager imageManager = new ImageManager();

        Holder(Context context) {
            tvID = findViewById(R.id.tvId);
            tvHabitat = findViewById(R.id.tvHabitat);
            tvName = findViewById(R.id.tvName);
            ivBackground = findViewById(R.id.ivBackground);
            ivSprite = findViewById(R.id.ivSprite);
            ivOverlay = findViewById(R.id.ivOverlay);
            tvHp = findViewById(R.id.tvHp);
            rvMoves = findViewById(R.id.rvMoves);

            rvMoves.setLayoutManager(new LinearLayoutManager(context));

            Runnable update = () -> {
                rvMoves.setAdapter(new MovesAdapter(moves));;
            };

            VolleyPokemonDetail volley = new VolleyPokemonDetail(PokemonDetailActivity.this, pokemon) {
                @Override
                public void fill(Pokemon pokemon) {
                    //Fill text views with pokemon's details
                    tvHabitat.setText(pokemon.getHabitat());
                    tvHp.setText(pokemon.getHp() + "");
                    moves = pokemon.getMovesList();

                    Thread t = new Thread(() -> {
                        DaoThread daoThread = new DaoThread();

                        daoThread.getMoveType(PokemonDetailActivity.this, moves, handler, update);

                    });

                    t.start();
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
                    .setBottomLeftCorner(CornerFamily.ROUNDED, radius)
                    .setBottomRightCorner(CornerFamily.ROUNDED, radius)
                    .build());

            ivBackground.setShapeAppearanceModel(ivOverlay.getShapeAppearanceModel()
                    .toBuilder()
                    .setBottomLeftCorner(CornerFamily.ROUNDED, radius)
                    .setBottomRightCorner(CornerFamily.ROUNDED, radius)
                    .build());
        }
    }

    class MovesAdapter extends RecyclerView.Adapter<MoveHolder> {

        private List<Move> moveList;

        MovesAdapter(List<Move> moveList) {
            this.moveList = moveList;
        }

        @NonNull
        @Override
        public MoveHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ConstraintLayout cl;
            // Inflate row of RecyclerView
            cl = (ConstraintLayout) LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.item_move, parent, false);

            return new MoveHolder(cl);
        }

        @Override
        public void onBindViewHolder(@NonNull MoveHolder holder, int position) {
            Move move = moveList.get(position);

            holder.tvMove.setText(move.getName());

            //TODO set move type
            Log.d("POKE", "move: "+move.getName()+" type: "+move.getType());
        }

        @Override
        public int getItemCount() {
            return moveList.size();
        }
    }

    class MoveHolder extends RecyclerView.ViewHolder {
        TextView tvMove;

        public MoveHolder(@NonNull View itemView) {
            super(itemView);

            tvMove = itemView.findViewById(R.id.tvMove);
        }
    }
}