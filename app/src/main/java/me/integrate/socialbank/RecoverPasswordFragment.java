package me.integrate.socialbank;


import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.HashMap;

public class RecoverPasswordFragment extends Fragment {

    private EditText email;
    private Button submitButton;

    //TODO mirar si es correcto
    private static final String URL = "/recover";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_activity_recover_password, container, false);
        email = (EditText) rootView.findViewById(R.id.email_submit);
        submitButton = (Button) rootView.findViewById(R.id.submit_button);
        enableButton();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getView().findViewById(R.id.submit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tokenSelected();
                //submitButton.setEnabled(false);
                //cleanEmail(); //TODO aqui no ira
                //if (email.getText().toString().length() != 0) {
                    //IF api funciona fa lo seguent
                //}
                // TODO postCredentials(user.getText().toString(), password.getText().toString());
            }
        });
        email.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                enableButton();
            }
        });

    }

    //TODO conexion with API
    private void postCredentials(String user, String password) {

        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = new Response.Listener<CustomRequest.CustomResponse>() {
            @Override
            public void onResponse(CustomRequest.CustomResponse response) {
                //TODO
                String token = response.headers.get("Authorization");
                SharedPreferencesManager.INSTANCE.store(getActivity(),"token",token);
                startActivity(new Intent(getActivity().getApplicationContext(), InsideActivity.class));
                getActivity().finish();
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                submitButton.setEnabled(true);
                //TODO tratar error
                Toast.makeText(getActivity().getApplicationContext(), "Bu", Toast.LENGTH_LONG).show();
            }
        };
        HashMap<String, String> params = new HashMap<>();
        params.put("email", email.getText().toString());

        apiCommunicator.postRequest(getActivity().getApplicationContext(), URL, responseListener, errorListener, params);
    }

    private void cleanEmail() {
        email.getText().clear();
    }

    private void enableButton() {
        submitButton.setEnabled( email.getText().toString().length() != 0 );
    }

    //TODO cambiar al new fragment
    private void tokenSelected() {
        Fragment tokenFragment = new TokenFragment();
        FragmentChangeListener fc = (FragmentChangeListener) getActivity();
        fc.replaceFragment(tokenFragment);
    }
}
