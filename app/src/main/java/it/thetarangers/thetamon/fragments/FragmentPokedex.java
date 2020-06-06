package it.thetarangers.thetamon.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import it.thetarangers.thetamon.R;
import it.thetarangers.thetamon.database.DaoThread;
import it.thetarangers.thetamon.model.Pokemon;
import it.thetarangers.thetamon.utilities.ImageManager;
import it.thetarangers.thetamon.viewmodel.PokemonListViewModel;

public class FragmentPokedex extends Fragment {

    private PokemonListViewModel pokemonListViewModel;
    private Holder holder;

    private Context context;

    public FragmentPokedex(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pokedex, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        holder = new Holder(view);

        pokemonListViewModel = new ViewModelProvider(requireActivity()).get(PokemonListViewModel.class);
        pokemonListViewModel.getPokemons().observe(getViewLifecycleOwner(),
                pokemons -> holder.adapter.setPokemonList(pokemons));   //Observe the LiveData

        List<Pokemon> tmp = pokemonListViewModel.getPokemonList();
        if (tmp == null)
            search(""); //Loads all pokemons
    }

    private String capitalize(String in) { // TODO maybe put this somewhere else
        return in.substring(0, 1).toUpperCase() + in.substring(1);
    }

    private void search(String query) {
        DaoThread daoThread = new DaoThread(pokemonListViewModel);

        try {
            //If the searched string is an int search by id
            daoThread.getPokemonFromId(context, Integer.parseInt(query));
        } catch (NumberFormatException e) {
            //Otherwise search by name
            daoThread.getPokemonFromName(context, query);
        }
    }

    class Holder implements View.OnClickListener, EditText.OnEditorActionListener {
        final RecyclerView rvPokedex;
        final PokemonAdapter adapter;
        final FloatingActionButton fabSearch;
        final ImageView ivClose;
        final TextInputLayout tilSearch;
        final ImageView ivSearch;
        final RecyclerViewFastScroller fastScroller;

        Holder(View fp) {

            fabSearch = fp.findViewById(R.id.fabSearch);
            fabSearch.setOnClickListener(this);

            ivClose = fp.findViewById(R.id.ivClose);
            ivClose.setOnClickListener(this);

            rvPokedex = fp.findViewById(R.id.rvPokedex);
            rvPokedex.setLayoutManager(new LinearLayoutManager(context));
            adapter = new PokemonAdapter();
            adapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
            rvPokedex.setAdapter(adapter);

            fastScroller = fp.findViewById(R.id.fastScroller);
            fastScroller.attachFastScrollerToRecyclerView(rvPokedex);

            tilSearch = fp.findViewById(R.id.tilSearch);
            Objects.requireNonNull(tilSearch.getEditText()).setOnEditorActionListener(this);

            ivSearch = fp.findViewById(R.id.ivSearch);
            ivSearch.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.fabSearch) {
                fastScroller.setVisibility(View.GONE);
                fabSearch.setExpanded(true);
            } else if (v.getId() == R.id.ivClose) {
                fabSearch.setExpanded(false);
                fastScroller.setVisibility(View.VISIBLE);
            } else if (v.getId() == R.id.ivSearch) {
                Objects.requireNonNull(tilSearch.getEditText()).onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        }

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            search(Objects.requireNonNull(tilSearch.getEditText()).getText().toString());

            fabSearch.setExpanded(false);
            fastScroller.setVisibility(View.VISIBLE);
            return false;
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

        class PokemonAdapter extends RecyclerView.Adapter<ViewHolder>
                implements RecyclerViewFastScroller.OnPopupTextUpdate {
            private List<Pokemon> pokemonList;
            private ImageManager imageManager = new ImageManager();

            public PokemonAdapter() {
                this.pokemonList = new ArrayList<>();
            }

            public void setPokemonList(List<Pokemon> pokemonList) {
                this.pokemonList = pokemonList;
                notifyDataSetChanged();
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                ConstraintLayout cl;
                //Inflate row of RecyclerView
                cl = (ConstraintLayout) LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.item_pokemon, parent, false);

                return new ViewHolder(cl);
            }

            @Override
            public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                //TODO usare getter
                Pokemon pokemon = pokemonList.get(position);

                holder.cvPokemon.setCardBackgroundColor(Color.parseColor(pokemon.averageColor));
                holder.tvId.setText(String.format(Locale.getDefault(), "#%d", pokemon.id));
                holder.tvName.setText(capitalize(pokemon.name));
                holder.ivSprite.setImageBitmap(imageManager.loadFromDisk(
                        context.getFilesDir() + getString(R.string.sprites_front),
                        pokemon.id + getString(R.string.extension)));

                //Initialize type1 TextView
                String type1 = pokemon.type1;
                type1 = capitalize(type1);
                String color1 = getString(R.string.color_type) + type1;
                int color1ID = getResources().getIdentifier(color1, "color", context.getPackageName());
                GradientDrawable bg1 = (GradientDrawable) holder.tvType1.getBackground();
                bg1.setColor(context.getColor(color1ID));
                bg1.setStroke((int) getResources().getDimension(R.dimen.stroke_tv_type), Color.WHITE);
                holder.tvType1.setText(type1.toUpperCase());

                String type2 = pokemon.type2;
                //Initialize type2 TextView if exists
                if (type2 != null) {
                    type2 = capitalize(type2);
                    String color2 = getString(R.string.color_type) + type2;
                    int color2ID = getResources().getIdentifier(color2, "color", context.getPackageName());
                    GradientDrawable bg2 = (GradientDrawable) holder.tvType2.getBackground();
                    bg2.setColor(context.getColor(color2ID));
                    bg2.setStroke((int) getResources().getDimension(R.dimen.stroke_tv_type), Color.WHITE);
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

            @NotNull
            @Override
            public CharSequence onChange(int i) {
                return String.valueOf(pokemonList.get(i).id);
            }
        }
    }
}
