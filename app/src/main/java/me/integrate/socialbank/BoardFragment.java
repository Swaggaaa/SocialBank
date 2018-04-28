package me.integrate.socialbank;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;

public class BoardFragment extends Fragment {

    private static final String URL = "/events";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List items = new ArrayList();

    private String title;
    private String initDate;
    private String place;
    private String finishDate;
    private String individual;
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
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        items.clear();
        inicialBoard(rootView);
        return rootView;
    }

    public void getAllEvents(List<Event> items) {

        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = (Response.Listener<CustomRequest.CustomResponse>) response -> {
            JSONArray jsonArray;
            try {
                jsonArray = new JSONArray(response.response);
                for ( int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    getInfoEvent(jsonObject);
                    Bitmap decodedByte = getImageFromString(photoEvent);

                    items.add(new Event(id, title, initDate, place, finishDate,"No", description, decodedByte));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            //TODO quitar
            Toast.makeText(getActivity().getApplicationContext(), "Recovery code email sent!", Toast.LENGTH_LONG).show();
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String message;
                int errorCode = error.networkResponse.statusCode;
                if (errorCode == 401)
                    message = "Unauthorized";
                else if(errorCode == 403)
                    message = "Forbidden";
                else if(errorCode == 404)
                    message = "Not Found";
                else
                    message = "Unexpected error";
                Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        };
        apiCommunicator.getRequest(getActivity().getApplicationContext(), URL, responseListener, errorListener,null);
    }

    private void getInfoEvent (JSONObject jsonObject) {

        try {

            id = Integer.parseInt(jsonObject.getString("id"));
            title = jsonObject.getString("title");

            initDate = jsonObject.getString("initDate");
            initDate = initDate.substring(8, 10) + "-" + initDate.substring(5, 7) + "-" +
                    initDate.substring(0, 4) + "  " + initDate.substring(11, 19);

            place = jsonObject.getString("place");

            finishDate = jsonObject.getString("endDate");
            finishDate = finishDate.substring(8, 10) + "-" + finishDate.substring(5, 7) + "-" +
                    finishDate.substring(0, 4) + "  " + finishDate.substring(11, 19);

            description = jsonObject.getString("description");

            photoEvent = jsonObject.getString("image");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Bitmap getImageFromString (String image) {
        byte[] decodeString = Base64.decode(image, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
        return decodedByte;

    }


    public void inicialBoard(View v) {

        //TODO Coger JSONObject y transformarlo la imagen bitmap
        getAllEvents(items);

        //TODO borrar
      /*  String ruben = ;
        Bitmap rub = getImageFromString(ruben);
        items.add(new Event(1,"Ferran", "BU", "Aqui", "3-4", "No","bu", R.drawable.ic_close));
        items.add(new Event( 2,"Sergio", "Guapo","Aqui", "3-4", "No", "bu", R.drawable.ab));
        items.add(new Event(3,"Sergi", "Guapo","Aqui", "3-4", "No", "bu", R.drawable.joan));
        items.add(new Event(3,"Sergi", "Guapo","Aqui", "3-4", "No", "bu", R.drawable.user_icon));*/


        mAdapter = new EventAdapter(items, getActivity(), new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                //TODO hacer comunicacion entre fragments
                // concuerda id con position
                Fragment eventFragment = new EventFragment();
                FragmentChangeListener fc = (FragmentChangeListener) getActivity();
                fc.replaceFragment(eventFragment);
            }
        });


        mRecyclerView.setAdapter(mAdapter);
    }

}
