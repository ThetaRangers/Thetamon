package it.thetarangers.thetadex.utilities;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.TextView;

import it.thetarangers.thetadex.R;
import it.thetarangers.thetadex.model.Move;
import it.thetarangers.thetadex.model.Pokemon;

public class TypeTextViewManager {

    // fill with text and color the type textViews
    public static void setup(Context context, Pokemon pokemon, TextView tvType1, TextView tvType2) {
        String type1 = pokemon.getType1();
        type1 = StringManager.capitalize(type1);

        // Initialize type1 TextView
        String color1 = context.getString(R.string.color_type) + type1;
        int color1ID = context.getResources().getIdentifier(color1, "color", context.getPackageName());
        GradientDrawable bg1 = (GradientDrawable) tvType1.getBackground();
        bg1.setColor(context.getColor(color1ID));
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

    public static void moveTextViewInit(Context context, Move move, TextView tvType) {
        String type = move.getType();
        type = StringManager.capitalize(type);

        // Initialize type1 TextView
        String color = context.getString(R.string.color_type) + type;
        int colorID = context.getResources().getIdentifier(color, "color", context.getPackageName());
        GradientDrawable bg = (GradientDrawable) tvType.getBackground();
        bg.setColor(context
                .getColor(colorID));
        bg.setStroke((int) context.getResources().getDimension(R.dimen.stroke_tv_type), Color.WHITE);
        tvType.setText(type.toUpperCase());
    }

}
