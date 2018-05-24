package me.integrate.socialbank;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

public class MyAccountFragment extends Fragment {
    private static final String URL = "/users";
    private TextView accountStatus;
    private TextView accountStatusImage;
    private TextView verifyAccountHint;
    private EditText sendRequestText;
    private Button sendRequestButton;
    private boolean verified;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_my_account, container, false);
        accountStatus = (TextView) rootView.findViewById(R.id.AccountStatus);
        accountStatusImage = (TextView) rootView.findViewById(R.id.account_verified);
        verifyAccountHint = (TextView) rootView.findViewById(R.id.account_verify_hint);
        sendRequestText = (EditText) rootView.findViewById(R.id.editText_request);
        sendRequestButton = (Button) rootView.findViewById(R.id.button_send_request);
        String email = SharedPreferencesManager.INSTANCE.read(getActivity(),"user_email");
        loadScreen(email);
        return rootView;
    }

    private void loadScreen(String emailUser) {

        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = (Response.Listener<CustomRequest.CustomResponse>) response -> {
            JSONObject jsonObject;
            Float balance = null;
            try {
                jsonObject = new JSONObject(response.response);
                verified = jsonObject.getBoolean("verified");

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

}
