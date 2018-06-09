package me.integrate.socialbank;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class EventFragment extends Fragment {

    private static final String URL = "/events";

    protected Button join_button;

    protected ImageView imageView;
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
    protected EditText editDescription;

    private Integer capacity;
    private Integer numberEnrolled;

    protected String creator;
    protected int id;
    protected String descriptionEvent;
    protected Date iniDate;
    protected Date endDate;

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
        editDescription = (EditText) rootView.findViewById(R.id.editDescription);

        join_button = (Button) rootView.findViewById(R.id.join_button);

        id = getArguments().getInt("id");
        showEventInformation();
        return rootView;
    }

    //Call API to obtain event's information
    private void showEventInformation() {
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

                editDescription.setText(descriptionEvent);

                capacity = event.getCapacity();
                numberEnrolled = event.getNumberEnrolled();

                if(event.isIndividual())
                    textIndividualOrGroup.setText(R.string.individual);
                else
                    textIndividualOrGroup.setText(R.string.groupal);

                textViewNumberPersonsEvent.setText(numberEnrolled+"/"+capacity);

                iniDate = event.getIniDate();
                endDate = event.getEndDate();
                long hoursL = getHours(iniDate, endDate);
                String hours =  (hoursL > 0) ? String.valueOf(hoursL) : getResources().getString(R.string.notHour) + " " + getResources().getString(R.string.time_hours);
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

    protected long getHours(Date hourIni, Date hourEnd) {
        long hours = 0;
        if (hourIni != null && hourEnd != null ) {
            long diff = hourEnd.getTime() - hourIni.getTime();
            hours = diff/1000/ 60 / 60;
        }
        return hours;
    }

    protected String getCreator() {
        return creator;
    }

    protected void changesEnrollment(boolean join) {
        if (join) numberEnrolled++;
        else  numberEnrolled--;
        textViewNumberPersonsEvent.setText(numberEnrolled+"/"+capacity);
    }

    protected boolean isEventFull() {
        return capacity == numberEnrolled;
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
