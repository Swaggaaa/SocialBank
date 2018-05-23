package me.integrate.socialbank;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AllMyEventsFragment extends Fragment {


    private static final String URL = "/users";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    private ProgressDialog loadingDialog;
    private List<Event> items;
    private String emailUser;

    //TODO filter options

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_board, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        setHasOptionsMenu(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        loadingDialog = ProgressDialog.show(getActivity(), "",
                getString(R.string.loadingMessage), true);
        items = new ArrayList<>();
        emailUser = SharedPreferencesManager.INSTANCE.read(getActivity(),"user_email");
        getAllEventsByUser();

        //TODO modificar
        //getJoinEvents();
        return rootView;
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    //Call to the api for the events by creator
    private void getAllEventsByUser() {

        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = (Response.Listener<CustomRequest.CustomResponse>) response -> {
            JSONArray jsonArray;
            try {
                jsonArray = new JSONArray(response.response);
                System.out.println(String.valueOf(jsonArray.length()));
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    items.add(new Event(jsonObject));

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        };
        Response.ErrorListener errorListener = error -> errorTreatment(error.networkResponse.statusCode);

        apiCommunicator.getRequest(getActivity().getApplicationContext(), URL +'/'+ emailUser + "/events", responseListener, errorListener, null);
    }

    //Call to the api for the join events
    private void getJoinEvents() {

        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = (Response.Listener<CustomRequest.CustomResponse>) response -> {
            JSONArray jsonArray;
            try {
                jsonArray = new JSONArray(response.response);
                System.out.println(String.valueOf(jsonArray.length()));
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    items.add(new Event(jsonObject));

                }
                mAdapter = new EventAdapter(items, getActivity(), (v1, position) -> {
                    Bundle bundle = new Bundle();
                    Event event = items.get(position);

                    bundle.putInt("id", event.getId());
                    bundle.putBoolean("MyEvents", true);
                    Fragment eventFragment;
                    if (event.getCreatorEmail().equals(SharedPreferencesManager.INSTANCE.read(getActivity(),"user_email"))
                            && correctDate(event.getIniDate())) {
                        eventFragment = MyEventFragment.newInstance(bundle);
                    } else if (event.getCreatorEmail().equals(SharedPreferencesManager.INSTANCE.read(getActivity(), "user_email"))) eventFragment = EventFragment.newInstance(bundle);
                    else eventFragment = MyJoinEventFragment.newInstance(bundle);
                    FragmentChangeListener fc = (FragmentChangeListener) getActivity();
                    fc.replaceFragment(eventFragment);
                });

                mRecyclerView.setAdapter(mAdapter);
                loadingDialog.dismiss();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        };
        Response.ErrorListener errorListener = error -> errorTreatment(error.networkResponse.statusCode);

        apiCommunicator.getRequest(getActivity().getApplicationContext(), URL +'/'+ emailUser + "/events", responseListener, errorListener, null);
    }

    private void errorTreatment(int errorCode) {
        String message;
        if (errorCode == 401)
            message = getString(R.string.Unauthorized);
        else if(errorCode == 403)
            message = getString(R.string.Forbidden);
        else if(errorCode == 404)
            message = getString(R.string.NotFound);
        else
            message = getString(R.string.UnexpectedError);

        loadingDialog.dismiss();
        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private boolean correctDate(Date iniDate) {
        if (iniDate == null) return true;
        else {
            Date currentDate = new Date();
            long hours = iniDate.getTime() - currentDate.getTime();
            hours = hours/ 1000 / 60 / 60;
            return hours >= 24;
        }
    }
}
