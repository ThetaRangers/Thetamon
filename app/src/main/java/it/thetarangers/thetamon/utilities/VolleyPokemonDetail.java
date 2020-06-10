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

import org.json.JSONException;
import org.json.JSONObject;

import it.thetarangers.thetamon.R;
import it.thetarangers.thetamon.activities.PokemonDetailActivity;
import it.thetarangers.thetamon.model.Pokemon;

public abstract class VolleyPokemonDetail implements Response.ErrorListener, Response.Listener<String>{

    private Context context;

    public VolleyPokemonDetail(Context context){
        this.context = context;
    }

    public abstract Pokemon fill(Pokemon pokemon);

    public void getPokemonDetail(Pokemon pokemon){
        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                String.format(context.getString(R.string.url_species), pokemon.getId()),
                this,
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

        try {
            JSONObject jsonObject = new JSONObject(response);
            int genderRate = jsonObject.getInt("gender_rate");
            int captureRate = jsonObject.getInt("capture_rate");
            String growthRate = jsonObject.getJSONObject("growth_rate").getString("name");

            Log.d("POKE", "i'm searching, king");
            Log.d("POKE", String.format("%s genderRate:%s captureRate:%s grothRate:%s",
                    jsonObject.getString("name"), genderRate, captureRate, growthRate));

        } catch (JSONException exception) {
            exception.printStackTrace();
        }
    }
}
