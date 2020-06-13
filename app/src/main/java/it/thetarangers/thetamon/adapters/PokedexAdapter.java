package it.thetarangers.thetamon.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import it.thetarangers.thetamon.R;
import it.thetarangers.thetamon.activities.PokemonDetailActivity;
import it.thetarangers.thetamon.listener.SelectorCallback;
import it.thetarangers.thetamon.listener.SelectorListener;
import it.thetarangers.thetamon.model.Pokemon;
import it.thetarangers.thetamon.utilities.ImageManager;
import it.thetarangers.thetamon.utilities.StringManager;
import it.thetarangers.thetamon.utilities.TypeTextViewManager;

public class PokedexAdapter extends RecyclerView.Adapter<PokedexAdapter.ViewHolder>
        implements FastScrollRecyclerView.SectionedAdapter {
    public static Integer REQ_CODE = 42;
    private List<Pokemon> pokemonList;
    private Activity context;
    private Boolean isClickable;

    private ImageManager imageManager = new ImageManager();
    private SelectorListener selectList;
    private SelectorCallback call;

    //aggiustare chi crea l adapter
    public PokedexAdapter(Activity context, SelectorCallback call) {
        this.pokemonList = new ArrayList<>();
        this.context = context;
        isClickable = true;
        this.call = call;
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
        Log.d("POKE", "in adapter");
        for (int i = 0; i < selectIndex.size(); i++) {
            Pokemon temp = pokemonList.get(selectIndex.get(i));
            selected.add(temp);
            Log.d("POKE", "pokemon ID " + temp.getId() + "pokemon name " + temp.getName());
        }

        return selected;
    }

    public List<Pokemon> getPokemonList() {
        return this.pokemonList;
    }

    public void setPokemonList(List<Pokemon> pokemonList) {
        if (pokemonList.size() > 0) {
            this.pokemonList = pokemonList;
            selectList.clearList();
            notifyDataSetChanged();
        } else {
            if (context
                    != null) {
                Toast t = Toast.makeText(context
                        ,
                        context
                                .getString(R.string.no_pokemon_found),
                        Toast.LENGTH_SHORT);
                t.show();
            }
        }
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

        holder.cvPokemon.setCardBackgroundColor(Color.parseColor(pokemon.getAverageColor()));
        holder.tvId.setText(String.format(Locale.getDefault(), "#%d", pokemon.getId()));
        holder.tvName.setText(StringManager.capitalize(pokemon.getName()));

        if (context == null)
            return;

        holder.ivSprite.setImageBitmap(imageManager.loadFromDisk(
                context.getFilesDir() + context.getString(R.string.sprites_front),
                pokemon.getId() + context.getString(R.string.extension)));

        TypeTextViewManager typeTextViewManager = new TypeTextViewManager(pokemon, holder.tvType1, holder.tvType2);
        typeTextViewManager.setup();

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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cvPokemon = itemView.findViewById(R.id.cvPokemon);
            cvPokemon.setOnClickListener(this);
            cvPokemon.setOnLongClickListener(selectList);

            ivSprite = itemView.findViewById(R.id.ivSprite);
            imageView = itemView.findViewById(R.id.imageView);

            tvName = itemView.findViewById(R.id.tvName);
            tvId = itemView.findViewById(R.id.tvId);
            tvType1 = itemView.findViewById(R.id.tvType1);
            tvType2 = itemView.findViewById(R.id.tvType2);
        }

        @Override
        public void onClick(View v) {
            if (!isClickable)
                return;

            if (selectList.selectedSize() > 0)
                selectList.onLongClick(v);
            else {
                String id = tvId.getText().toString().substring(1);

                for (int i = 0; i < pokemonList.size(); i++) {
                    if (id.equals(pokemonList.get(i).getId() + "")) {
                        Intent data = new Intent(context, PokemonDetailActivity.class);
                        data.putExtra("pokemon", pokemonList.get(i));

                        // TODO: make it work with tvId and tvName
                        ActivityOptions options = ActivityOptions
                                .makeSceneTransitionAnimation(context,
                                        Pair.create(imageView, "cardExpansion"),
                                        Pair.create(ivSprite, "imageExpansion"),
                                        Pair.create(tvId, "transId"),
                                        Pair.create(tvName, "transName"));

                        setClickable(false);
                        context.startActivityForResult(data, REQ_CODE, options.toBundle());
                    }
                }
            }
        }


    }
}
