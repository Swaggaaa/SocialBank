package me.integrate.socialbank;


import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class EventFragment extends Fragment {

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
        textEventTitle.setText(getArguments().getString("title"));
        textEventOrganizer.setText(getArguments().getString("creator"));
        textEventDescription.setText(getArguments().getString("description"));
        textLocation.setText(getArguments().getString("location"));
        textDemandEvent.setText(getResources().getString(getArguments().getBoolean("isDemand") ? R.string.demand : R.string.offer));
        textIndividualOrGroup.setText("Individual");
        textViewNumberPersonsEvent.setText("1/1");

        TextView startDate = (TextView) rootView.findViewById(R.id.start_date);
        startDate.setText(getArguments().getString("startDate"));
        TextView endDate = (TextView) rootView.findViewById(R.id.end_date);
        endDate.setText(getArguments().getString("endDate"));


        return rootView;
    }

}
