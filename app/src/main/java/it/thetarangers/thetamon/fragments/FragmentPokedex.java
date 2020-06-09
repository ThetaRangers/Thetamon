package it.thetarangers.thetamon.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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
import androidx.transition.Fade;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

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
                (pokemons) -> filter());   // Observe the LiveData

        List<Pokemon> tmp = pokemonListViewModel.getPokemonList();
        if (tmp == null)
            search(""); // Load all pokemons
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
        for (MaterialCardView card : holder.cardsType) {
            card.setChecked(false);
        }

        checkedTypes.clear();

        try {
            // If the searched string is an int search by id
            daoThread.getPokemonFromId(getContext(), Integer.parseInt(query));
        } catch (NumberFormatException e) {
            // Otherwise search by name
            daoThread.getPokemonFromName(getContext(), query);
        }
    }

    private void filter() {
        //TODO refactor please i'm dirty
        List<Pokemon> tmp = new ArrayList<>();
        List<Pokemon> pokemonList = pokemonListViewModel.getPokemonList();
        String type1;
        String type2 = null;

        switch (checkedTypes.size()) {
            case 2:
                type2 = checkedTypes.get(1).toLowerCase();
            case 1:
                type1 = checkedTypes.get(0).toLowerCase();
                break;
            default:
                holder.adapter.setPokemonList(pokemonList);
                return;
        }


        for (int i = 0; i < pokemonList.size(); i++) {
            Pokemon pokemon = pokemonList.get(i);

            if (type2 != null) {
                if ((type1.equals(pokemon.getType1()) && type2.equals(pokemon.getType2())) ||
                        (type2.equals(pokemon.getType1()) && type1.equals(pokemon.getType2()))) {
                    tmp.add(pokemon);
                }
            } else {
                if (type1.equals(pokemon.getType1()) || type1.equals(pokemon.getType2())) {
                    tmp.add(pokemon);
                }
            }
        }

        holder.adapter.setPokemonList(tmp);
    }

    class Holder extends BottomSheetBehavior.BottomSheetCallback implements View.OnClickListener, EditText.OnEditorActionListener, OnFastScrollStateChangeListener {
        final FastScrollRecyclerView rvPokedex;
        final PokedexAdapter adapter;
        final FloatingActionButton fabAdd;
        final FloatingActionButton fabSearch;
        final FloatingActionButton fabFilter;
        final FloatingActionButton fabReset;
        final TextInputLayout tilSearch;
        final ImageView ivSearch;

        final ConstraintLayout clShadow;
        final LinearLayout filterSheet;
        final LinearLayout llType1;
        final LinearLayout llType2;
        final LinearLayout llType3;
        // Not needed for now
        final List<MaterialCardView> cardsType;
        final BottomSheetBehavior<LinearLayout> bottomSheetBehaviorFilter;

        final LinearLayout searchSheet;
        final BottomSheetBehavior<LinearLayout> bottomSheetBehaviorSearch;

        boolean isOpen = false;

        Holder(View fp) {

            fabAdd = fp.findViewById(R.id.fabAdd);
            fabAdd.setOnClickListener(this);



            fabFilter = fp.findViewById(R.id.fabFilter);
            fabFilter.setOnClickListener(this);
            init(fabFilter);

            fabSearch = fp.findViewById(R.id.fabSearch);
            fabSearch.setOnClickListener(this);
            init(fabSearch);

            fabReset = fp.findViewById(R.id.fabReset);
            fabReset.setOnClickListener(this);
            init(fabReset);

            rvPokedex = fp.findViewById(R.id.rvPokedex);
            rvPokedex.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new PokedexAdapter(getActivity());
            adapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);

            rvPokedex.setAdapter(adapter);
            rvPokedex.setOnFastScrollStateChangeListener(this);

            tilSearch = fp.findViewById(R.id.tilSearch);
            Objects.requireNonNull(tilSearch.getEditText()).setOnEditorActionListener(this);

            ivSearch = fp.findViewById(R.id.ivSearch);
            ivSearch.setOnClickListener(this);

            clShadow = fp.findViewById(R.id.clShadow);
            clShadow.setOnClickListener(this);
            filterSheet = fp.findViewById(R.id.filterSheet);
            bottomSheetBehaviorFilter = BottomSheetBehavior.from(filterSheet);
            bottomSheetBehaviorFilter.addBottomSheetCallback(this);

            searchSheet = fp.findViewById(R.id.searchSheet);
            bottomSheetBehaviorSearch = BottomSheetBehavior.from(searchSheet);
            bottomSheetBehaviorSearch.addBottomSheetCallback(this);

            bottomSheetBehaviorFilter.setState(BottomSheetBehavior.STATE_HIDDEN);
            bottomSheetBehaviorSearch.setState(BottomSheetBehavior.STATE_HIDDEN);

            llType1 = fp.findViewById(R.id.llType1);
            llType2 = fp.findViewById(R.id.llType2);
            llType3 = fp.findViewById(R.id.llType3);

            checkedTypes = new ArrayList<>();
            cardsType = generateCards();
        }

        List<MaterialCardView> generateCards() {
            // Init variables
            List<MaterialCardView> retList = new ArrayList<>();
            String type1 = null;
            String type2 = null;
            if (savedInstanceState != null) {
                type1 = savedInstanceState.getString("TYPE1");
                type2 = savedInstanceState.getString("TYPE2");
            }

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

        //TODO change position
        public boolean rotateFab(final View v, boolean rotate) {
            v.animate().setDuration(200).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                }
            }).rotation(rotate ? 135f : 0f);
            return rotate;
        }


        public void showIn(final View v) {
            v.setVisibility(View.VISIBLE);
            v.setAlpha(0f);
            v.setTranslationY(v.getHeight());
            v.animate().setDuration(200).translationY(0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                        }
                    })
                    .alpha(1f).start();
        }


        public void showOut(final View v) {
            v.setVisibility(View.VISIBLE);
            v.setAlpha(1f);
            v.setTranslationY(0);
            v.animate().setDuration(200).translationY(v.getHeight())
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            v.setVisibility(View.GONE);
                            super.onAnimationEnd(animation);
                        }
                    }).alpha(0f).start();
        }


        //TODO init in xml
        public void init(final View v) {
            v.setVisibility(View.GONE);
            v.setTranslationY(v.getHeight());
            v.setAlpha(0f);
        }

        public void collapseFab() {
            showOut(fabFilter);
            showOut(fabSearch);
            showOut(fabReset);
            isOpen = rotateFab(fabAdd, !isOpen);
        }

        public void openFab() {
            showIn(fabFilter);
            showIn(fabSearch);
            showIn(fabReset);
            isOpen = rotateFab(fabAdd, !isOpen);
        }

        public void hideKeyboardFrom(View view) {
            Context context = getContext();
            if (context != null) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fabAdd:
                    if (isOpen) {
                        collapseFab();
                    } else {
                        openFab();
                    }
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
                        }
                    }
                    filter();
                    break;
                case R.id.clShadow:
                    bottomSheetBehaviorFilter.setState(BottomSheetBehavior.STATE_HIDDEN);
                    bottomSheetBehaviorSearch.setState(BottomSheetBehavior.STATE_HIDDEN);
                    hideKeyboardFrom(tilSearch);
                    break;
                case R.id.fabFilter:
                    bottomSheetBehaviorFilter.setState(BottomSheetBehavior.STATE_EXPANDED);
                    collapseFab();
                    break;
                case R.id.fabSearch:
                    bottomSheetBehaviorSearch.setState(BottomSheetBehavior.STATE_EXPANDED);
                    collapseFab();
                    break;
                case R.id.fabReset:
                    search("");
                    collapseFab();
                    break;
                default:
                    break;
            }
        }

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search(Objects.requireNonNull(tilSearch.getEditText()).getText().toString());
            }
            bottomSheetBehaviorSearch.setState(BottomSheetBehavior.STATE_HIDDEN);
            return false;
        }

        @Override
        public void onFastScrollStart() {
            fabAdd.setVisibility(View.GONE);
            fabFilter.setVisibility(View.GONE);
            fabSearch.setVisibility(View.GONE);
        }

        @Override
        public void onFastScrollStop() {
            fabAdd.setVisibility(View.VISIBLE);
            fabFilter.setVisibility(View.VISIBLE);
            fabSearch.setVisibility(View.VISIBLE);
        }

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            Transition transition = new Fade();
            transition.setDuration(300);
            transition.addTarget(clShadow);
            TransitionManager.beginDelayedTransition((ViewGroup) clShadow.getParent(), transition);
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
