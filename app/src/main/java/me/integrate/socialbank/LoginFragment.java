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

public class LoginFragment extends Fragment {
    private static final String URL = "/login";
    private EditText user;
    private EditText password;
    private Button LogIn;
    Intent i;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        user = (EditText) rootView.findViewById(R.id.email);
        password = (EditText) rootView.findViewById(R.id.password);
        LogIn = (Button) rootView.findViewById(R.id.log_in_button);

        enableButton();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        i = new Intent(getActivity().getApplicationContext(), InsideActivity.class);
        getView().findViewById(R.id.log_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // disable button
                if (user.getText().toString().length() != 0 && password.getText().toString().length() != 0) {
                    postCredentials(user.getText().toString(), password.getText().toString());
                }
            }
        });
        getView().findViewById(R.id.register_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerSelected();
            }
        });
        user.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                enableButton();
            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                enableButton();
            }
        });
    }

    private boolean areFilled() {
        return user.getText().toString().length() != 0 && password.getText().toString().length() != 0;
    }

    //Se extrae en función externa por si se quiere modificar el estilo
    private void enableButton() {
        LogIn.setEnabled( areFilled() );
    }

    //función para llamar a la API
    private void postCredentials(String user, String password) {

        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = new Response.Listener<CustomRequest.CustomResponse>() {
            @Override
            public void onResponse(CustomRequest.CustomResponse response) {
                String token = response.headers.get("Authorization");
                SharedPreferencesManager.INSTANCE.store(getActivity(),"token",token);
                startActivity(i);
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                cleanPassword();
                Toast.makeText(getActivity().getApplicationContext(), "Email or password incorrect", Toast.LENGTH_LONG).show();
            }
        };
        HashMap<String, String> params = new HashMap<>();
        params.put("email", user);
        params.put("password", password);

        apiCommunicator.postRequest(getActivity().getApplicationContext(), URL, responseListener, errorListener, params);
    }

    private void cleanPassword() {
        password.getText().clear();
    }


    private void registerSelected() {
        Fragment registerFragment = new RegisterFragment();
        FragmentChangeListener fc = (FragmentChangeListener) getActivity();
        fc.replaceFragment(registerFragment);
    }
}
