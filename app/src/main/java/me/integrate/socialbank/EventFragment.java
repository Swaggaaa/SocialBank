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

    private ImageView imageView;
    private TextView textEventTitle;
    private TextView textEventOrganizer;
    private TextView textEventDescription;

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

        byte[] imageBytes = getArguments().getByteArray("image");
        imageView.setImageBitmap(BitmapFactory.decodeByteArray(
                imageBytes, 0, imageBytes.length));

        textEventTitle.setText(getArguments().getString("title"));
        textEventDescription.setText(getArguments().getString("description"));
        return rootView;
    }
}
