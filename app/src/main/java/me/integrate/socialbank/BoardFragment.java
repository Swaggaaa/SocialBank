package me.integrate.socialbank;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
                //TODO solucionar datas
                jsonArray = new JSONArray(response.response);
                for ( int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    id = Integer.parseInt(jsonObject.getString("id"));
                    title = jsonObject.getString("title");
                    initDate = jsonObject.getString("initDate");
                    place = jsonObject.getString("place");
                    finishDate = jsonObject.getString("endDate");
                    description = jsonObject.getString("description");
                    photoEvent = jsonObject.getString("image");

                    //todo añadir a items
                    //items.add(new Event(id, title, hour, place, date,"No", description, R.drawable.user_icon));
                }

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
    }

    public void inicialBoard(View v) {

        //TODO Coger JSONObject y transformarlo la imagen bitmap
        //TODO hacer click imagen --> como guardo la imagen
        getAllEvents(items);

        //TODO borrar
        items.add(new Event(1,"Ferran", "Guapo", "Aqui", "3-4", "No","bu", R.drawable.ic_menu));
        items.add(new Event( 2,"Sergio", "Guapo","Aqui", "3-4", "No", "bu", R.drawable.user_icon));
        items.add(new Event(3,"Sergi", "Guapo","Aqui", "3-4", "No", "bu", R.drawable.user_icon));
        items.add(new Event(3,"Sergi", "Guapo","Aqui", "3-4", "No", "bu", R.drawable.user_icon));


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
