package me.integrate.socialbank;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

public class TokenFragment extends Fragment {

    private EditText token;
    private EditText pass1;
    private EditText pass2;
    private Button changeButton;
    private static final String URL = "/users";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_token, container, false);
        token = (EditText) rootView.findViewById(R.id.editTextToken);
        pass1 = (EditText) rootView.findViewById(R.id.editTextPassword1);
        pass2 = (EditText) rootView.findViewById(R.id.editTextPassword2);
        changeButton = (Button) rootView.findViewById(R.id.buttonChangePassword);
        enableButton();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getView().findViewById(R.id.buttonChangePassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( passwordMatch() && rightToken() ) {
                    //Si els passwords coincideixen i el token es correcte

                    HashMap<String, String> params = new HashMap<>();
                    params.put("token", token.getText().toString());
                    params.put("newpassword", pass1.getText().toString());
                    params.put("repeatedpassword", pass2.getText().toString());

                    postCredentials(params);
                }

            }
        });
        token.addTextChangedListener(new TextWatcher() {
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

    //TODO conexion with API
    private void postCredentials(HashMap<String, String> params) {

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
                //TODO tratar error
                Toast.makeText(getActivity().getApplicationContext(), "Bu", Toast.LENGTH_LONG).show();
            }
        };

        apiCommunicator.postRequest(getActivity().getApplicationContext(), URL, responseListener, errorListener, params);
    }

    private void clearPasswords() {
        pass1.getText().clear();
        pass2.getText().clear();
    }

    private boolean areFilled() { return token.getText().toString().length() != 0
            && pass1.getText().toString().length() != 0 && pass2.getText().toString().length() != 0;
    }

    private void enableButton() {
        changeButton.setEnabled( areFilled() );
    }

    private boolean passwordMatch() {
        boolean ret = pass1.getText() == pass2.getText();
        if( !ret ) {
            Toast.makeText(getActivity().getApplicationContext(), "Passwords are different", Toast.LENGTH_LONG).show();
            clearPasswords();
        }
        return ret;
    }

    private boolean rightToken() {
        boolean ret = false; //Comprovar que el token es el correcte
        if( !ret ) {
            Toast.makeText(getActivity().getApplicationContext(), "Wrong token", Toast.LENGTH_LONG).show();
            token.getText().clear();
        }
        return false;
    }

}