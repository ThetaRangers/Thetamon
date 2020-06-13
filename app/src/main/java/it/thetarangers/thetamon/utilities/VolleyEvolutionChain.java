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

    public abstract void fill(List<EvolutionDetail> evolutionDetails);

    public void getEvolutionChain(Pokemon pokemon, String chain) {
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
                if(thirdEvolutionJson.length() != 0) {
                    List<EvolutionDetail> thirdEvolutions = new ArrayList<>();

                    for(int j = 0; j < thirdEvolutionJson.length(); j++) {
                        JSONObject object = thirdEvolutionJson.getJSONObject(j);

                        EvolutionDetail evolutionDetail = parseEvolutionDetail(object);

                        thirdEvolutions.add(evolutionDetail);
                    }

                    ev.setNextPokemon(thirdEvolutions);
                }

                secondEvolutions.add(ev);
            }

            firstEvolution.setNextPokemon(secondEvolutions);

            Log.d("POKE", "Numero Evoluzioni 1:" + 1 + " 2: " + firstEvolution.getNextPokemon().size());

        } catch (JSONException exception) {
            exception.printStackTrace();
        }
    }

    private EvolutionDetail parseEvolutionDetail(JSONObject obj) throws JSONException {

        JSONObject tmp = obj.getJSONArray("evolution_details").getJSONObject(0);

        EvolutionDetail ev = gson.fromJson(tmp.toString(), EvolutionDetail.class);

        ev.setName(obj.getJSONObject("species").getString("name"));

        return ev;
    }
}
