package it.thetarangers.thetamon.utilities;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.thetarangers.thetamon.R;
import it.thetarangers.thetamon.model.EvolutionDetail;

public abstract class VolleyEvolutionChain implements Response.ErrorListener, Response.Listener<String> {
    Context context;

    protected VolleyEvolutionChain(Context context) {
        this.context = context;
    }

    public abstract void fill(EvolutionDetail evolutionDetail);

    public void getEvolutionChain(String chain) {
        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                chain,
                this,
                this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(context, R.string.volley_error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);

            JSONObject chain = jsonObject.getJSONObject("chain");

            // First pokemon
            EvolutionDetail firstEvolution = new EvolutionDetail();
            // Get the first pokemon name
            firstEvolution.setName(chain.getJSONObject("species").getString("name"));

            // Second evolution
            JSONArray secondEvolvesTo = chain.getJSONArray("evolves_to");

            // Check if there are evolutions
            if (secondEvolvesTo.length() != 0) {
                List<EvolutionDetail> secondEvolutions = new ArrayList<>();

                for (int i = 0; i < secondEvolvesTo.length(); i++) {
                    JSONObject obj = secondEvolvesTo.getJSONObject(i);

                    EvolutionDetail ev = parseEvolutionDetail(obj);

                    //Third evolutions
                    JSONArray thirdEvolutionJson = obj.getJSONArray("evolves_to");
                    if (thirdEvolutionJson.length() != 0) {
                        List<EvolutionDetail> thirdEvolutions = new ArrayList<>();

                        for (int j = 0; j < thirdEvolutionJson.length(); j++) {
                            JSONObject object = thirdEvolutionJson.getJSONObject(j);

                            EvolutionDetail evolutionDetail = parseEvolutionDetail(object);

                            thirdEvolutions.add(evolutionDetail);
                        }

                        ev.setNextPokemon(thirdEvolutions);
                    }

                    secondEvolutions.add(ev);
                }
                // Add the evolutions to the first pokemon
                firstEvolution.setNextPokemon(secondEvolutions);
            }

            fill(firstEvolution);
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
    }

    private EvolutionDetail parseEvolutionDetail(JSONObject obj) throws JSONException {

        JSONObject tmp = obj.getJSONArray("evolution_details").getJSONObject(0);

        EvolutionDetail ev = new EvolutionDetail();

        // needs a special location
        if (!tmp.isNull("location")) {
            ev.setLocationName(context.getResources().getString(R.string.special_location));
            ev.addCondition(context.getResources().getString(R.string.evo_location));
        }

        // knowing a move of type XX
        if (!tmp.isNull("known_move_type")) {
            String knownMoveType = tmp.getJSONObject("known_move_type").getString("name");
            ev.setKnown_move_type(knownMoveType);
            ev.addCondition(StringManager.formatFromR(context, R.string.evo_known_type_move, knownMoveType));
        }

        // gender is XX
        if (!tmp.isNull("gender")) {
            Integer gender = tmp.getInt("gender");
            ev.setGender(gender);
            String genderFormat = "";
            switch (gender) {
                case 1:
                    genderFormat = "female";
                    break;
                case 2:
                    genderFormat = "male";
                    break;
            }
            ev.addCondition(StringManager.formatFromR(context, R.string.evo_gender, genderFormat));

        }

        // helding item XX
        if (!tmp.isNull("held_item")) {
            String heldItem = tmp.getJSONObject("held_item").getString("name");
            ev.setHeld_item(heldItem);
            ev.addCondition(StringManager.formatFromR(context, R.string.evo_held_item, heldItem));
        }

        // item XX
        if (!tmp.isNull("item")) {
            String item = tmp.getJSONObject("item").getString("name");
            ev.setItem(item);
            ev.addCondition(StringManager.formatFromR(context, R.string.evo_item, item));
        }

        // knowing the move XX
        if (!tmp.isNull("known_move")) {
            String knownMove = tmp.getJSONObject("known_move").getString("name");
            ev.setKnown_move(knownMove);
            ev.addCondition(StringManager.formatFromR(context, R.string.evo_knwon_move, knownMove));
        }

        // at affection XX
        if (!tmp.isNull("min_affection")) {
            int minAffection = tmp.getInt("min_affection");
            ev.setMin_affection(minAffection);
            ev.addCondition(StringManager.formatFromR(context, R.string.evo_affection, minAffection));
        }

        // at beauty XX
        if (!tmp.isNull("min_beauty")) {
            int minBeauty = tmp.getInt("min_beauty");
            ev.setMin_beauty(minBeauty);
            ev.addCondition(StringManager.formatFromR(context, R.string.evo_beauty, minBeauty));
        }

        // at happiness XX
        if (!tmp.isNull("min_happiness")) {
            int minHappiness = tmp.getInt("min_happiness");
            ev.setMin_happiness(minHappiness);
            ev.addCondition(StringManager.formatFromR(context, R.string.evo_happiness, minHappiness));
        }

        // At level XX
        if (!tmp.isNull("min_level")) {
            int minLevel = tmp.getInt("min_level");
            ev.setMin_level(minLevel);
            ev.addCondition(StringManager.formatFromR(context, R.string.evo_level_up, minLevel));
        }

        // a XX is in the party
        if (!tmp.isNull("party_species")) {
            String partySpecies = tmp.getJSONObject("party_species").getString("name");
            ev.setParty_species(partySpecies);
            ev.addCondition(String.format(context.getResources().getString(R.string.evo_party_specie), partySpecies));
        }

        // a pokemon in the party is type XX
        if (!tmp.isNull("party_type")) {
            String partyType = tmp.getJSONObject("party_type").getString("name");
            ev.setParty_type(partyType);
            ev.addCondition(StringManager.formatFromR(context, R.string.evo_party_type, partyType));
        }

        // attack is higher than defense
        if (!tmp.isNull("relative_physical_stats")) {
            Integer relative = tmp.getInt("relative_physical_stats");
            ev.setRelative_physical_stats(relative);
            switch (relative) {
                case 1:
                    ev.addCondition(context.getResources().getString(R.string.evo_relative_1));
                    break;
                case -1:
                    ev.addCondition(context.getResources().getString(R.string.evo_relative_minus1));
                    break;
                case 0:
                    ev.addCondition(context.getResources().getString(R.string.evo_relative_0));
                    break;
            }
        }

        // time of day XX
        String timeOfDay = tmp.getString("time_of_day");
        if (!timeOfDay.equals("")) {
            ev.setTime_of_day(timeOfDay);
            ev.addCondition(StringManager.formatFromR(context, R.string.evo_time_of_day, timeOfDay));

        }

        // traded with XX
        if (!tmp.isNull("trade_species")) {
            String tradeSpecie = tmp.getJSONObject("trade_species").getString("name");
            ev.setTrade_species(tradeSpecie);
            ev.addCondition(StringManager.formatFromR(context, R.string.evo_trade_specie, tradeSpecie));
        }

        // don't care
        if (!tmp.isNull("trigger")) {
            ev.setTrigger(tmp.getJSONObject("trigger").getString("name"));
        }

        // while console is upside down
        Boolean turnUpsideDown = tmp.getBoolean("turn_upside_down");
        ev.setTurn_upside_down(turnUpsideDown);
        if (turnUpsideDown) {
            ev.addCondition(context.getResources().getString(R.string.evo_upside_down));
        }

        // while it's raining
        Boolean overworldRain = tmp.getBoolean("needs_overworld_rain");
        ev.setNeeds_overworld_rain(overworldRain);
        if (overworldRain) {
            ev.addCondition(context.getResources().getString(R.string.evo_overworld_rain));
        }

        ev.setName(obj.getJSONObject("species").getString("name"));

        return ev;
    }
}
