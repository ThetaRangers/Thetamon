package it.thetarangers.thetamon.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import it.thetarangers.thetamon.R;
import it.thetarangers.thetamon.model.Pokemon;
import it.thetarangers.thetamon.model.PokemonType;
import it.thetarangers.thetamon.utilities.StringManager;
import it.thetarangers.thetamon.viewmodel.PokemonListViewModel;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder> {
    private final PokemonListViewModel viewModel;
    private List<String> types;
    private List<String> checkedTypes;
    private List<Pokemon> pokemonList;
    private List<View> cards = new ArrayList<>();

    private Context context;

    public FilterAdapter(Context context, PokemonListViewModel viewModel) {
        this.context = context;
        this.viewModel = viewModel;

        types = new ArrayList<>();
        checkedTypes = new ArrayList<>();

        for (PokemonType type : PokemonType.values()) {
            types.add(type.name());
        }
    }

    public List<Pokemon> getPokemonList() {
        return this.pokemonList;
    }

    public void setPokemonList(List<Pokemon> pokemons) {
        if (pokemons != null) {
            this.pokemonList = pokemons;

            viewModel.setFilterList(this.pokemonList);
        }
    }

    public void setFilteredList(List<Pokemon> pokemons) {

    }

    @NonNull
    @Override
    public FilterAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ConstraintLayout cl;
        // Inflate row of RecyclerView
        cl = (ConstraintLayout) LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_type, parent, false);

        ViewHolder holder = new ViewHolder(cl);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvType.setText(types.get(position));

        String color = context.getString(R.string.color_type) + StringManager.capitalize(types.get(position));
        int colorID = context.getResources().getIdentifier(color, "color", context.getPackageName());

        holder.mcvType.setCardBackgroundColor(context.getColor(colorID));
        holder.mcvType.setStrokeColor(Color.WHITE);
        holder.mcvType.setStrokeWidth((int) context.getResources().getDimension(R.dimen.stroke_tv_type));

        holder.setType(types.get(position));
    }

    @Override
    public int getItemCount() {
        return types.size();
    }

    public List<String> getCheckedTypes() {
        return this.checkedTypes;
    }

    public void setFilter(String filter) {
        for (int i = 0; i < types.size(); i++) {
            if (types.get(i).equals(filter)) {
                cards.get(i).callOnClick();
                return;
            }
        }
    }

    private void filter(String type) {
        List<Pokemon> tmp;
/*
        for (int i = 0; i < filteredList.size(); i++) {

            if (filteredList.get(i).getType1().equals(type)) {
                filteredList.remove(i);
            }

        }

        setFilteredList(filteredList);

 */
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvType;
        MaterialCardView mcvType;
        private String type;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mcvType = itemView.findViewById(R.id.mcvType);

            tvType = itemView.findViewById(R.id.tvType);
            mcvType.setOnClickListener(this);
            cards.add(mcvType);
        }

        @Override
        public void onClick(View v) {
            if (mcvType.isChecked()) {
                mcvType.setChecked(false);

                if (checkedTypes.get(0).equals(type)) {
                    checkedTypes.remove(0);
                } else if (checkedTypes.get(1).equals(type)) {
                    checkedTypes.remove(1);
                }
            } else {
                if (checkedTypes.size() < 2) {
                    mcvType.setChecked(true);
                    checkedTypes.add(type);
                    filter(type);
                }
            }
        }

        public String getType() {
            return this.type;
        }

        public void setType(String type) {
            this.type = type;

            tvType.setText(type.toUpperCase());
        }
    }
}
