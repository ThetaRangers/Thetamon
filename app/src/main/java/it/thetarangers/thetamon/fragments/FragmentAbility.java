package it.thetarangers.thetamon.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import it.thetarangers.thetamon.R;
import it.thetarangers.thetamon.model.Ability;
import it.thetarangers.thetamon.utilities.StringManager;
import it.thetarangers.thetamon.utilities.VolleyAbility;

public class FragmentAbility extends BottomSheetDialogFragment {
    Ability ability;
    Holder holder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.ability_sheet_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ability = new Ability();

        assert getArguments() != null;
        String name = getArguments().getString("name");

        ability.setName(name);

        holder = new Holder(view);

        VolleyAbility volleyAbility = new VolleyAbility(getContext()) {
            @Override
            public void fill(Ability ability) {
                holder.tvEffect.setText(ability.getEffect());
                holder.tvFlavor.setText(ability.getFlavor_text());
            }
        };

        volleyAbility.getAbilityDetail(ability);
    }

    class Holder {
        final TextView tvName;
        final TextView tvEffect;
        final TextView tvFlavor;

        Holder(View v){
            tvName = v.findViewById(R.id.tvName);
            tvEffect = v.findViewById(R.id.tvEffect);
            tvFlavor = v.findViewById(R.id.tvFlavor);

            tvName.setText(StringManager.capitalize(ability.getName()));
        }
    }

}
