package me.integrate.socialbank;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class RegisterFragment extends Fragment {
    private static final int RC_SIGN_IN = 9001;
    private static final String URL = "/users";
    private GoogleSignInClient mGoogleSignInClient;
    private EditText name;
    private EditText lastName;
    private EditText email;
    private EditText password;
    private Button SignUpGoogle;
    private Button SignUpButton;
    private Spinner gender;
    private EditText birthdate;
    private String strDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_register, container, false);
        name = (EditText) rootView.findViewById(R.id.editTextFirstName);
        lastName = (EditText) rootView.findViewById(R.id.editTextLastName);
        birthdate = (EditText) rootView.findViewById(R.id.birthdate);
        gender = (Spinner) rootView.findViewById(R.id.editTextGender);
        email = (EditText) rootView.findViewById(R.id.editTextEmail);
        password = (EditText) rootView.findViewById(R.id.editTextPassword);
        SignUpButton = (Button) rootView.findViewById(R.id.buttonRegister);
        SignUpGoogle = (Button) rootView.findViewById(R.id.googleSignInButton);
        enableButton();
        initGoogleLogin();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SignUpGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
        SignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> params = new HashMap<>();
                params.put("name", name.getText().toString());
                params.put("surname", lastName.getText().toString());
                params.put("birthdate", strDate);
                params.put("gender", gender.getSelectedItem().toString().toUpperCase());
                params.put("email", email.getText().toString());
                params.put("password", password.getText().toString());

                postCredentials(params);
            }
        });
        name.addTextChangedListener(new TextWatcher() {
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
        lastName.addTextChangedListener(new TextWatcher() {
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
        password.addTextChangedListener(new TextWatcher() {
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
        birthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });
    }

    private boolean areFilled() {
        return !name.getText().toString().isEmpty() && !lastName.getText().toString().isEmpty()
                && !email.getText().toString().isEmpty() && !password.getText().toString().isEmpty()
                && !birthdate.getText().toString().isEmpty();
    }

    private void enableButton() {
        SignUpButton.setEnabled(areFilled());
    }

    private void initGoogleLogin() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity().getApplicationContext(), gso);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            if (account.getEmail().length() != 0) email.setText(account.getEmail());
            if (account.getDisplayName().length() != 0) name.setText(account.getGivenName());
            if (account.getFamilyName().length() != 0) lastName.setText(account.getFamilyName());
            //Todas las funciones que hacer cuando Google nos devuelve el mail
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Main Screen", "signInResult:failed code=" + e.getStatusCode());
            //Excepción a lanzar
        }
    }

    private void postCredentials(HashMap<String, String> params) {
        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                Toast.makeText(getActivity().getApplicationContext(), "Account created!", Toast.LENGTH_LONG).show();
                Fragment loginFragment = new LoginFragment();
                FragmentChangeListener fc = (FragmentChangeListener) getActivity();
                fc.replaceFragment(loginFragment);

            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
            }
        };


        apiCommunicator.postRequest(getActivity().getApplicationContext(), URL, responseListener, errorListener, params);
    }

    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because january is zero
                final String selectedDate = day + " / " + (month + 1) + " / " + year;

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                strDate = format.format(calendar.getTime());
                birthdate.setText(selectedDate);
                enableButton();
            }
        });
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

}
