package me.integrate.socialbank;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AllMyEventsFragment extends Fragment {


    private static final String URL = "/users";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    private ProgressDialog loadingDialog;
    private List<Event> events;
    private List<Event> joinEvents;
    private List<Event> items;
    private String emailUser;

    private MenuItem itemMyEvents;
    private MenuItem itemMyJoinEvents;

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
        events = new ArrayList<>();
        joinEvents = new ArrayList<>();
        items = new ArrayList<>();
        emailUser = SharedPreferencesManager.INSTANCE.read(getActivity(),"user_email");

        getJoinEvents();
        getAllEventsByUser();

        return rootView;
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.filter_events, menu);
        itemMyEvents = menu.findItem(R.id.my_events);
        itemMyJoinEvents = menu.findItem(R.id.my_events_join);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.my_events:
                items.clear();
                if (item.isChecked()) {
                    if (itemMyJoinEvents.isChecked()) items.addAll(joinEvents);
                    else {
                        items.addAll(events);
                        items.addAll(joinEvents);
                    }
                    item.setChecked(false);
                }
                else {
                    if (itemMyJoinEvents.isChecked()) items.addAll(joinEvents);
                    items.addAll(events);
                    item.setChecked(true);
                }
                break;
            case R.id.my_events_join:
                items.clear();
                if (item.isChecked()) {
                    if (itemMyEvents.isChecked()) items.addAll(events);
                    else {
                        items.addAll(events);
                        items.addAll(joinEvents);
                    }
                    item.setChecked(false);
                }
                else {
                    if (itemMyEvents.isChecked()) items.addAll(events);
                    items.addAll(joinEvents);
                    item.setChecked(true);
                }
                break;
            case R.id.delete_filters:
                items.clear();
                items.addAll(events);
                items.addAll(joinEvents);
                itemMyJoinEvents.setChecked(false);
                itemMyEvents.setChecked(false);
                break;
        }
        mAdapter.notifyDataSetChanged();
        return true;

    }

    //Call to the api for the events by creator
    private void getAllEventsByUser() {

        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = (Response.Listener<CustomRequest.CustomResponse>) response -> {
            JSONArray jsonArray;
            try {
                jsonArray = new JSONArray(response.response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    events.add(new Event(jsonObject));

                }
                items.addAll(events);
                mAdapter = new EventAdapter(items, getActivity(), (v1, position) -> {
                    Bundle bundle = new Bundle();
                    Event event = items.get(position);

                    bundle.putInt("id", event.getId());
                    bundle.putBoolean("MyProfile", true);
                    Fragment eventFragment;
                    boolean eventCreator = event.getCreatorEmail().equals(SharedPreferencesManager.INSTANCE.read(getActivity(),"user_email"));
                    if( eventCreator && event.stillEditable() )
                        eventFragment = MyEventFragment.newInstance(bundle);
                    else if( !eventCreator && event.isAvailable() )
                        eventFragment = MyJoinEventFragment.newInstance(bundle);
                    else
                        eventFragment = EventFragment.newInstance(bundle);
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

    //Call to the api for the join events
    private void getJoinEvents() {

        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = (Response.Listener<CustomRequest.CustomResponse>) response -> {
            JSONArray jsonArray;
            try {
                jsonArray = new JSONArray(response.response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    joinEvents.add(new Event(jsonObject));

                }
                items.addAll(joinEvents);
                mAdapter = new EventAdapter(items, getActivity(), (v1, position) -> {
                    Bundle bundle = new Bundle();
                    Event event = items.get(position);

                    bundle.putInt("id", event.getId());
                    bundle.putBoolean("MyProfile", true);
                    Fragment eventFragment;
                    boolean eventCreator = event.getCreatorEmail().equals(SharedPreferencesManager.INSTANCE.read(getActivity(),"user_email"));
                    if( eventCreator && event.stillEditable() )
                        eventFragment = MyEventFragment.newInstance(bundle);
                    else if( !eventCreator && event.isAvailable() )
                        eventFragment = MyJoinEventFragment.newInstance(bundle);
                    else
                        eventFragment = EventFragment.newInstance(bundle);
                    FragmentChangeListener fc = (FragmentChangeListener) getActivity();
                    fc.replaceFragment(eventFragment);
                });

                mRecyclerView.setAdapter(mAdapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        };
        Response.ErrorListener errorListener = error -> errorTreatment(error.networkResponse.statusCode);

        apiCommunicator.getRequest(getActivity().getApplicationContext(), URL +'/'+ emailUser + "/enrollments", responseListener, errorListener, null);
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

}
