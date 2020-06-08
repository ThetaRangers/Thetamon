package it.thetarangers.thetamon.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.simplecityapps.recyclerview_fastscroll.interfaces.OnFastScrollStateChangeListener;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.thetarangers.thetamon.R;
import it.thetarangers.thetamon.adapters.PokedexAdapter;
import it.thetarangers.thetamon.database.DaoThread;
import it.thetarangers.thetamon.model.Pokemon;
import it.thetarangers.thetamon.model.PokemonType;
import it.thetarangers.thetamon.utilities.StringManager;
import it.thetarangers.thetamon.viewmodel.PokemonListViewModel;

public class FragmentPokedex extends Fragment {

    List<String> checkedTypes;
    private PokemonListViewModel pokemonListViewModel;
    private Holder holder;
    private Bundle savedInstanceState;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pokedex, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.savedInstanceState = savedInstanceState;

        holder = new Holder(view);

        pokemonListViewModel = new ViewModelProvider(requireActivity()).get(PokemonListViewModel.class);
        pokemonListViewModel.getPokemons().observe(getViewLifecycleOwner(),
                pokemons -> holder.adapter.setPokemonList(pokemons));   // Observe the LiveData

        List<Pokemon> tmp = pokemonListViewModel.getPokemonList();
        if (tmp == null)
            search(""); // Load all pokemons
    }

    @Override
    public void onStop() {
        pokemonListViewModel.setPokemonsSynchronous(holder.adapter.getUnfilteredPokemonList());
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle out) {
        switch (checkedTypes.size()) {
            case 2:
                out.putString("TYPE2", checkedTypes.get(1));
            case 1:
                out.putString("TYPE1", checkedTypes.get(0));
            default:
                break;
        }
        super.onSaveInstanceState(out);
    }

    private void search(String query) {
        DaoThread daoThread = new DaoThread(pokemonListViewModel);

        try {
            // If the searched string is an int search by id
            daoThread.getPokemonFromId(getContext(), Integer.parseInt(query));
        } catch (NumberFormatException e) {
            // Otherwise search by name
            daoThread.getPokemonFromName(getContext(), query);
        }
    }

    private void filter(String type) {
        // TODO
        Log.d("POKE", "Filter: " + type);
    }

    class Holder extends BottomSheetBehavior.BottomSheetCallback implements View.OnClickListener, EditText.OnEditorActionListener, OnFastScrollStateChangeListener {
        final FastScrollRecyclerView rvPokedex;
        final PokedexAdapter adapter;
        final FloatingActionButton fabSearch;
        final ImageView ivClose;
        final TextInputLayout tilSearch;
        final ImageView ivSearch;

        final ConstraintLayout clShadow;
        final LinearLayout contentLayout;
        final Button buttonTest; // Dummy
        final LinearLayout llType1;
        final LinearLayout llType2;
        final LinearLayout llType3;
        // Not needed for now
        final List<MaterialCardView> cardsType;
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior;

        Holder(View fp) {

            fabSearch = fp.findViewById(R.id.fabSearch);
            fabSearch.setOnClickListener(this);

            ivClose = fp.findViewById(R.id.ivClose);
            ivClose.setOnClickListener(this);

            rvPokedex = fp.findViewById(R.id.rvPokedex);
            rvPokedex.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new PokedexAdapter(getContext());
            adapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);

            rvPokedex.setAdapter(adapter);
            rvPokedex.setOnFastScrollStateChangeListener(this);

            tilSearch = fp.findViewById(R.id.tilSearch);
            Objects.requireNonNull(tilSearch.getEditText()).setOnEditorActionListener(this);

            ivSearch = fp.findViewById(R.id.ivSearch);
            ivSearch.setOnClickListener(this);

            clShadow = fp.findViewById(R.id.clShadow);
            clShadow.setOnClickListener(this);
            contentLayout = fp.findViewById(R.id.contentLayout);
            bottomSheetBehavior = BottomSheetBehavior.from(contentLayout);
            bottomSheetBehavior.addBottomSheetCallback(this);

            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

            buttonTest = fp.findViewById(R.id.buttonTest);
            buttonTest.setOnClickListener((View) -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED));

            llType1 = fp.findViewById(R.id.llType1);
            llType2 = fp.findViewById(R.id.llType2);
            llType3 = fp.findViewById(R.id.llType3);

            checkedTypes = new ArrayList<>();
            cardsType = generateCards();
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fabSearch:
                    fabSearch.setExpanded(true);
                    break;
                case R.id.ivClose:
                    fabSearch.setExpanded(false);
                    break;
                case R.id.ivSearch:
                    Objects.requireNonNull(tilSearch.getEditText())
                            .onEditorAction(EditorInfo.IME_ACTION_DONE);
                    break;
                case R.id.mcvType:
                    MaterialCardView mcvType = (MaterialCardView) v;
                    String type = ((TextView) v.findViewById(R.id.tvType)).getText().toString();

                    if (mcvType.isChecked()) {
                        mcvType.setChecked(false);
                        // TODO remove filters

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
                    break;
                case R.id.clShadow:
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    break;
                default:
                    break;
            }
        }

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            search(Objects.requireNonNull(tilSearch.getEditText()).getText().toString());

            fabSearch.setExpanded(false);
            return false;
        }

        @Override
        public void onFastScrollStart() {
            fabSearch.setVisibility(View.GONE);
        }

        @Override
        public void onFastScrollStop() {
            fabSearch.setVisibility(View.VISIBLE);
        }

        List<MaterialCardView> generateCards() {
            // Init variables
            List<MaterialCardView> retList = new ArrayList<>();

            int i = 0;

            for (PokemonType type : PokemonType.values()) {
                String typeName = type.name();
                String tvText = typeName.toUpperCase();

                // Init color
                String color = Objects.requireNonNull(getContext()).getString(R.string.color_type) +
                        StringManager.capitalize(typeName);
                int colorID = getContext().getResources().getIdentifier(color, "color",
                        getContext().getPackageName());

                // Init card
                MaterialCardView materialCardView = (MaterialCardView) View.inflate(getContext(), R.layout.card_type, null);
                materialCardView.setCardBackgroundColor(getContext().getColor(colorID));
                TextView textView = materialCardView.findViewById(R.id.tvType);
                textView.setText(tvText);

                materialCardView.setOnClickListener(this);
                if (savedInstanceState != null) {
                    String type1 = savedInstanceState.getString("TYPE1");
                    String type2 = savedInstanceState.getString("TYPE2");
                    if (tvText.equals(type1) | tvText.equals(type2)) {
                        materialCardView.setChecked(true);
                        checkedTypes.add(tvText);
                        Log.d("POKE", tvText);
                    }
                }

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
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                clShadow.setVisibility(View.VISIBLE);
            } else if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                clShadow.setVisibility(View.GONE);
            }

        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    }
}
