package me.integrate.socialbank;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

    private static final String URL = "/recover";
    private EditText email;
    private Button submitButton;

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
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitButton.setEnabled(false);
                if (email.getText().toString().length() != 0) {
                    postCredentials(email.getText().toString());
                }
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

        view.findViewById(R.id.token_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tokenSelected();
            }
        });

    }

    private void postCredentials(String email) {

        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = new Response.Listener<CustomRequest.CustomResponse>() {
            @Override
            public void onResponse(CustomRequest.CustomResponse response) {
                Toast.makeText(getActivity().getApplicationContext(), "Recovery information initialized", Toast.LENGTH_LONG).show();
                tokenSelected();
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                submitButton.setEnabled(true);
                Toast.makeText(getActivity().getApplicationContext(), "An unexpected error has occurred", Toast.LENGTH_LONG).show();
            }
        };
        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);

        apiCommunicator.postRequest(getActivity().getApplicationContext(), URL, responseListener, errorListener, params);
    }

    private void enableButton() {
        submitButton.setEnabled(!email.getText().toString().isEmpty());
    }

    public void tokenSelected() {
        Fragment tokenFragment = new TokenFragment();
        FragmentChangeListener fc = (FragmentChangeListener) getActivity();
        fc.replaceFragment(tokenFragment);
    }
}
