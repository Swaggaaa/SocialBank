package me.integrate.socialbank;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class BoardFragment extends Fragment {


    private static final String URL = "/events";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    private String title;
    private String initDate;
    private String place;
    private String finishDate;
    private String description;
    private String photoEvent;
    private int id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_board, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        getAllEvents();
        return rootView;
    }

    //Call to the API
    public void getAllEvents() {

        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = (Response.Listener<CustomRequest.CustomResponse>) response -> {
            List<Event> items = new ArrayList<>();
            JSONArray jsonArray;
            try {
                jsonArray = new JSONArray(response.response);
                System.out.println(String.valueOf(jsonArray.length()));
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    getInfoEvent(jsonObject);
                    Bitmap decodedByte = getImageFromString(photoEvent);

                    //TODO: do it again
                    if (decodedByte != null) {
                        items.add(new Event(id, title, initDate, place, finishDate, "No", description, decodedByte));
                    } else
                        items.add(new Event(id, title, initDate, place, finishDate, "No", description, R.drawable.user_icon));

                }

                mAdapter = new EventAdapter(items, getActivity(), (v1, position) -> {
                });

                mRecyclerView.setAdapter(mAdapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            //TODO quitar
            Toast.makeText(getActivity().getApplicationContext(), "FUNCIONA!", Toast.LENGTH_LONG).show();
        };
        Response.ErrorListener errorListener = error -> {
            String message;
            int errorCode = error.networkResponse.statusCode;
            if (errorCode == 401)
                message = "Unauthorized";
            else if (errorCode == 403)
                message = "Forbidden";
            else if (errorCode == 404)
                message = "Not Found";
            else
                message = "Unexpected error";
            Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
        };
        apiCommunicator.getRequest(getActivity().getApplicationContext(), URL, responseListener, errorListener, null);
    }

    //Obtain info from the API, create Event and save in the list<Event>
    private void getInfoEvent(JSONObject jsonObject) {

        try {

            id = jsonObject.getInt("id");

            title = jsonObject.getString("title");

            initDate = jsonObject.getString("iniDate");
            if (!initDate.equals("null")) {
                initDate = initDate.substring(8, 10) + "-" + initDate.substring(5, 7) + "-" +
                        initDate.substring(0, 4);
            } else initDate = "No hay fecha";


            place = jsonObject.getString("location");

            finishDate = jsonObject.getString("endDate");
            if (!finishDate.equals("null")) {
                finishDate = finishDate.substring(8, 10) + "-" + finishDate.substring(5, 7) + "-" + finishDate.substring(0, 4);
            } else finishDate = "No hay fecha";

            description = jsonObject.getString("description");

            photoEvent = jsonObject.getString("image");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Transform and string base64 to bitmap
    private Bitmap getImageFromString(String image) {

        //TODO quitar
        if (!image.equals("")) {
            byte[] decodeString = Base64.decode(image, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
        }
        return null;

    }


}
