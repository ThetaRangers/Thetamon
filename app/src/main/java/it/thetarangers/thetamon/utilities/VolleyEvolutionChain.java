package it.thetarangers.thetamon.utilities;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import it.thetarangers.thetamon.R;
import it.thetarangers.thetamon.model.Pokemon;

public abstract class VolleyEvolutionChain implements Response.ErrorListener, Response.Listener<String> {
    Context context;

    public abstract void fill(Pokemon pokemon);

    VolleyEvolutionChain(Context context){
        this.context = context;
    }

    public void getEvolutionChain(Pokemon pokemon, String chain){
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

    }

    @Override
    public void onResponse(String response) {

    }
}
