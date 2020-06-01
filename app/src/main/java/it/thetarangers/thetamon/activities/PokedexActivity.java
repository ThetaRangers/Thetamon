package it.thetarangers.thetamon.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import it.thetarangers.thetamon.R;
import it.thetarangers.thetamon.database.PokemonDao;
import it.thetarangers.thetamon.database.PokemonDb;
import it.thetarangers.thetamon.model.Pokemon;
import it.thetarangers.thetamon.utilities.ImageManager;

public class PokedexActivity extends AppCompatActivity {

    Handler handler;
    Runnable update;
    Holder holder;
    List<Pokemon> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokedex);
        handler = new Handler();

        update = new Runnable() {
            @Override
            public void run() {
                holder.adapter.setPokemonList(list);
                holder.rvPokedex.setAdapter(holder.adapter);
            }
        };

        holder = new Holder();
    }

    private void search(String searchedString){
        Log.v("SC","Searched " + searchedString);

    }


    class Holder {
        final RecyclerView rvPokedex;
        final PokemonAdapter adapter;
        final SearchView svSearch;

        public Holder(){
            rvPokedex = findViewById(R.id.rvPokedex);
            rvPokedex.setLayoutManager(new LinearLayoutManager(PokedexActivity.this));
            adapter = new PokemonAdapter();


            svSearch = findViewById(R.id.svSearch);
            final PokemonAdapter adapter = new PokemonAdapter();
            SearchViewListener svl = new SearchViewListener();
            svSearch.setOnQueryTextListener(svl);

            final Thread t = new Thread() {
                @Override
                public void run() {
                    PokemonDb db = PokemonDb.getInstance(PokedexActivity.this);
                    PokemonDao dao = db.pokemonDao();

                    list = dao.getPokemons();

                    Log.w("POKE", list.size() + "");
                    handler.post(update);
                }
            };

            t.start();
        }
    }

    class SearchViewListener implements SearchView.OnQueryTextListener{

        @Override
        public boolean onQueryTextChange(String newText){
            //DUMP
            return true;
        }

        @Override
        public boolean onQueryTextSubmit(String query){
            search(query);
            hideKeyboard(PokedexActivity.this);
            return true;
        }

        public void hideKeyboard(Activity activity) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            View view = activity.getCurrentFocus();
            if (view == null) {
                view = new View(activity);
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
