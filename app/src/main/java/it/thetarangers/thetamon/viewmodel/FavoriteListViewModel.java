package it.thetarangers.thetamon.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import it.thetarangers.thetamon.model.Pokemon;

public class FavoriteListViewModel extends ViewModel {

    private MutableLiveData<List<Pokemon>> pokemons;

    public LiveData<List<Pokemon>> getFavorites() {
        if (pokemons == null) {
            pokemons = new MutableLiveData<>();
        }
        return pokemons;
    }

    public void setFavorites(List<Pokemon> pokemons) {
        this.pokemons.setValue(pokemons);
    }

    public void setFavoritesAsynchronous(List<Pokemon> pokemons) {
        this.pokemons.postValue(pokemons);
    }

    public List<Pokemon> getFavoriteList() {
        return pokemons.getValue();
    }

}