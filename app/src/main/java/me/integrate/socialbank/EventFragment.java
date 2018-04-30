package me.integrate.socialbank;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class EventFragment extends Fragment {

    private int id;

    private TextView ID;

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
        id = getArguments().getInt("id");

        //TODO borrar
        ID = (TextView) rootView.findViewById(R.id.event_id);
        ID.setText(String.valueOf(id));
        return rootView;
    }
}
