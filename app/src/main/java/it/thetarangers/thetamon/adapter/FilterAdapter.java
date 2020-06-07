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
import it.thetarangers.thetamon.model.PokemonType;
import it.thetarangers.thetamon.utilities.StringManager;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder> {
    private List<String> types;
    private List<String> checkedTypes;
    private Context context;

    public FilterAdapter(Context context) {
        this.context = context;

        types = new ArrayList<>();
        checkedTypes = new ArrayList<>();

        for (PokemonType type : PokemonType.values()) {
            types.add(type.name());
        }
    }

    @NonNull
    @Override
    public FilterAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ConstraintLayout cl;
        // Inflate row of RecyclerView
        cl = (ConstraintLayout) LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_type, parent, false);

        return new ViewHolder(cl);
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

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvType;
        MaterialCardView mcvType;
        private String type;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mcvType = itemView.findViewById(R.id.mcvType);

            tvType = itemView.findViewById(R.id.tvType);
            mcvType.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mcvType.isChecked()){
                mcvType.setChecked(false);

                if(checkedTypes.get(0).equals(type)){
                    checkedTypes.remove(0);
                } else if (checkedTypes.get(1).equals(type)){
                    checkedTypes.remove(1);
                }
            } else {
                if(checkedTypes.size() < 2) {
                    mcvType.setChecked(true);
                    checkedTypes.add(type);
                }
            }
        }

        public void setType(String type) {
            this.type = type;

            tvType.setText(type.toUpperCase());
        }
    }
}
