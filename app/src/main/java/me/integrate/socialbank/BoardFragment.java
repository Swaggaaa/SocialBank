package me.integrate.socialbank;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BoardFragment extends Fragment {

    private static final String URL = "/events";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List items = new ArrayList();

    private String title;
    private String hour;
    private String place;
    private String date;
    private String individual;
    private String photoEvent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_board, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        items = getAllEvents();
        inicialBoard(items);
        return rootView;
    }

    public List getAllEvents() {
        List allEvents = new ArrayList();
        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = (Response.Listener<CustomRequest.CustomResponse>) response -> {
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(response.response);
                title = jsonObject.getString("title");
                hour = jsonObject.getString("hours");
                place = jsonObject.getString("place");
                date = jsonObject.getString("iniDate") + " - " + jsonObject.getString("endDate");
                photoEvent = jsonObject.getString("image");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            //TODO quitar
            Toast.makeText(getActivity().getApplicationContext(), "Recovery code email sent!", Toast.LENGTH_LONG).show();
        };
        Response.ErrorListener errorListener = error -> {
            //TODO quitar
            Toast.makeText(getActivity().getApplicationContext(), "An unexpected error has occurred", Toast.LENGTH_LONG).show();
        };
        apiCommunicator.getRequest(getActivity().getApplicationContext(), URL, responseListener, errorListener,null);
        return allEvents;
    }

    public void inicialBoard(List<Event> items) {

        //TODO Coger JSon y transformarlo la imagen bitmap
        //TODO cambiar imagen si es individual o no
        //TODO hacer un foreach
        //TODO hacer click imagen
      //  items.add(new Event(title, hour, place, date,"No", R.drawable.user_icon));
        items.add(new Event("Ferran", "Guapo", "Aqui", "3-4", "No", R.drawable.user_icon));
        items.add(new Event("Sergio", "Guapo","Aqui", "3-4", "No", R.drawable.user_icon));
        items.add(new Event("Sergi", "Guapo","Aqui", "3-4", "No", R.drawable.user_icon));

        mAdapter = new EventAdapter(items);
        mRecyclerView.setAdapter(mAdapter);



    }


}
