package it.thetarangers.thetadex.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import it.thetarangers.thetadex.R;
import it.thetarangers.thetadex.activities.PokemonDetailActivity;
import it.thetarangers.thetadex.favorites.FavoritesManager;
import it.thetarangers.thetadex.listener.SelectorCallback;
import it.thetarangers.thetadex.listener.SelectorListener;
import it.thetarangers.thetadex.model.Pokemon;
import it.thetarangers.thetadex.utilities.ImageManager;
import it.thetarangers.thetadex.utilities.StringManager;
import it.thetarangers.thetadex.utilities.TypeTextViewManager;

public class PokedexAdapter extends RecyclerView.Adapter<PokedexAdapter.ViewHolder>
        implements FastScrollRecyclerView.SectionedAdapter {
    public static Integer REQ_CODE = 42;
    public List<Pokemon> pokemonList;
    private Activity context;
    private Boolean isClickable;

    private ImageManager imageManager = new ImageManager();
    private SelectorListener selectList;

    public PokedexAdapter(Activity context, SelectorCallback call) {
        this.pokemonList = new ArrayList<>();
        this.context = context;
        isClickable = true;
        this.selectList = new SelectorListener(call);

    }

    public void setClickable(Boolean isClickable) {
        this.isClickable = isClickable;
    }

    public void deselectAll() {
        selectList.clearList();
        notifyDataSetChanged();
    }


    public List<Pokemon> getSelected() {

        List<Pokemon> selected = new ArrayList<>();
        List<Integer> selectIndex = selectList.getSelectedPosition();

        for (int i = 0; i < selectIndex.size(); i++) {
            Pokemon temp = pokemonList.get(selectIndex.get(i));
            selected.add(temp);
        }

        return selected;
    }

    public void setPokemonList(List<Pokemon> pokemonList) {
        if (pokemonList.size() > 0) {
            // Set the pokemons list
            this.pokemonList = pokemonList;
            notifyDataSetChanged();
        } else {
            // If the list is empty show Toast
            if (context != null) {
                Toast t = Toast.makeText(context, context.getString(R.string.no_pokemon_found),
                        Toast.LENGTH_SHORT);
                t.show();
            }
        }
    }

    // Used to set the List in FragmentFavorite
    public void setFavoriteList(List<Pokemon> pokemonList) {
        this.pokemonList = pokemonList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PokedexAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MaterialCardView cardView;
        // Inflate row of RecyclerView
        cardView = (MaterialCardView) LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_pokemon, parent, false);

        return new PokedexAdapter.ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull PokedexAdapter.ViewHolder holder, int position) {
        Pokemon pokemon = pokemonList.get(position);

        holder.actualPokemon = pokemon;

        holder.cvPokemon.setCardBackgroundColor(Color.parseColor(pokemon.getAverageColor()));
        holder.tvId.setText(String.format(Locale.getDefault(), "#%d", pokemon.getId()));
        holder.tvName.setText(StringManager.capitalize(pokemon.getName()));
        holder.tbFavorite.setChecked(pokemon.getFavorite());

        if (context == null)
            return;

        // Load image from disk
        holder.ivSprite.setImageBitmap(imageManager.loadFromDisk(
                context.getFilesDir() + context.getString(R.string.images),
                pokemon.getId() + context.getString(R.string.extension)));

        // Setup the type's text
        TypeTextViewManager.setup(context, pokemon, holder.tvType1, holder.tvType2);

        boolean isSelected = selectList.isSelected(position);
        if (isSelected)
            holder.cvPokemon.setSelected(true);
        else holder.cvPokemon.setSelected(false);
    }

    @Override
    public int getItemCount() {
        return pokemonList.size();
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        return String.valueOf(pokemonList.get(position).getId());
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivSprite;
        ImageView imageView;
        TextView tvName;
        TextView tvId;
        TextView tvType1;
        TextView tvType2;
        MaterialCardView cvPokemon;
        ToggleButton tbFavorite;

        Pokemon actualPokemon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cvPokemon = itemView.findViewById(R.id.cvPokemon);
            cvPokemon.setOnClickListener(this);
            cvPokemon.setOnLongClickListener(selectList);

            ivSprite = itemView.findViewById(R.id.ivImage);
            imageView = itemView.findViewById(R.id.imageView);

            tvName = itemView.findViewById(R.id.tvName);
            tvId = itemView.findViewById(R.id.tvId);
            tvType1 = itemView.findViewById(R.id.tvType1);
            tvType2 = itemView.findViewById(R.id.tvType2);
            tbFavorite = itemView.findViewById(R.id.tbFavorite);
            tbFavorite.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // Used to prevent the user from opening more than one PokemonDetailActivity
            // when the animation is still playing
            if (!isClickable)
                return;

            if (v.getId() == R.id.cvPokemon) {
                if (selectList.selectedSize() > 0)
                    selectList.onLongClick(v);
                else {
                    // Open the detail activity for the selected pokemon
                    Intent data = new Intent(context, PokemonDetailActivity.class);
                    data.putExtra("pokemon", actualPokemon);

                    ActivityOptions options = ActivityOptions
                            .makeSceneTransitionAnimation(context,
                                    Pair.create(ivSprite, "imageExpansion"));


                    setClickable(false);
                    context.startActivityForResult(data, REQ_CODE, options.toBundle());
                }
            } else if (v.getId() == R.id.tbFavorite) {
                // When the star icon is pressed add the pokemon to favorites
                FavoritesManager fm = new FavoritesManager(context);
                if (tbFavorite.isChecked()) {
                    fm.addPokemonToFav(actualPokemon);
                } else {
                    fm.removePokemonFromFav(actualPokemon);
                }
            }
        }
    }
}
