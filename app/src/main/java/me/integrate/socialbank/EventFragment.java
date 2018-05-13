package me.integrate.socialbank;


import android.graphics.BitmapFactory;
import android.os.Bundle;
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

public class EventFragment extends Fragment {

    private static final String URL = "/events";

    private ImageView imageView;
    private TextView textEventTitle;
    private TextView textEventOrganizer;
    private TextView textEventDescription;
    private TextView textDemandEvent;
    private TextView textLocation;
    private TextView textIndividualOrGroup;
    private TextView textViewNumberPersonsEvent;
    private TextView textStartDate;
    private TextView textEndDate;

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
        textEventDescription = (TextView) rootView.findViewById(R.id.textEventDescription);
        textDemandEvent = (TextView) rootView.findViewById(R.id.demand_event);
        textLocation = (TextView) rootView.findViewById(R.id.location_event);
        textIndividualOrGroup = (TextView) rootView.findViewById(R.id.individual_or_group);
        textViewNumberPersonsEvent = (TextView) rootView.findViewById(R.id.number_person);
        textStartDate = (TextView) rootView.findViewById(R.id.start_date);
        textEndDate = (TextView) rootView.findViewById(R.id.end_date);

        byte[] imageBytes = getArguments().getByteArray("image");
        if (imageBytes != null) {
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(
                    imageBytes, 0, imageBytes.length));
        }

        showEventInformation(getArguments().getInt("id"));

        return rootView;
    }

    void showEventInformation(int id) {
        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = (Response.Listener<CustomRequest.CustomResponse>) response -> {
            try {
                Event event = new Event(new JSONObject(response.response));
                textEventTitle.setText(event.getTitle());
                textEventOrganizer.setText(event.getCreatorEmail());
                textEventDescription.setText(event.getDescription());
                textLocation.setText(event.getLocation());
                textDemandEvent.setText(event.getDemand() ? R.string.demand : R.string.offer);
                textIndividualOrGroup.setText("Individual");
                textViewNumberPersonsEvent.setText("1/1");

                String iniDate = ((event.getIniDate() == null) ? "There is no date yet" : event.getIniDate().toString());
                textStartDate.setText(iniDate);
                String endDate = ((event.getIniDate() == null) ? "There is no date yet" : event.getEndDate().toString());
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
