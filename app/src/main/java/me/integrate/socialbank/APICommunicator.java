package me.integrate.socialbank;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

class APICommunicator {

    private static final String API_URL = "http://sandshrew.fib.upc.edu:9000";
    private static final String CONTENT_TYPE = "application/json; charset=utf-8";
    private static final String CHARSET = "utf-8";


    void getRequest(Context context, String url, Response.Listener responseListener, Response.ErrorListener errorListener, final Map<String, String> params) {
        doRequest(context, Request.Method.GET, url, responseListener, errorListener, params);
    }

    void postRequest(Context context, String url, Response.Listener responseListener, Response.ErrorListener errorListener, final Map<String, String> params) {
        doRequest(context, Request.Method.POST, url, responseListener, errorListener, params);
    }

    void putRequest(Context context, String url, Response.Listener responseListener, Response.ErrorListener errorListener, final Map<String, String> params) {
        doRequest(context, Request.Method.PUT, url, responseListener, errorListener, params);
    }

    void deleteRequest(Context context, String url, Response.Listener responseListener, Response.ErrorListener errorListener, final Map<String, String> params) {
        doRequest(context, Request.Method.DELETE, url, responseListener, errorListener, params);
    }

    private void doRequest(Context context, final int post, final String url, final Response.Listener responseListener, final Response.ErrorListener errorListener, final Map<String, String> params) {
        CustomRequest postRequest = new CustomRequest(post, API_URL + url, responseListener, errorListener) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return new JSONObject(params).toString().getBytes(CHARSET);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public String getBodyContentType() {
                return CONTENT_TYPE;
            }
        };
        Volley.newRequestQueue(context).add(postRequest);
    }

}
