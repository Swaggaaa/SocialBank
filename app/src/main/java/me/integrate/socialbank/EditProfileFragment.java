package me.integrate.socialbank;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class EditProfileFragment extends Fragment {
    private static final String URL = "/users";
    private EditText newName;
    private EditText newLastName;
    private EditText newBirthdate;
    private Spinner newGender;
    private EditText newDescription;
    private Button update;
    private String bornDate;
    private String emailUser;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        newName = (EditText) rootView.findViewById(R.id.updateFirstName);
        newLastName = (EditText) rootView.findViewById(R.id.updateLastName);
        newBirthdate = (EditText) rootView.findViewById(R.id.updateBirthdate);
        newGender = (Spinner) rootView.findViewById(R.id.updateGender);
        newDescription = (EditText) rootView.findViewById(R.id.updateDescription);
        update = (Button) rootView.findViewById(R.id.buttonRegister);
        emailUser = SharedPreferencesManager.INSTANCE.read(getActivity(), "user_email");
        getUserInfo(emailUser);
        return rootView;
    }

    private void getUserInfo(String emailUser) {
        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = (Response.Listener<CustomRequest.CustomResponse>) response -> {
            JSONObject jsonObject;
            Float balance = null;
            try {
                jsonObject = new JSONObject(response.response);
                newName.setText(jsonObject.getString("name"));
                newLastName.setText(jsonObject.getString("surname"));
                getIndex(jsonObject.getString("gender"));
                newDescription.setText(jsonObject.getString("description"));
                bornDate = jsonObject.getString("birthdate");
                newBirthdate.setText(bornDate);

            } catch (JSONException e) {
                e.printStackTrace();
            }


        };
        Response.ErrorListener errorListener = error -> {
            Toast.makeText(getActivity().getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
            Fragment boardFragment = new BoardFragment();
            FragmentChangeListener fc = (FragmentChangeListener) getActivity();
            fc.replaceFragment(boardFragment);
        };

        apiCommunicator.getRequest(getActivity().getApplicationContext(), URL + '/' + emailUser, responseListener, errorListener, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newBirthdate.setOnClickListener(view1 -> chooseDate());
        update.setOnClickListener(v -> {
            HashMap<String, String> params = new HashMap<>();
            params.put("name", newName.getText().toString());
            params.put("surname", newLastName.getText().toString());
            params.put("birthdate", bornDate);
            params.put("gender", newGender.getSelectedItem().toString().toUpperCase());
            params.put("description", newDescription.getText().toString());
            params.put("email", emailUser);
            //TODO: remove email when API fixed

            putCredentials(params);
        });

    }

    private void putCredentials(HashMap<String, String> params) {
        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = response -> {
            Toast.makeText(getActivity().getApplicationContext(), R.string.updateAccount, Toast.LENGTH_LONG).show();
            SharedPreferencesManager.INSTANCE.store(getActivity(), "user_name", params.get("name"));
        };
        Response.ErrorListener errorListener = error -> Toast.makeText(getActivity().getApplicationContext(), "Something went wrong PUT", Toast.LENGTH_LONG).show();


        apiCommunicator.putRequest(getActivity().getApplicationContext(), URL + '/' + emailUser + "/update", responseListener, errorListener, params);
    }

    private void chooseDate(){
        showDatePickerDialog();
    }
    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance((datePicker, year, month, day) -> {
            // +1 because january is zero
            final String selectedDate = day + " / " + (month + 1) + " / " + year;

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            bornDate = format.format(calendar.getTime());
            newBirthdate.setText(selectedDate);

        });
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    private void getIndex(String myString){

        switch (myString) {
            case "MALE":
                newGender.setSelection(0);
                break;
            case "FEMALE":
                newGender.setSelection(1);
                break;
            default:
                newGender.setSelection(2);
                break;
        }
    }
}
