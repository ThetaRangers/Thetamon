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

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import it.thetarangers.thetamon.R;
import it.thetarangers.thetamon.model.Move;

public abstract class VolleyMoves implements Response.ErrorListener, Response.Listener<String> {
    private Context context;

    public VolleyMoves(Context context) {
        this.context = context;
    }

    public abstract void fill(List<Move> moves);

    public void getAllMoves() {
        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                context.getString(R.string.url_all_moves),
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
        List<Move> moveList;

        try {
            JSONObject jsonObject = new JSONObject(response);
            String result = jsonObject.getJSONArray("results").toString();

            Type listType = new TypeToken<List<Move>>() {
            }.getType();    //Setting up the type for the conversion

            moveList = gson.fromJson(result, listType);

            fill(moveList);
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
    }
}
