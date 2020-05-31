package it.thetarangers.thetamon.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import it.thetarangers.thetamon.R;
import it.thetarangers.thetamon.database.PokemonDao;
import it.thetarangers.thetamon.database.PokemonDb;
import it.thetarangers.thetamon.model.Pokemon;
import it.thetarangers.thetamon.utilities.ImageManager;

public class PokedexActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokedex);

        new Holder();
    }

    class Holder {
        final RecyclerView rvPokedex;

        public Holder(){
            rvPokedex = findViewById(R.id.rvPokedex);
            rvPokedex.setLayoutManager(new LinearLayoutManager(PokedexActivity.this));
            final PokemonAdapter adapter = new PokemonAdapter();


            final Thread t = new Thread() {
                @Override
                public void run() {
                    PokemonDb db = PokemonDb.getInstance(PokedexActivity.this);
                    PokemonDao dao = db.pokemonDao();

                    List<Pokemon> list = dao.getPokemons();

                    Log.w("POKE", list.size() + "");
                    adapter.setPokemonList(list);
                    rvPokedex.setAdapter(adapter);
                }
            };

            t.start();
        }
    }

    class PokemonAdapter extends RecyclerView.Adapter<ViewHolder>{
        private List<Pokemon> pokemonList;
        private ImageManager imageManager = new ImageManager();

        public PokemonAdapter(List<Pokemon> pokemonList){
            this.pokemonList = pokemonList;
        }

        public PokemonAdapter(){
            this.pokemonList = new ArrayList<>();
        }

        public void setPokemonList(List<Pokemon> pokemonList){
            this.pokemonList = pokemonList;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ConstraintLayout cl;
            //Inflate row of RecyclerView
            cl = (ConstraintLayout) LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.item_pokemon, parent, false);

            return new ViewHolder(cl);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            //TODO usare getter
            Pokemon pokemon = pokemonList.get(position);
            holder.tvId.setText(pokemon.id + "");
            holder.tvName.setText(pokemon.name);
            holder.ivSprite.setImageBitmap(imageManager.loadFromDisk(
                    PokedexActivity.this.getFilesDir() + "/sprites_front", pokemon.id + ".png"));
        }

        @Override
        public int getItemCount() {
            return pokemonList.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView ivSprite;
        TextView tvName;
        TextView tvId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivSprite = itemView.findViewById(R.id.ivSprite);
            tvName = itemView.findViewById(R.id.tvName);
            tvId = itemView.findViewById(R.id.tvId);
        }
    }
}
