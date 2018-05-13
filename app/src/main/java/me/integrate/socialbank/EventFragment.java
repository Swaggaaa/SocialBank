package me.integrate.socialbank;


import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.EventLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EventFragment extends Fragment {


    private String creator;
    private Event event;

    private static final String URL = "/events";

    public static EventFragment newInstance(Bundle params) {
        EventFragment eventFragment = new EventFragment();
        eventFragment.setArguments(params);
        return eventFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_event, container, false);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.imageViewEvent);
        TextView textEventTitle = (TextView) rootView.findViewById(R.id.textEventTitle);
        TextView textEventOrganizer = (TextView) rootView.findViewById(R.id.textEventOrganizer);
        TextView textEventCategory = (TextView) rootView.findViewById(R.id.textEventCategory);
        TextView textEventDescription = (TextView) rootView.findViewById(R.id.textEventDescription);
        TextView textDemandEvent = (TextView) rootView.findViewById(R.id.demand_event);
        TextView textLocation = (TextView) rootView.findViewById(R.id.location_event);
        TextView textIndividualOrGroup = (TextView) rootView.findViewById(R.id.individual_or_group);
        TextView textViewNumberPersonsEvent = (TextView) rootView.findViewById(R.id.number_person);

        byte[] imageBytes = getArguments().getByteArray("image");
        if (imageBytes != null) {
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(
                    imageBytes, 0, imageBytes.length));
        }

        showEventInformation(getArguments().getInt("id"));
        return rootView;
    }

    private String getHours(Date hourIni, Date hourEnd) {
        if (hourIni != null && hourEnd != null ) {
            long diff = hourEnd.getTime() - hourIni.getTime();
            long seconds = diff/1000;
            long minutes = seconds/60;
            long hours = minutes/60;
            return String.valueOf(hours);
        } else return getResources().getString(R.string.notHour);
    }

    private String dateToString(Date date) {
        if (date == null) return getResources().getString(R.string.notDate);
        else{
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            return df.format(date);
        }
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.textEventOrganizer).setOnClickListener(v ->
        {
            Bundle b = new Bundle();
            b.putString("email", creator);
            Fragment profileFragment = null;
            if (!creator.equals(SharedPreferencesManager.INSTANCE.read(getActivity(),"user_email"))){
                 profileFragment = new ProfileFragment();
            }
            else {
                profileFragment = new MyProfileFragment();
            }
            profileFragment.setArguments(b);
            FragmentChangeListener fc = (FragmentChangeListener) getActivity();
            fc.replaceFragment(profileFragment);
        });
    }

    private void showEventInformation(int id) {
        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = (Response.Listener<CustomRequest.CustomResponse>) response -> {
            try {
                Event event = new Event(new JSONObject(response.response));
                textEventTitle.setText(event.getTitle());
                textEventOrganizer.setText(event.getCreatorEmail());
                textEventCategory.setText(event.getCategory());
                textEventDescription.setText(event.getDescription());
                textLocation.setText(event.getLocation());
                textDemandEvent.setText(event.getDemand() ? R.string.demand : R.string.offer);
              
                //TODO not harcoded this values
                textIndividualOrGroup.setText("Individual");
                textViewNumberPersonsEvent.setText("1/1");

                String iniDate = ((event.getIniDate() == null) ? "There is no date yet" : event.getIniDate().dateToString());
                textStartDate.setText(iniDate);
                String endDate = ((event.getIniDate() == null) ? "There is no date yet" : event.getEndDate().dateToString());
                textEndDate.setText(endDate);

            } catch (JSONException e) {
                Toast.makeText(EventFragment.this.getActivity().getApplicationContext(), R.string.JSONException, Toast.LENGTH_LONG).show();
            }
        };
        Response.ErrorListener errorListener = error -> {
            String message;
            int errorCode = error.networkResponse.statusCode;
            if (errorCode == 401)
                message = getString(R.string.Unauthorized);
            else if(errorCode == 403)
                message = getString(R.string.Forbidden);
            else if(errorCode == 404)
                message = getString(R.string.NotFound);
            else
                message = getString(R.string.UnexpectedError);

            Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
        };

        apiCommunicator.getRequest(getActivity().getApplicationContext(), URL+'/'+ id, responseListener, errorListener, null);

    }

}
