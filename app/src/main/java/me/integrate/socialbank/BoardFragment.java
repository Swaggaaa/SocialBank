package me.integrate.socialbank;


import android.app.ProgressDialog;
import android.graphics.Bitmap;
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
import android.widget.Toast;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class BoardFragment extends Fragment {

    private static final String URL = "/events";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    private List<Event> items;
    private List<Event> allItems;
    private List<Event> aux;

    private boolean language;
    private boolean culture;
    private boolean workshops;
    private boolean sports;
    private boolean gastronomy;
    private boolean leisure;
    private boolean other;
    private boolean offer;
    private boolean demand;

    private MenuItem itemLanguage;
    private MenuItem itemCulture;
    private MenuItem itemWorkshops;
    private MenuItem itemSports;
    private MenuItem itemGastronomy;
    private MenuItem itemLeisure;
    private MenuItem itemOther;
    private MenuItem itemOffer;
    private MenuItem itemDemand;

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
        loadingDialog = ProgressDialog.show(getActivity(), "",
                getString(R.string.loadingMessage), true);
        items = new ArrayList<>();
        allItems = new ArrayList<>();
        aux = new ArrayList<>();
        demand = other = offer = language = culture = workshops = sports = gastronomy = leisure = false;
        getAllEvents();
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

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.category_language:
                language = !item.isChecked();
                if (item.isChecked())item.setChecked(false);
                else item.setChecked(true);
                break;
            case R.id.category_culture:
                culture = !item.isChecked();
                item.setChecked(!item.isChecked());
                break;
            case R.id.category_workshops:
                workshops = !item.isChecked();
                item.setChecked(!item.isChecked());
                break;
            case R.id.category_sports:
                sports = !item.isChecked();
                item.setChecked(!item.isChecked());
                break;
            case R.id.category_gastronomy:
                gastronomy = !item.isChecked();
                item.setChecked(!item.isChecked());
                break;
            case R.id.category_leisure:
                leisure = !item.isChecked();
                item.setChecked(!item.isChecked());
                break;
            case R.id.category_other:
                other = !item.isChecked();
                item.setChecked(!item.isChecked());
                break;
            case R.id.event_offer:
                offer = !item.isChecked();
                item.setChecked(!item.isChecked());

                break;
            case R.id.event_demand:
                demand = !item.isChecked();
                item.setChecked(!item.isChecked());
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
                break;
        }
        update();
        return true;

    }

    private void update() {
        aux.clear();
        boolean category = language || culture || workshops || sports || gastronomy || leisure || other;
        if (category || offer || demand) {
            for (Event event: allItems) {
                if ((language &&  event.getCategory() == Event.Category.LANGUAGE)) aux.add(event);
                else if (culture && event.getCategory() == Event.Category.CULTURE ) aux.add(event);
                else if (workshops && event.getCategory() == Event.Category.WORKSHOPS ) aux.add(event);
                else if (sports && event.getCategory() == Event.Category.SPORTS ) aux.add(event);
                else if (gastronomy && event.getCategory() == Event.Category.GASTRONOMY ) aux.add(event);
                else if (leisure && event.getCategory() == Event.Category.LEISURE) aux.add(event);
                else if (other && event.getCategory() == Event.Category.OTHER ) aux.add(event);
                else if (!category && (offer || demand )) {
                    if ((offer && !event.isDemand()) || (demand && event.isDemand()) ) aux.add(event);
                }
            }
            items.clear();
            items.addAll(aux);
            for (Event event : items) {
                if (offer && event.isDemand()) aux.remove(event);
                else if (demand && !event.isDemand()) aux.remove(event);
            }
            items.clear();
            items.addAll(aux);
        } else {
            items.clear();
            items.addAll(allItems);
        }
        mAdapter.notifyDataSetChanged();
    }

    //Call to the API
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
                    if (event.getCreatorEmail().equals(SharedPreferencesManager.INSTANCE.read(getActivity(),"user_email"))
                            && correctDate(event.getIniDate())) {
                        eventFragment = MyEventFragment.newInstance(bundle);
                    }
                    else eventFragment = EventFragment.newInstance(bundle);
                    FragmentChangeListener fc = (FragmentChangeListener) getActivity();
                    fc.replaceFragment(eventFragment);
                });
                allItems.addAll(items);
                mRecyclerView.setAdapter(mAdapter);
                loadingDialog.dismiss();

            } catch (JSONException e) {
                e.printStackTrace();
            }
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

            loadingDialog.dismiss();
            Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
        };
        apiCommunicator.getRequest(getActivity().getApplicationContext(), URL, responseListener, errorListener, null);
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

    private byte[] bitmapToByteArray(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }
        else return null;
    }

    private String dateToString(Date date) {
        if (date == null) return getResources().getString(R.string.notDate);
        else{
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            return df.format(date);
        }
    }

}
