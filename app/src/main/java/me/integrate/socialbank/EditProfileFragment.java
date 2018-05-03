package me.integrate.socialbank;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class EditProfileFragment extends Fragment {
    private EditText newName;
    private EditText newLastName;
    private EditText newBirthdate;
    private Spinner newGender;
    private EditText newDescription;
    private Button update;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        newName = (EditText)rootView.findViewById(R.id.updateFirstName);
        newLastName = (EditText)rootView.findViewById(R.id.updateLastName);
        newBirthdate = (EditText)rootView.findViewById(R.id.updateBirthdate);
        newGender = (Spinner)rootView.findViewById(R.id.updateGender);
        newDescription = (EditText)rootView.findViewById(R.id.updateDescription);
        update = (Button)rootView.findViewById(R.id.buttonRegister);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


}
