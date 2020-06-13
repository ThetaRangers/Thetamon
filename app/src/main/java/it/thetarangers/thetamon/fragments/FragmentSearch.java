package it.thetarangers.thetamon.fragments;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Objects;

import it.thetarangers.thetamon.R;
import it.thetarangers.thetamon.database.DaoThread;
import it.thetarangers.thetamon.viewmodel.PokemonListViewModel;

public class FragmentSearch extends BottomSheetDialogFragment {

    public static String TAG = "FragmentSearch";

    PokemonListViewModel pokemonListViewModel;
    Holder holder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.search_sheet_layout, container, false);
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

    private void search(String query) {
        pokemonListViewModel.setFilters(new ArrayList<>());
        DaoThread daoThread = new DaoThread(pokemonListViewModel);
        try {
            // If the searched string is an int search by id
            daoThread.getPokemonFromId(getContext(), Integer.parseInt(query));
        } catch (NumberFormatException e) {
            // Otherwise search by name
            daoThread.getPokemonFromName(getContext(), query);
        }
    }

    class Holder implements EditText.OnEditorActionListener, View.OnClickListener {

        final TextInputLayout tilSearch;
        final ImageView ivSearch;

        public Holder(View fs) {
            tilSearch = fs.findViewById(R.id.tilSearch);
            Objects.requireNonNull(tilSearch.getEditText()).setOnEditorActionListener(this);

            ivSearch = fs.findViewById(R.id.ivSearch);
            ivSearch.setOnClickListener(this);
        }

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search(Objects.requireNonNull(tilSearch.getEditText()).getText().toString());
            }
            return false;
        }

        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.ivSearch) {
                Objects.requireNonNull(tilSearch.getEditText())
                        .onEditorAction(EditorInfo.IME_ACTION_DONE);
                FragmentSearch.this.dismiss();
            }

        }

    }

}
