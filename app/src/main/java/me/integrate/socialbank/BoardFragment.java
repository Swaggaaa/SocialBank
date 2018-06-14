package me.integrate.socialbank;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class BoardFragment extends Fragment {

    private static final String URL = "/events";
    private static final String URL_users = "/users";

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    private List<Event> items;
    private List<Event> allItems;

    private boolean language;
    private boolean culture;
    private boolean workshops;
    private boolean sports;
    private boolean gastronomy;
    private boolean leisure;
    private boolean other;
    private boolean offer;
    private boolean demand;
    private boolean verified;
    private boolean available;
    private String emailUser;

    private MenuItem itemLanguage;
    private MenuItem itemCulture;
    private MenuItem itemWorkshops;
    private MenuItem itemSports;
    private MenuItem itemGastronomy;
    private MenuItem itemLeisure;
    private MenuItem itemOther;
    private MenuItem itemOffer;
    private MenuItem itemDemand;
    private MenuItem itemAvailable;
    private String[] tagsText;

    private ProgressDialog loadingDialog;

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
        loadingDialog = ProgressDialog.show(getActivity(), "", getString(R.string.loadingMessage), true);
        items = new ArrayList<>();
        allItems = new ArrayList<>();
        emailUser = SharedPreferencesManager.INSTANCE.read(getActivity(),"user_email");
        available = demand = other = offer = language = culture = workshops = sports = gastronomy = leisure = false;
        getAllEvents();
        tagsText = new String[1];
        tagsText[0] = "";
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.filter_options, menu);
        itemLanguage = menu.findItem(R.id.category_language);
        itemCulture = menu.findItem(R.id.category_culture);
        itemWorkshops = menu.findItem(R.id.category_workshops);
        itemSports = menu.findItem(R.id.category_sports);
        itemGastronomy = menu.findItem(R.id.category_gastronomy);
        itemLeisure = menu.findItem(R.id.category_leisure);
        itemOther = menu.findItem(R.id.category_other);
        itemOffer = menu.findItem(R.id.event_offer);
        itemDemand = menu.findItem(R.id.event_demand);
        itemAvailable = menu.findItem(R.id.event_available);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.category_language:
                language = !item.isChecked();
                item.setChecked(!item.isChecked());
                update();
                break;
            case R.id.category_culture:
                culture = !item.isChecked();
                item.setChecked(!item.isChecked());
                update();
                break;
            case R.id.category_workshops:
                workshops = !item.isChecked();
                item.setChecked(!item.isChecked());
                update();
                break;
            case R.id.category_sports:
                sports = !item.isChecked();
                item.setChecked(!item.isChecked());
                update();
                break;
            case R.id.category_gastronomy:
                gastronomy = !item.isChecked();
                item.setChecked(!item.isChecked());
                update();
                break;
            case R.id.category_leisure:
                leisure = !item.isChecked();
                item.setChecked(!item.isChecked());
                update();
                break;
            case R.id.category_other:
                other = !item.isChecked();
                item.setChecked(!item.isChecked());
                update();
                break;
            case R.id.event_offer:
                offer = !item.isChecked();
                item.setChecked(!item.isChecked());
                update();
                break;
            case R.id.event_demand:
                demand = !item.isChecked();
                item.setChecked(!item.isChecked());
                update();
                break;
            case R.id.event_available:
                available = !item.isChecked();
                item.setChecked(!item.isChecked());
                update();
                break;
            case R.id.delete_filters:
                demand = other = offer = language = culture = workshops = sports = gastronomy = leisure = false;
                itemLanguage.setChecked(false);
                itemCulture.setChecked(false);
                itemWorkshops.setChecked(false);
                itemSports.setChecked(false);
                itemGastronomy.setChecked(false);
                itemLeisure.setChecked(false);
                itemOther.setChecked(false);
                itemOffer.setChecked(false);
                itemDemand.setChecked(false);
                itemAvailable.setChecked(false);
                update();
                break;
        }
        return true;
    }

    private void set_tags() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.tag_search);

        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.fragment_input_tags,
                (ViewGroup) getView(), false);

        final EditText inputTags = (EditText) viewInflated.findViewById(R.id.inputTags);
        builder.setView(viewInflated);

        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            dialog.dismiss();
            tagsText[0] = inputTags.getText().toString();
            update();
        });
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private boolean checkAvailability(Event event) {
        return (!available || (available && event.isAvailable()));
    }

    private void check(Event event) {
        if (offer || demand || available) {
            if (offer && !event.isDemand() && checkAvailability(event)) {
                items.add(event);
            }
            else if (demand && event.isDemand() && checkAvailability(event)) {
                items.add(event);
            }
            else if (!offer && !demand && checkAvailability(event)) {
                items.add(event);
            }
        } else items.add(event);
    }

    private void update() {
        items.clear();
        boolean category = language || culture || workshops || sports || gastronomy || leisure || other;
        if (category || offer || demand || available) {
            for (Event event: allItems) {
                if (language && event.getCategory() == Event.Category.LANGUAGE) check(event);
                else if (culture && event.getCategory() == Event.Category.CULTURE ) check(event);
                else if (workshops && event.getCategory() == Event.Category.WORKSHOPS ) check(event);
                else if (sports && event.getCategory() == Event.Category.SPORTS ) check(event);
                else if (gastronomy && event.getCategory() == Event.Category.GASTRONOMY ) check(event);
                else if (leisure && event.getCategory() == Event.Category.LEISURE) check(event);
                else if (other && event.getCategory() == Event.Category.OTHER ) check(event);
                else if (!category && (offer || demand || available)) check(event);
            }
        } else {
            items.addAll(allItems);
        }
        mAdapter.notifyDataSetChanged();
    }

    //Call to API
    public void getAllEvents() {
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
                    Fragment eventFragment;
                    boolean eventCreator = event.getCreatorEmail().equals(emailUser);
                    if( eventCreator && event.stillEditable() )
                            eventFragment = MyEventFragment.newInstance(bundle);
                    else if( !eventCreator && event.isAvailable() && !verified)
                        eventFragment = MyJoinEventFragment.newInstance(bundle);
                    else
                        eventFragment = EventFragment.newInstance(bundle);
                    FragmentChangeListener fc = (FragmentChangeListener) getActivity();
                    fc.replaceFragment(eventFragment);
                });
                allItems.addAll(items);
                mRecyclerView.setAdapter(mAdapter);
                getUserInfo();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        };
        Response.ErrorListener errorListener = error -> {
            loadingDialog.dismiss();
            errorTreatment(error.networkResponse.statusCode);
        };
        apiCommunicator.getRequest(getActivity().getApplicationContext(), URL, responseListener, errorListener, null);
    }

    private void getUserInfo() {
        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = (Response.Listener<CustomRequest.CustomResponse>) response -> {
            JSONObject jsonObject;
            try{
                jsonObject = new JSONObject(response.response);
                verified = jsonObject.getBoolean("verified");
                SharedPreferencesManager.INSTANCE.store(getActivity(), "verified", (verified) ? "true" : "false");
                loadingDialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        };
        Response.ErrorListener errorListener = error ->  errorTreatment(error.networkResponse.statusCode);

        apiCommunicator.getRequest(getActivity().getApplicationContext(), URL_users +'/'+ emailUser, responseListener, errorListener, null);
    }

    private void errorTreatment(int errorCode) {
        String message;
        if (errorCode == 401)
            message = getString(R.string.unauthorized);
        else if(errorCode == 403)
            message = getString(R.string.forbidden);
        else if(errorCode == 404)
            message = getString(R.string.not_found);
        else
            message = getString(R.string.unexpectedError);

        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}