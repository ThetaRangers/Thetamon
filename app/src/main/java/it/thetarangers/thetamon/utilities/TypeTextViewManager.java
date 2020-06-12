package it.thetarangers.thetamon.utilities;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.TextView;

import it.thetarangers.thetamon.R;
import it.thetarangers.thetamon.model.Pokemon;

public class TypeTextViewManager {

    private Pokemon pokemon;
    private TextView tvType1, tvType2;
    private Context context;

    public TypeTextViewManager(Pokemon pokemon, TextView tvType1, TextView tvType2) {
        this.pokemon = pokemon;
        this.tvType1 = tvType1;
        this.tvType2 = tvType2;
        this.context = tvType1.getContext();
    }

    // fill with text and color the type textViews
    public void setup() {
        String type1 = pokemon.getType1();
        type1 = StringManager.capitalize(type1);

        // Initialize type1 TextView
        String color1 = context.getString(R.string.color_type) + type1;
        int color1ID = context.getResources().getIdentifier(color1, "color", context.getPackageName());
        GradientDrawable bg1 = (GradientDrawable) tvType1.getBackground();
        bg1.setColor(context
                .getColor(color1ID));
        bg1.setStroke((int) context.getResources().getDimension(R.dimen.stroke_tv_type), Color.WHITE);
        tvType1.setText(type1.toUpperCase());

        String type2 = pokemon.getType2();
        // Initialize type2 TextView if exists
        if (type2 != null) {
            type2 = StringManager.capitalize(type2);
            String color2 = context.getString(R.string.color_type) + type2;
            int color2ID = context.getResources().getIdentifier(color2, "color", context.getPackageName());
            GradientDrawable bg2 = (GradientDrawable) tvType2.getBackground();
            bg2.setColor(context.getColor(color2ID));
            bg2.setStroke((int) context.getResources().getDimension(R.dimen.stroke_tv_type), Color.WHITE);
            tvType2.setText(type2.toUpperCase());
            tvType2.setVisibility(View.VISIBLE);
        } else {
            tvType2.setVisibility(View.GONE);
        }
    }

}
