package it.thetarangers.thetamon.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import it.thetarangers.thetamon.R;
import it.thetarangers.thetamon.model.Pokemon;
import it.thetarangers.thetamon.utilities.ImageManager;
import it.thetarangers.thetamon.utilities.StringManager;

public class PokedexAdapter extends RecyclerView.Adapter<PokedexAdapter.ViewHolder>
        implements FastScrollRecyclerView.SectionedAdapter {
    private List<Pokemon> pokemonList;
    private Context context;

    private ImageManager imageManager = new ImageManager();

    public PokedexAdapter(Context context) {
        this.pokemonList = new ArrayList<>();
        this.context = context;
    }

    public void setPokemonList(List<Pokemon> pokemonList) {
        if (pokemonList.size() > 0) {
            this.pokemonList = pokemonList;
            notifyDataSetChanged();
        } else {
            if (context != null) {
                Toast t = Toast.makeText(context, context.getString(R.string.no_pokemon_found),
                        Toast.LENGTH_SHORT);
                t.show();
            }
        }
    }

    public List<Pokemon> getPokemonList(){
        return this.pokemonList;
    }

    @NonNull
    @Override
    public PokedexAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ConstraintLayout cl;
        // Inflate row of RecyclerView
        cl = (ConstraintLayout) LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_pokemon, parent, false);

        return new PokedexAdapter.ViewHolder(cl);
    }

    @Override
    public void onBindViewHolder(@NonNull PokedexAdapter.ViewHolder holder, int position) {
        Pokemon pokemon = pokemonList.get(position);

        holder.cvPokemon.setCardBackgroundColor(Color.parseColor(pokemon.getAverageColor()));
        holder.tvId.setText(String.format(Locale.getDefault(), "#%d", pokemon.getId()));
        holder.tvName.setText(StringManager.capitalize(pokemon.getName()));

        if (context == null)
            return;

        holder.ivSprite.setImageBitmap(imageManager.loadFromDisk(
                context.getFilesDir() + context.getString(R.string.sprites_front),
                pokemon.getId() + context.getString(R.string.extension)));

        // Initialize type1 TextView
        String type1 = pokemon.getType1();
        type1 = StringManager.capitalize(type1);
        String color1 = context.getString(R.string.color_type) + type1;
        int color1ID = context.getResources().getIdentifier(color1, "color", context.getPackageName());
        GradientDrawable bg1 = (GradientDrawable) holder.tvType1.getBackground();
        bg1.setColor(context
                .getColor(color1ID));
        bg1.setStroke((int) context.getResources().getDimension(R.dimen.stroke_tv_type), Color.WHITE);
        holder.tvType1.setText(type1.toUpperCase());

        String type2 = pokemon.getType2();
        // Initialize type2 TextView if exists
        if (type2 != null) {
            type2 = StringManager.capitalize(type2);
            String color2 = context.getString(R.string.color_type) + type2;
            int color2ID = context.getResources().getIdentifier(color2, "color", context.getPackageName());
            GradientDrawable bg2 = (GradientDrawable) holder.tvType2.getBackground();
            bg2.setColor(context.getColor(color2ID));
            bg2.setStroke((int) context.getResources().getDimension(R.dimen.stroke_tv_type), Color.WHITE);
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

    @NonNull
    @Override
    public String getSectionName(int position) {
        return String.valueOf(pokemonList.get(position).getId());
    }

    class ViewHolder extends RecyclerView.ViewHolder {
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
}
