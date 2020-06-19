package it.thetarangers.thetamon.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import it.thetarangers.thetamon.model.Pokemon;

public class PokemonListViewModel extends ViewModel {

    private MutableLiveData<List<Pokemon>> pokemons;
    private MutableLiveData<List<String>> filters;

    public LiveData<List<Pokemon>> getPokemons() {
        if (pokemons == null) {
            pokemons = new MutableLiveData<>();
        }
        return pokemons;
    }

    public void setPokemons(List<Pokemon> pokemons) {
        this.pokemons.setValue(pokemons);
    }

    public LiveData<List<String>> getFilters() {
        if (filters == null) {
            filters = new MutableLiveData<>();
        }
        return filters;
    }

    public void setFilters(List<String> filters) {
        this.filters.setValue(filters);
    }

    public void setPokemonsAsynchronous(List<Pokemon> pokemons) {
        this.pokemons.postValue(pokemons);
    }

    public List<Pokemon> getPokemonList() {
        return pokemons.getValue();
    }

    public List<String> getFilterList() {
        return filters.getValue();
    }

}
