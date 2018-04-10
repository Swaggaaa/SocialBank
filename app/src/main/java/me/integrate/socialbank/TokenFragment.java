package me.integrate.socialbank;

import android.content.Intent;
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
import java.util.Map;

public class TokenFragment extends Fragment {

    private static final String URL = "/recover";
    private EditText tokenEditText;
    private EditText pass1;
    private EditText pass2;
    private Button changeButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_token, container, false);
        tokenEditText = (EditText) rootView.findViewById(R.id.editTextToken);
        pass1 = (EditText) rootView.findViewById(R.id.editTextPassword1);
        pass2 = (EditText) rootView.findViewById(R.id.editTextPassword2);
        changeButton = (Button) rootView.findViewById(R.id.buttonChangePassword);
        enableButton();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( passwordMatch() ) {
                    postCredentials(tokenEditText.getText().toString(), pass1.getText().toString());
                }

            }
        });
        tokenEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                enableButton();
            }
        });
        pass1.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                enableButton();
            }
        });
        pass2.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                enableButton();
            }
        });

    }

    private void postCredentials(String newPassword, String token) {
        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = new Response.Listener<CustomRequest.CustomResponse>() {
            @Override
            public void onResponse(CustomRequest.CustomResponse response) {
                String token = response.headers.get("Authorization");
                SharedPreferencesManager.INSTANCE.store(getActivity(),"Password changed!",token);
                loginSelected();
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(), "Wrong token", Toast.LENGTH_LONG).show();
                tokenEditText.getText().clear();
            }
        };

        Map<String, String> params = new HashMap<>();
        params.put("newPassword", newPassword);

        apiCommunicator.putRequest(getActivity().getApplicationContext(), URL.concat("/".concat(token)), responseListener, errorListener, params);
    }

    private void clearPasswords() {
        pass1.getText().clear();
        pass2.getText().clear();
    }

    private boolean areFilled() {
        return !tokenEditText.getText().toString().isEmpty()
                && !pass1.getText().toString().isEmpty() && !pass2.getText().toString().isEmpty();
    }

    private void enableButton() {
        changeButton.setEnabled( areFilled() );
    }

    private boolean passwordMatch() {
        boolean ret = pass1.getText().toString().equals( pass2.getText().toString() );
        if( !ret ) {
            Toast.makeText(getActivity().getApplicationContext(), "Passwords are different", Toast.LENGTH_LONG).show();
            clearPasswords();
        }
        return ret;
    }

    public void loginSelected() {
        Fragment loginFragment = new LoginFragment();
        FragmentChangeListener fc = (FragmentChangeListener) getActivity();
        fc.replaceFragment(loginFragment);
    }

}