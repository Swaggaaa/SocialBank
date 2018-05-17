package me.integrate.socialbank;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EventFragment extends Fragment {

    private static final String URL = "/events";

    private ImageView imageView;
    private TextView textEventTitle;
    private TextView textEventOrganizer;
    private TextView textEventCategory;
    protected TextView textEventDescription;
    private TextView textDemandEvent;
    private TextView textLocation;
    private TextView textIndividualOrGroup;
    private TextView textViewNumberPersonsEvent;
    private TextView textEventHours;
    private TextView textStartDate;
    private TextView textEndDate;

    private String creator;
    protected int id;
    protected String descriptionEvent;

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
        imageView = (ImageView) rootView.findViewById(R.id.imageViewEvent);
        textEventTitle = (TextView) rootView.findViewById(R.id.textEventTitle);
        textEventOrganizer = (TextView) rootView.findViewById(R.id.textEventOrganizer);
        textEventCategory = (TextView) rootView.findViewById(R.id.textEventCategory);
        textEventDescription = (TextView) rootView.findViewById(R.id.textEventDescription);
        textDemandEvent = (TextView) rootView.findViewById(R.id.demand_event);
        textLocation = (TextView) rootView.findViewById(R.id.location_event);
        textIndividualOrGroup = (TextView) rootView.findViewById(R.id.individual_or_group);
        textViewNumberPersonsEvent = (TextView) rootView.findViewById(R.id.number_person);
        textEventHours = (TextView) rootView.findViewById(R.id.hours);
        textStartDate = (TextView) rootView.findViewById(R.id.start_date);
        textEndDate = (TextView) rootView.findViewById(R.id.end_date);



        id = getArguments().getInt("id");
        showEventInformation();
        return rootView;
    }

        //Call API to obtain event's information
    void showEventInformation() {
        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = (Response.Listener<CustomRequest.CustomResponse>) response -> {
            try {
                Event event = new Event(new JSONObject(response.response));
                textEventTitle.setText(event.getTitle());
                creator = event.getCreatorEmail();
                textEventOrganizer.setText(creator);
                textEventCategory.setText(event.getCategory().toString());
                descriptionEvent = event.getDescription();
                textEventDescription.setText(descriptionEvent);
                textLocation.setText(event.getLocation());
                textDemandEvent.setText(event.getDemand() ? R.string.demand : R.string.offer);
                imageView.setImageBitmap(event.getImage());

                //TODO not harcoded this values
                textIndividualOrGroup.setText("Individual");
                textViewNumberPersonsEvent.setText("1/1");

                Date iniDate = event.getIniDate();
                Date endDate = event.getEndDate();
                String hours = getHours(iniDate, endDate) + " " + getResources().getString(R.string.time_hours);
                textEventHours.setText(hours);
                textStartDate.setText(dateToString(iniDate));
                textEndDate.setText(dateToString(endDate));

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
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy        HH:mm");
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
            Fragment profileFragment;
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
}
