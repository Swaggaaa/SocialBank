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
        submitButton.setOnClickListener(v -> {
            submitButton.setEnabled(false);
            if (email.getText().toString().length() != 0) {
                postCredentials(email.getText().toString());
            }
        });
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                enableButton();
            }
        });

        view.findViewById(R.id.token_button).setOnClickListener(view1 -> tokenSelected());

    }

    private void postCredentials(String email) {

        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = (Response.Listener<CustomRequest.CustomResponse>) response -> {
            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.account_created), Toast.LENGTH_LONG).show();
            tokenSelected();
        };
        Response.ErrorListener errorListener = error -> {
            submitButton.setEnabled(true);
            Toast.makeText(getActivity().getApplicationContext(), "An unexpected error has occurred", Toast.LENGTH_LONG).show();
        };
        apiCommunicator.postRequest(getActivity().getApplicationContext(), URL, responseListener, errorListener, email);
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
