package it.thetarangers.thetamon.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import it.thetarangers.thetamon.model.Pokemon;

public class PokemonListViewModel extends ViewModel {

    private MutableLiveData<List<Pokemon>> pokemons;

    public LiveData<List<Pokemon>> getPokemons() {
        if (pokemons == null) {
            pokemons = new MutableLiveData<>();
        }
        return pokemons;
    }

    public void setPokemons(List<Pokemon> pokemons) {
        this.pokemons.postValue(pokemons);
    }

    public void setPokemonsSynchronous(List<Pokemon> pokemons) {
        this.pokemons.setValue(pokemons);
    }

    public List<Pokemon> getPokemonList() {
        return pokemons.getValue();
    }

}
