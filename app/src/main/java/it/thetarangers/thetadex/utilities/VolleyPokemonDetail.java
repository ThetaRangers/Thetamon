package it.thetarangers.thetadex.utilities;

import android.content.Context;
import android.widget.Toast;

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
import java.util.List;

import it.thetarangers.thetadex.R;
import it.thetarangers.thetadex.model.Ability;
import it.thetarangers.thetadex.model.Move;
import it.thetarangers.thetadex.model.Pokemon;

public abstract class VolleyPokemonDetail implements Response.ErrorListener, Response.Listener<String> {

    private Context context;
    private RequestQueue requestQueue;
    private Pokemon pokemon;

    public VolleyPokemonDetail(Context context, Pokemon pokemon) {
        this.context = context;
        this.pokemon = pokemon;
        requestQueue = Volley.newRequestQueue(context);
    }

    public abstract void fill(Pokemon pokemon);

    public void getPokemonDetail() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, pokemon.getUrl(),
                this,
                this);
        requestQueue.add(stringRequest);
    }

    private void getPokemonSpeciesDetail() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                String.format(context.getString(R.string.url_species), pokemon.getId()),
                new SpeciesListener(pokemon),
                this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(context, R.string.volley_error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(String response) {
        Gson gson = new Gson();
        List<Move> moveList = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(response);

            // Get moves list
            JSONArray moves = jsonObject.getJSONArray("moves");
            for (int i = 0; i < moves.length(); i++) {
                // Parse the moves
                JSONObject tempObj = moves.getJSONObject(i);
                JSONObject tempMove = tempObj.getJSONObject("move");

                Move temp = gson.fromJson(tempMove.toString(), Move.class);

                JSONObject levelObj = tempObj.getJSONArray("version_group_details").getJSONObject(0);
                temp.setLevel(levelObj.getInt("level_learned_at"));
                temp.setLearnMethod(levelObj.getJSONObject("move_learn_method").getString("name"));

                moveList.add(temp);
            }

            pokemon.setMovesList(moveList);

            pokemon.setWeight(jsonObject.getInt("height"));
            pokemon.setHeight(jsonObject.getInt("weight"));


            //Get abilities
            JSONArray abilityJson = jsonObject.getJSONArray("abilities");
            List<Ability> abilityList = new ArrayList<>();
            for (int i = 0; i < abilityJson.length(); i++) {
                // Parse the abilities
                JSONObject tempObj = abilityJson.getJSONObject(i);
                JSONObject tempAbility = tempObj.getJSONObject("ability");

                Ability temp = gson.fromJson(tempAbility.toString(), Ability.class);
                abilityList.add(temp);
            }

            pokemon.setAbilityList(abilityList);

            //Get stats
            JSONArray statJson = jsonObject.getJSONArray("stats");
            List<Integer> statList = new ArrayList<>();
            for (int i = 0; i < statJson.length(); i++) {
                JSONObject tempObj = statJson.getJSONObject(i);

                statList.add(tempObj.getInt("base_stat"));
            }

            pokemon.setStats(statList.get(0), statList.get(1), statList.get(2), statList.get(3),
                    statList.get(4), statList.get(5));

            // When done fetching data call the api for the species details
            getPokemonSpeciesDetail();
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
    }

    // Listener for the species call
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
                String habitat;

                String evolutionChain = jsonObject.getJSONObject("evolution_chain").getString("url");

                if (jsonObject.isNull("habitat")) {
                    habitat = context.getResources().getString(R.string.no_habitat);
                } else {
                    habitat = jsonObject.getJSONObject("habitat").getString("name");
                }

                String lang = context.getResources().getString(R.string.localization);

                JSONArray flavorTexts = jsonObject.getJSONArray("flavor_text_entries");
                for (int i = flavorTexts.length() - 1; i >= 0; i--) {
                    JSONObject tmp = flavorTexts.getJSONObject(i);

                    if (lang.equals(tmp.getJSONObject("language").getString("name"))) {
                        pokemon.setFlavorText(String.format("%s %s: %s",
                                tmp.getString("flavor_text"),
                                context.getString(R.string.version),
                                tmp.getJSONObject("version").getString("name")));
                        break;
                    }
                }

                pokemon.setGenderRate(genderRate);
                pokemon.setCaptureRate(captureRate);
                pokemon.setGrowthRate(growthRate);
                pokemon.setHabitat(habitat);
                pokemon.setUrlEvolutionChain(evolutionChain);

                // Call the abstract method
                fill(pokemon);
            } catch (JSONException exception) {
                exception.printStackTrace();
            }
        }
    }
}
