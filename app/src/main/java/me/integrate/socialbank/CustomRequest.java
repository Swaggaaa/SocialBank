package me.integrate.socialbank;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class CustomRequest extends Request<CustomRequest.CustomResponse> {


    private Response.Listener<CustomResponse> mListener;

    public CustomRequest(int method, String url, Response.Listener<CustomResponse> responseListener, Response.ErrorListener listener) {
        super(method, url, listener);
        this.mListener = responseListener;
    }


    @Override
    protected void deliverResponse(CustomResponse response) {
        this.mListener.onResponse(response);
    }

    @Override
    protected Response<CustomResponse> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }

        CustomResponse customResponse = new CustomResponse();
        customResponse.headers = response.headers;
        customResponse.response = parsed;

        return Response.success(customResponse, HttpHeaderParser.parseCacheHeaders(response));
    }


    public static class CustomResponse {
        Map<String, String> headers;
        String response;
    }

}