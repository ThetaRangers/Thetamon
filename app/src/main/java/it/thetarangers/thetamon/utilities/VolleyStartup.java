package it.thetarangers.thetamon.utilities;

import android.content.Context;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.thetarangers.thetamon.R;
import it.thetarangers.thetamon.model.Move;
import it.thetarangers.thetamon.model.MoveDamageClass;
import it.thetarangers.thetamon.model.Pokemon;
import it.thetarangers.thetamon.model.PokemonType;

public abstract class VolleyStartup implements Response.ErrorListener, Response.Listener<String> {
    private final int POKEDEX_LENGTH = 807;
    private final int TYPES_NUMBER = PokemonType.values().length;
    private final int DAMAGE_CLASSES_NUMBER = MoveDamageClass.values().length;
    private final Context context;
    private List<Pokemon> pokemonList;
    private List<Move> moveList = new ArrayList<>();

    private TypeListener listener;
    private DamageClassListener damageClassListener;

    public VolleyStartup(Context context) {
        this.context = context;
        listener = new TypeListener();
        damageClassListener = new DamageClassListener();
    }

    public abstract void fill(List<Pokemon> pokemonList, List<Move> moveList);

    public void getPokemonList() {
        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                context.getString(R.string.url),
                this,
                this);
        requestQueue.add(stringRequest);
    }

    private void getPokemonListWithTypes() {
        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(context);

        for (PokemonType type : PokemonType.values()) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET,
                    String.format(context.getString(R.string.url_type), type.name()),
                    listener,
                    this);
            requestQueue.add(stringRequest);
        }
    }

    private void getMovesListWithDamageClass() {
        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(context);

        for (MoveDamageClass damageClass : MoveDamageClass.values()) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET,
                    String.format(context.getString(R.string.url_damage_classes),
                            damageClass.getValue()),
                    damageClassListener,
                    this);
            requestQueue.add(stringRequest);
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(context, R.string.volley_error, Toast.LENGTH_LONG).show();
    }

    // Parse the pokemon list
    @Override
    public void onResponse(String response) {
        Gson gson = new Gson();

        try {
            JSONObject jsonObject = new JSONObject(response);
            String result = jsonObject.getJSONArray("results").toString();

            Type listType = new TypeToken<List<Pokemon>>() {
            }.getType();    //Setting up the type for the conversion

            pokemonList = gson.fromJson(result, listType);
            pokemonList = pokemonList.subList(0, POKEDEX_LENGTH);

            for (int i = 0; i < pokemonList.size(); i++) {
                pokemonList.get(i).setIdFromUrl();
            }

            getPokemonListWithTypes();
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
    }

    // Listener for the type api call
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

                JSONArray pokemons = jsonObject.getJSONArray("pokemon");

                for (int i = 0; i < pokemons.length(); i++) {
                    // Get the pokemon types
                    JSONObject object = pokemons.getJSONObject(i);
                    JSONObject pokemon = object.getJSONObject("pokemon");

                    Pokemon temp = gson.fromJson(pokemon.toString(), Pokemon.class);
                    int slot = object.getInt("slot");

                    int id = temp.setIdFromUrl() - 1;

                    if (id < POKEDEX_LENGTH) {
                        // Set the pokemon type
                        pokemonList.get(id).setType(name, slot);
                    }
                }

                String moves = jsonObject.getJSONArray("moves").toString();
                Type listType = new TypeToken<List<Move>>() {
                }.getType();    //Setting up the type for the conversion

                List<Move> moveTmp;
                moveTmp = gson.fromJson(moves, listType);
                for (Move m : moveTmp) {
                    // Set type of the move
                    m.setIdFromUrl();
                    m.setType(name);
                }

                moveList.addAll(moveTmp);

                num++;

                if (num == TYPES_NUMBER) {
                    // If all api call are done fetch moves damage class
                    Collections.sort(moveList);
                    getMovesListWithDamageClass();
                }
            } catch (JSONException exception) {
                exception.printStackTrace();
            }
        }
    }

    // Listener for the damage class api call
    class DamageClassListener implements Response.Listener<String> {
        Gson gson = new Gson();
        int num = 0;

        @Override
        public void onResponse(String response) {

            try {
                JSONObject jsonObject = new JSONObject(response);
                String name = jsonObject.getString("name");

                JSONArray moves = jsonObject.getJSONArray("moves");

                for (int i = 0; i < moves.length(); i++) {
                    JSONObject move = moves.getJSONObject(i);
                    Move temp = gson.fromJson(move.toString(), Move.class);

                    int index = temp.setIdFromUrl() - 1;

                    if (index < 10000) // Ignore shadow moves
                        moveList.get(index).setDamageClass(name);   // Add damage type to moves

                }

                num++;

                if (num == DAMAGE_CLASSES_NUMBER) {
                    // Call the abstract method when done
                    fill(pokemonList, moveList);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}

