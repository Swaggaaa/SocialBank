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

import java.util.HashMap;
import java.util.Map;

public class LoginFragment extends Fragment {
    private static final String URL = "/login";
    private EditText user;
    private EditText password;
    private Button loginButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        user = (EditText) rootView.findViewById(R.id.email);
        password = (EditText) rootView.findViewById(R.id.password);
        loginButton = (Button) rootView.findViewById(R.id.log_in_button);

        enableButton();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loginButton.setOnClickListener(view1 -> {
            loginButton.setEnabled(false);
            loginButton.setText(R.string.loading);
            user.setEnabled(false);
            password.setEnabled(false);

            if (user.getText().toString().length() != 0 && password.getText().toString().length() != 0) {
                postCredentials(user.getText().toString(), password.getText().toString());
            }
        });
        view.findViewById(R.id.register_button).setOnClickListener(v -> registerSelected());
        view.findViewById(R.id.forgot_button).setOnClickListener(v -> forgotPasswordSelected());
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
        return !user.getText().toString().isEmpty() && !password.getText().toString().isEmpty();
    }

    //Se extrae en función externa por si se quiere modificar el estilo
    private void enableButton() {
        loginButton.setEnabled(areFilled());
    }

    //función para llamar a la API
    private void postCredentials(String user, String password) {

        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = (Response.Listener<CustomRequest.CustomResponse>) response -> {
            String token = response.headers.get("Authorization");
            SharedPreferencesManager.INSTANCE.store(getActivity(), "token", token);
            startActivity(new Intent(getActivity().getApplicationContext(), InsideActivity.class));
            getActivity().finish();
        };
        Response.ErrorListener errorListener = error -> {
            cleanPassword();
            loginButton.setEnabled(true);
            this.user.setEnabled(true);
            this.password.setEnabled(true);
            loginButton.setText(R.string.login);
            Toast.makeText(getActivity().getApplicationContext(), "Email or password incorrect", Toast.LENGTH_LONG).show();
        };
        Map<String, String> params = new HashMap<>();
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
    private void forgotPasswordSelected() {
        Fragment recoverPasswordFragment = new RecoverPasswordFragment();
        FragmentChangeListener fc = (FragmentChangeListener) getActivity();
        fc.replaceFragment(recoverPasswordFragment);
    }
}
