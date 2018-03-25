package me.integrate.socialbank;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Map;

public class APICommunicator {

    private static final String API_URL = "http://sandshrew.fib.upc.edu:9000";


    public void postRequest(Context context, String url, Response.Listener responseListener, Response.ErrorListener errorListener, final Map<String, String> params){
        StringRequest postRequest = new StringRequest(Request.Method.POST, API_URL+url, responseListener, errorListener) {
            @Override
            protected Map<String, String> getParams()
            {
                return params;
            }
        };
        Volley.newRequestQueue(context).add(postRequest);

    }


}
