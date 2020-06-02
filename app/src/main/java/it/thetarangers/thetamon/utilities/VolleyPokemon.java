package it.thetarangers.thetamon.utilities;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import it.thetarangers.thetamon.model.Pokemon;
import it.thetarangers.thetamon.model.PokemonType;

public abstract class VolleyPokemon implements Response.ErrorListener, Response.Listener<String> {
    private final String URL = "https://pokeapi.co/api/v2/pokemon?limit=10000";
    private final String URL_TYPE = "https://pokeapi.co/api/v2/type/%s";
    private final int POKEDEX_LENGHT = 807;
    private final int TYPES_NUMBER = PokemonType.values().length;
    private final Context context;
    private List<Pokemon> pokemonList;

    private TypeListener listener;

    public VolleyPokemon(Context context) {
        this.context = context;
        listener = new TypeListener();
    }

    public abstract void fill(List<Pokemon> pokemonList);

    public void getPokemonList() {
        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                URL,
                this,
                this);
        requestQueue.add(stringRequest);
    }

    private void getPokemonListWithTypes() {
        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(context);

        for (PokemonType type : PokemonType.values()) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET,
                    String.format(URL_TYPE, type.name()),
                    listener,
                    this);
            requestQueue.add(stringRequest);
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        //TODO make error Response
        Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(String response) {
        Gson gson = new Gson();

        try {
            JSONObject jsonObject = new JSONObject(response);
            String result = jsonObject.getJSONArray("results").toString();

            Type listType = new TypeToken<List<Pokemon>>() {
            }.getType();    //Setting up the type for the conversion

            pokemonList = gson.fromJson(result, listType);
            pokemonList = pokemonList.subList(0, POKEDEX_LENGHT);

            Log.w("POKE", pokemonList.get(2).url);

            for (int i = 0; i < pokemonList.size(); i++) {
                pokemonList.get(i).setIdFromUrl();
            }

            //fill(pokemonList);
            getPokemonListWithTypes();
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
    }

    class TypeListener implements Response.Listener<String> {
        Gson gson;
        int num;

        TypeListener() {
            gson = new Gson();
            num = 0;
        }

        @Override
        public void onResponse(String response) {

            try {

                JSONObject jsonObject = new JSONObject(response);
                String name = jsonObject.getString("name");

                HashMap<Integer, Pokemon> pokemonHashMap = new HashMap<>();

                JSONArray pokemons = jsonObject.getJSONArray("pokemon");

                for (int i = 0; i < pokemons.length(); i++) {
                    JSONObject object = pokemons.getJSONObject(i);
                    JSONObject pokemon = object.getJSONObject("pokemon");

                    Pokemon temp = gson.fromJson(pokemon.toString(), Pokemon.class);
                    int slot = object.getInt("slot");

                    int id = temp.setIdFromUrl() - 1;

                    if (id < POKEDEX_LENGHT) {
                        pokemonList.get(id).setType(name, slot);
                    }

                }

                num++;

                if (num == TYPES_NUMBER) {
                    fill(pokemonList);
                }
            } catch (JSONException exception) {
                exception.printStackTrace();
            }
        }
    }
}

