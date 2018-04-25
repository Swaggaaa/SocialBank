package me.integrate.socialbank;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class SeeMyProfileFragment extends Fragment {
    private static final String URL = "/users";
    private ImageView userPicture;
    private TextView userName;
    private ImageView changePhoto;
    private TextView userLocation;
    private TextView userBalance;
    private FloatingActionButton editProfile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_see_my_profile, container, false);
        userPicture = (ImageView) rootView.findViewById(R.id.myProfileImage);
        userName = (TextView) rootView.findViewById(R.id.myProfileName);
        changePhoto = (ImageView) rootView.findViewById(R.id.loadPicture);
        userLocation = (TextView) rootView.findViewById(R.id.userLocation);
        userBalance = (TextView) rootView.findViewById(R.id.hoursBalance);
        editProfile = (FloatingActionButton) rootView.findViewById(R.id.editProfile);
        fillFields();
        return rootView;
    }

    private void fillFields() {
        String emailUser = SharedPreferencesManager.INSTANCE.read(getActivity(),"user_email");
        askCredentials(emailUser);
    }

    private void askCredentials(String emailUser) {
        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = (Response.Listener<CustomRequest.CustomResponse>) response -> {
            JSONObject jsonObject = null;
            Float balance = null;
            try{
                jsonObject = new JSONObject(response.response);
                String completeName = jsonObject.getString("name") + " " + jsonObject.getString("surname");
                userName.setText(completeName);
                balance = BigDecimal.valueOf(jsonObject.getDouble("balance")).floatValue();
                userBalance.setText(balance.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (balance < 0) userBalance.setTextColor(Color.RED);
            else if (balance > 0) userBalance.setTextColor(Color.GREEN);
            else userBalance.setTextColor(Color.BLUE);

        };
        Response.ErrorListener errorListener = error -> {
            Toast.makeText(getActivity().getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
            Fragment boardFragment = new BoardFragment();
            FragmentChangeListener fc = (FragmentChangeListener) getActivity();
            fc.replaceFragment(boardFragment);
        };

        apiCommunicator.getRequest(getActivity().getApplicationContext(), URL+'/'+emailUser, responseListener, errorListener, null);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
