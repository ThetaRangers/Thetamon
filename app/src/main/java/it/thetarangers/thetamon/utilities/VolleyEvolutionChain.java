package it.thetarangers.thetamon.utilities;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.apache.commons.io.output.ThresholdingOutputStream;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.thetarangers.thetamon.R;
import it.thetarangers.thetamon.model.EvolutionDetail;
import it.thetarangers.thetamon.model.Pokemon;

public abstract class VolleyEvolutionChain implements Response.ErrorListener, Response.Listener<String> {
    Context context;
    Gson gson = new Gson();

    protected VolleyEvolutionChain(Context context) {
        this.context = context;
    }

    public abstract void fill(EvolutionDetail evolutionDetail);

    public void getEvolutionChain(String chain) {
        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(context);

        Log.d("POKE", chain);

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
        List<EvolutionDetail> evolutions = null;

        try {
            Log.d("POKE", "Madonna se trovo eevee");

            JSONObject jsonObject = new JSONObject(response);

            JSONObject chain = jsonObject.getJSONObject("chain");

            //First evolution
            EvolutionDetail firstEvolution = new EvolutionDetail();
            firstEvolution.setName(chain.getJSONObject("species").getString("name"));

            //Second evolution
            JSONArray secondEvolvesTo = chain.getJSONArray("evolves_to");

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

            firstEvolution.setNextPokemon(secondEvolutions);

            fill(firstEvolution);
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
    }

    private EvolutionDetail parseEvolutionDetail(JSONObject obj) throws JSONException {

        JSONObject tmp = obj.getJSONArray("evolution_details").getJSONObject(0);

        EvolutionDetail ev = new EvolutionDetail();

        if (!tmp.isNull("location")) {
            ev.setLocationName(context.getResources().getString(R.string.special_location));
            ev.addCondition(context.getResources().getString(R.string.special_location));
        }

        if (!tmp.isNull("known_move_type")) {
            ev.setKnown_move_type(tmp.getJSONObject("known_move_type").getString("name"));
        }

        if (!tmp.isNull("gender")) {
            ev.setGender(tmp.getInt("gender"));
        }

        if (!tmp.isNull("held_item")) {
            ev.setHeld_item(tmp.getJSONObject("held_item").getString("name"));
        }

        if (!tmp.isNull("item")) {
            ev.setItem(tmp.getJSONObject("item").getString("name"));
        }

        if (!tmp.isNull("known_move")) {
            ev.setKnown_move(tmp.getJSONObject("known_move").getString("name"));
        }

        // at affection XX
        if (!tmp.isNull("min_affection")) {
            Integer minAffection = tmp.getInt("min_affection");
            ev.setMin_affection(minAffection);
            ev.addCondition(StringManager.formatFromR(context, R.string.evo_affection, minAffection));
        }

        // at beauty XX
        if (!tmp.isNull("min_beauty")) {
            Integer minBeauty = tmp.getInt("min_beauty");
            ev.setMin_beauty(minBeauty);
            ev.addCondition(StringManager.formatFromR(context, R.string.evo_beauty, minBeauty));
        }

        // at happiness XX
        if (!tmp.isNull("min_happiness")) {
            Integer minHappiness = tmp.getInt("min_happinness");
            ev.setMin_happiness(minHappiness);
            ev.addCondition(StringManager.formatFromR(context, R.string.evo_happiness, minHappiness));
        }

        // At level XX
        if (!tmp.isNull("min_level")) {
            Integer minLevel = tmp.getInt("min_level");
            ev.setMin_level(minLevel);
            ev.addCondition(StringManager.formatFromR(context, R.string.evo_level_up, minLevel));
        }

        if (!tmp.isNull("party_species")) {
            ev.setParty_species(tmp.getJSONObject("party_species").getString("party_species"));
        }

        if (!tmp.isNull("party_type")) {
            ev.setParty_type(tmp.getJSONObject("party_type").getString("party_type"));
        }

        if (!tmp.isNull("relative_physical_stats")) {
            ev.setRelative_physical_stats(tmp.getInt("relative_physical_stats"));
        }

        String timeOfDay = tmp.getString("time_of_day");
        if (!timeOfDay.equals("")) {
            ev.setTime_of_day(timeOfDay);
        }

        if (!tmp.isNull("trade_species")) {
            ev.setTrade_species(tmp.getJSONObject("trade_species").getString("name"));
        }

        if (!tmp.isNull("trigger")) {
            ev.setTrigger(tmp.getJSONObject("trigger").getString("name"));
        }

        Log.d("POKE", tmp.getJSONObject("trigger").getString("name"));

        ev.setTurn_upside_down(tmp.getBoolean("turn_upside_down"));
        ev.setNeeds_overworld_rain(tmp.getBoolean("needs_overworld_rain"));
        ev.setName(obj.getJSONObject("species").getString("name"));

        return ev;
    }
}
