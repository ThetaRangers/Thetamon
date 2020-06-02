package it.thetarangers.thetamon.activities;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.thetarangers.thetamon.R;
import it.thetarangers.thetamon.database.DaoThread;
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

        final DaoThread daoThread = new DaoThread();

        update = new Runnable() {
            @Override
            public void run() {
                list = daoThread.getList();
                holder.adapter.setPokemonList(list);
            }
        };

        holder = new Holder();

        daoThread.getPokemons(PokedexActivity.this, handler, update);

    }

    private void search(String searchedString) {
        Log.v("SC", "Searched " + searchedString);

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivSprite;
        TextView tvName;
        TextView tvId;
        TextView tvType1;
        TextView tvType2;
        MaterialCardView cvPokemon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cvPokemon = itemView.findViewById(R.id.cvPokemon);
            ivSprite = itemView.findViewById(R.id.ivSprite);
            tvName = itemView.findViewById(R.id.tvName);
            tvId = itemView.findViewById(R.id.tvId);
            tvType1 = itemView.findViewById(R.id.tvType1);
            tvType2 = itemView.findViewById(R.id.tvType2);

        }
    }

    class Holder implements View.OnClickListener {
        final RecyclerView rvPokedex;
        final PokemonAdapter adapter;
        final SearchView svSearch;
        final FloatingActionButton fabSearch;
        final ImageButton ibClose;

        public Holder() {
            fabSearch = findViewById(R.id.fabSearch);
            fabSearch.setOnClickListener(this);

            ibClose = findViewById(R.id.ibClose);
            ibClose.setOnClickListener(this);

            rvPokedex = findViewById(R.id.rvPokedex);
            rvPokedex.setLayoutManager(new LinearLayoutManager(PokedexActivity.this));
            adapter = new PokemonAdapter();
            rvPokedex.setAdapter(adapter);


            svSearch = findViewById(R.id.svSearch);
            SearchViewListener svl = new SearchViewListener();
            svSearch.setOnQueryTextListener(svl);

        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.fabSearch) {
                fabSearch.setExpanded(true);
            } else if (v.getId() == R.id.ibClose) {
                fabSearch.setExpanded(false);
            }
        }
    }

    class SearchViewListener implements SearchView.OnQueryTextListener {

        @Override
        public boolean onQueryTextChange(String newText) {
            //DUMP
            return true;
        }

        @Override
        public boolean onQueryTextSubmit(String query) {
            search(query);
            hideKeyboard(PokedexActivity.this);
            return true;
        }

        public void hideKeyboard(AppCompatActivity activity) {
            InputMethodManager imm = (InputMethodManager) activity
                    .getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE);
            View view = activity.getCurrentFocus();
            if (view == null) {
                view = new View(activity);
            }
            Objects.requireNonNull(imm).hideSoftInputFromWindow(view.getWindowToken(), 0);
        }


    }

    class PokemonAdapter extends RecyclerView.Adapter<ViewHolder> {
        private List<Pokemon> pokemonList;
        private ImageManager imageManager = new ImageManager();

        public PokemonAdapter() {
            this.pokemonList = new ArrayList<>();
        }

        public void setPokemonList(List<Pokemon> pokemonList) {
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
            holder.cvPokemon.setCardBackgroundColor(Color.parseColor(pokemon.averageColor));
            holder.tvId.setText(String.valueOf(pokemon.id));
            holder.tvName.setText(capitalize(pokemon.name));
            holder.ivSprite.setImageBitmap(imageManager.loadFromDisk(
                    PokedexActivity.this.getFilesDir() + "/sprites_front", pokemon.id + ".png"));

            String type1 = pokemon.type1;
            type1 = capitalize(type1);
            String color1 = "colorType" + type1;
            int color1ID = getResources().getIdentifier(color1, "color", getPackageName());
            GradientDrawable bg1 = (GradientDrawable) holder.tvType1.getBackground();
            bg1.setColor(getColor(color1ID));
            if (type1.equals("Dark"))
                bg1.setStroke(3, Color.WHITE);
            else
                bg1.setStroke(3, Color.DKGRAY);
            holder.tvType1.setText(type1.toUpperCase());

            String type2 = pokemon.type2;
            if (type2 != null) {
                type2 = capitalize(type2);
                String color2 = "colorType" + type2;
                int color2ID = getResources().getIdentifier(color2, "color", getPackageName());
                GradientDrawable bg2 = (GradientDrawable) holder.tvType2.getBackground();
                bg2.setColor(getColor(color2ID));
                if (type2.equals("Dark"))
                    bg2.setStroke(3, Color.WHITE);
                else
                    bg2.setStroke(3, Color.DKGRAY);
                holder.tvType2.setText(type2.toUpperCase());
                holder.tvType2.setVisibility(View.VISIBLE);
            } else {
                holder.tvType2.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return pokemonList.size();
        }
    }

    public String capitalize(String in) { // TODO maybe put this somewhere else
        return in.substring(0, 1).toUpperCase() + in.substring(1);
    }
}
