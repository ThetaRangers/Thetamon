package it.thetarangers.thetamon.utilities;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.room.Ignore;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.thetarangers.thetamon.R;
import it.thetarangers.thetamon.model.Move;
import it.thetarangers.thetamon.model.Pokemon;

public abstract class VolleyPokemonDetail implements Response.ErrorListener, Response.Listener<String> {

    private Context context;
    private RequestQueue requestQueue;

    public VolleyPokemonDetail(Context context) {
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
    }

    public abstract void fill(Pokemon pokemon);

    public void getPokemonDetail(Pokemon pokemon) {
        Log.d("POKE", "url " + pokemon.getUrl());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, pokemon.getUrl(),
                this,
                this);
        requestQueue.add(stringRequest);
    }

    public void getPokemonSpeciesDetail(Pokemon pokemon) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                String.format(context.getString(R.string.url_species), pokemon.getId()),
                new SpeciesListener(pokemon),
                this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        //TODO replace king
        Toast.makeText(context, "Stringa di errore quì king", Toast.LENGTH_SHORT);
    }

    @Override
    public void onResponse(String response) {
        Gson gson = new Gson();
        List<Move> moveList = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(response);

            JSONArray moves = jsonObject.getJSONArray("moves");
            for (int i = 0; i < moves.length(); i++) {
                JSONObject tempObj = moves.getJSONObject(i);
                JSONObject tempMove = tempObj.getJSONObject("move");

                Move temp = gson.fromJson(tempMove.toString(), Move.class);
                moveList.add(temp);
            }

            JSONObject spritesObj = jsonObject.getJSONObject("sprites");

            HashMap<String, HashMap<String, String>> sprites = new HashMap<>();

            HashMap<String, String> back = new HashMap<>();
            back.put("default", spritesObj.getString("back_default"));
            back.put("default_female", spritesObj.getString("back_female"));
            back.put("shiny", spritesObj.getString("back_shiny"));
            back.put("shiny_female", spritesObj.getString("back_shiny_female"));
            sprites.put("back", back);

            HashMap<String, String> front = new HashMap<>();
            back.put("default", spritesObj.getString("front_default"));
            back.put("default_female", spritesObj.getString("front_female"));
            back.put("shiny", spritesObj.getString("front_shiny"));
            back.put("shiny_female", spritesObj.getString("front_female"));
            sprites.put("front", front);


        } catch (JSONException exception) {
            exception.printStackTrace();
        }
    }

    class SpeciesListener implements Response.Listener<String> {
        Pokemon pokemon;

        SpeciesListener(Pokemon pokemon) {
            this.pokemon = pokemon;
        }

        @Override
        public void onResponse(String response) {

            try {
                JSONObject jsonObject = new JSONObject(response);
                int genderRate = jsonObject.getInt("gender_rate");
                int captureRate = jsonObject.getInt("capture_rate");
                String growthRate = jsonObject.getJSONObject("growth_rate").getString("name");

            } catch (JSONException exception) {
                exception.printStackTrace();
            }
        }
    }
}
