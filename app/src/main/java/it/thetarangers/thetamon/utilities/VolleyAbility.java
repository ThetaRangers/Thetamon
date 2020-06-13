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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.thetarangers.thetamon.R;
import it.thetarangers.thetamon.model.Ability;

public abstract class VolleyAbility implements Response.ErrorListener, Response.Listener<String> {
    private Context context;

    public VolleyAbility(Context context) {
        this.context = context;
    }

    public abstract void fill(Ability ability);

    public void getAbilityDetail(Ability ability) {
        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                context.getString(R.string.url_ability_detail) + ability.getName(),
                this,
                this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(context, R.string.volley_error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(String response) {
        Gson gson = new Gson();

        try {
            JSONObject jsonObject = new JSONObject(response);
            String lang = context.getResources().getString(R.string.localization);

            Ability ability = gson.fromJson(response, Ability.class);

            JSONArray effects = jsonObject.getJSONArray("effect_entries");
            JSONObject effectObj = null;

            for (int index = 0; index < effects.length(); index++) {
                effectObj = effects.getJSONObject(index);
                if (lang.equals(effectObj.getJSONObject("language").getString("name"))) {
                    String effect = effectObj.getString("effect");
                    ability.setEffect(effect);
                    break;
                }
            }


            JSONArray flavorTexts = jsonObject.getJSONArray("flavor_text_entries");
            for (int i = flavorTexts.length() - 1; i >= 0; i--) {
                JSONObject tmp = flavorTexts.getJSONObject(i);

                if (lang.equals(tmp.getJSONObject("language").getString("name"))) {
                    ability.setFlavor_text(String.format("%s version: %s",
                            tmp.getString("flavor_text"), tmp.getJSONObject("version_group").getString("name")));
                    break;
                }
            }

            fill(ability);
        } catch (JSONException exception) {
            exception.printStackTrace();
        }


    }

}
