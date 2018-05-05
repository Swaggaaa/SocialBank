package me.integrate.socialbank;


import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class EventFragment extends Fragment {

    private ImageView imageView;
    private TextView textEventTitle;
    private TextView textEventOrganizer;
    private TextView textEventDescription;
    private TextView textDemandEvent;
    private TextView textLocation;
    private TextView textIndividualOrGroup;
    private TextView textViewNumberPersonsEvent;

    public static EventFragment newInstance (Bundle params) {
       EventFragment eventFragment = new EventFragment();
       eventFragment.setArguments(params);
       return eventFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_event, container, false);
        imageView = (ImageView)rootView.findViewById(R.id.imageViewEvent);
        textEventTitle = (TextView)rootView.findViewById(R.id.textEventTitle);
        textEventOrganizer = (TextView)rootView.findViewById(R.id.textEventOrganizer);
        textEventDescription = (TextView)rootView.findViewById(R.id.textEventDescription);
        textDemandEvent = (TextView) rootView.findViewById(R.id.demand_event);
        textLocation = (TextView) rootView.findViewById(R.id.location_event);
        textIndividualOrGroup = (TextView) rootView.findViewById(R.id.individual_or_group);
        textViewNumberPersonsEvent = (TextView) rootView.findViewById(R.id.number_person);

        byte[] imageBytes = getArguments().getByteArray("image");
        if (imageBytes != null) {
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(
                    imageBytes, 0, imageBytes.length));
        }
        textEventTitle.setText(getArguments().getString("title"));
        textEventOrganizer.setText(getArguments().getString("creator"));
        textEventDescription.setText(getArguments().getString("description"));
        textLocation.setText(getArguments().getString("location"));
        textDemandEvent.setText(getDemandOrOffer(getArguments().getBoolean("isDemand")));
        textIndividualOrGroup.setText("Individual");
        textViewNumberPersonsEvent.setText("1/1");


        return rootView;
    }

    public String getDemandOrOffer(boolean isDemand) {
        if(isDemand) return getResources().getString(R.string.demand);
        else return getResources().getString(R.string.offered);
    }
}
