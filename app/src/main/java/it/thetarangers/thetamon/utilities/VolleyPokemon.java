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

import java.lang.reflect.Type;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import it.thetarangers.thetamon.model.Pokemon;

public abstract class VolleyPokemon implements Response.ErrorListener, Response.Listener<String>{
    private final String URL = "https://pokeapi.co/api/v2/pokemon?limit=10000";
    private final int POKEDEX_LENGHT = 807;
    private final Context context;

    public abstract void fill(List<Pokemon> pokemonList);

    public VolleyPokemon(Context context){
        this.context = context;
    }

    public void getPokemonList(){
        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                URL,
                this,
                this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        //TODO make error Response
        Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(String response) {
        Gson gson = new Gson();

        try{
            JSONObject jsonObject = new JSONObject(response);
            String result = jsonObject.getJSONArray("results").toString();

            Type listType = new TypeToken<List<Pokemon>>() {
            }.getType();    //Setting up the type for the conversion

            List<Pokemon> pokemonList = gson.fromJson(result, listType);
            pokemonList = pokemonList.subList(0, POKEDEX_LENGHT);
            for(int i = 0; i < pokemonList.size(); i++){
                pokemonList.get(i).setId(i+1);
            }

            fill(pokemonList);
        } catch (JSONException exception){
            exception.printStackTrace();
        }
    }
}
