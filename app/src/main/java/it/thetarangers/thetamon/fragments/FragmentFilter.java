package it.thetarangers.thetamon.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import it.thetarangers.thetamon.R;
import it.thetarangers.thetamon.model.PokemonType;
import it.thetarangers.thetamon.utilities.StringManager;
import it.thetarangers.thetamon.viewmodel.PokemonListViewModel;

public class FragmentFilter extends BottomSheetDialogFragment {

    public static String TAG = "FragmentFilter";

    PokemonListViewModel pokemonListViewModel;
    Holder holder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.filter_sheet_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pokemonListViewModel = new ViewModelProvider(requireActivity()).get(PokemonListViewModel.class);
        holder = new Holder(view);

        BottomSheetDialog dialog = (BottomSheetDialog) this.getDialog();
        assert dialog != null;
        BottomSheetBehavior<FrameLayout> behavior = dialog.getBehavior();
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    class Holder implements View.OnClickListener {

        final LinearLayout llType1;
        final LinearLayout llType2;
        final LinearLayout llType3;
        final List<MaterialCardView> cardsType;
        List<String> checkedTypes;

        Holder(View ff) {

            llType1 = ff.findViewById(R.id.llType1);
            llType2 = ff.findViewById(R.id.llType2);
            llType3 = ff.findViewById(R.id.llType3);

            checkedTypes = pokemonListViewModel.getFilterList();
            if (checkedTypes == null)
                checkedTypes = new ArrayList<>();

            cardsType = generateCards();
        }

        List<MaterialCardView> generateCards() {
            // Init variables
            List<MaterialCardView> retList = new ArrayList<>();

            int i = 0;

            for (PokemonType type : PokemonType.values()) {
                String typeName = type.name();
                String tvText = typeName.toUpperCase();

                // Init color
                String color = requireContext().getString(R.string.color_type) +
                        StringManager.capitalize(typeName);
                int colorID = requireContext().getResources().getIdentifier(color, "color",
                        requireContext().getPackageName());

                // Init card
                MaterialCardView materialCardView = (MaterialCardView) View.inflate(getContext(), R.layout.card_type, null);
                materialCardView.setCardBackgroundColor(requireContext().getColor(colorID));
                TextView textView = materialCardView.findViewById(R.id.tvType);
                textView.setText(tvText);

                // Check if the filter was already applied
                switch (checkedTypes.size()) {
                    case 2:
                        if (tvText.equals(checkedTypes.get(0)) ||
                                tvText.equals(checkedTypes.get(1)))
                            materialCardView.setChecked(true);
                        break;
                    case 1:
                        if (tvText.equals(checkedTypes.get(0)))
                            materialCardView.setChecked(true);
                        break;
                    default:
                        break;
                }

                materialCardView.setOnClickListener(this);

                // Add button to correspondent LinearLayout
                switch (i) {
                    case 0:
                        llType1.addView(materialCardView);
                        break;
                    case 1:
                        llType2.addView(materialCardView);
                        break;
                    case 2:
                        llType3.addView(materialCardView);
                        break;
                    default:
                        break;
                }
                retList.add(materialCardView);

                i = (i + 1) % 3;
            }
            return retList;
        }

        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.mcvType) {
                MaterialCardView mcvType = (MaterialCardView) v;
                String type = ((TextView) v.findViewById(R.id.tvType)).getText().toString();

                if (mcvType.isChecked()) {
                    mcvType.setChecked(false);
                    if (checkedTypes.get(0).equals(type)) {
                        checkedTypes.remove(0);
                        pokemonListViewModel.setFilters(checkedTypes);
                    } else if (checkedTypes.get(1).equals(type)) {
                        checkedTypes.remove(1);
                        pokemonListViewModel.setFilters(checkedTypes);
                    }
                } else {
                    if (checkedTypes.size() < 2) {
                        mcvType.setChecked(true);
                        checkedTypes.add(type);
                        pokemonListViewModel.setFilters(checkedTypes);
                    }
                }
            }
        }

    }

}
