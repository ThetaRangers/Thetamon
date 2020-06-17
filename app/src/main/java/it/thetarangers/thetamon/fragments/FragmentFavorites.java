package it.thetarangers.thetamon.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.thetarangers.thetamon.R;
import it.thetarangers.thetamon.model.Pokemon;

public class FragmentFavorites extends Fragment {
    Holder holder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        holder = new Holder(view);
    }

    class Holder {
        Holder(View view) {

        }
    }

    class FavoriteAdapter extends RecyclerView.Adapter<ViewHolder> {
        private List<Pokemon> pokemonList;

        FavoriteAdapter(List<Pokemon> pokemonList) {
            this.pokemonList = pokemonList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return pokemonList.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
