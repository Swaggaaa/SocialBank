package me.integrate.socialbank;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;


public class MyAccountFragment extends Fragment {
    private static final String URL = "/users";
    private TextView accountStatus;
    private TextView accountStatusImage;
    private TextView verifyAccountHint;
    private EditText sendRequestText;
    private Button sendRequestButton;
    private TextView userBalance;
    private Button buyHours;
    private boolean verified;
    private ProgressDialog loadingDialog;
    private String email;
    private Spinner languageSpinner;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        new LanguageHelper();
        View rootView = inflater.inflate(R.layout.fragment_my_account, container, false);
        accountStatus = (TextView) rootView.findViewById(R.id.AccountStatus);
        accountStatusImage = (TextView) rootView.findViewById(R.id.account_verified);
        verifyAccountHint = (TextView) rootView.findViewById(R.id.account_verify_hint);
        sendRequestText = (EditText) rootView.findViewById(R.id.editText_request);
        sendRequestButton = (Button) rootView.findViewById(R.id.button_send_request);
        userBalance = (TextView) rootView.findViewById(R.id.user_balance);
        buyHours = (Button) rootView.findViewById(R.id.button_buy_hours);
        languageSpinner = (Spinner) rootView.findViewById(R.id.language_spinner);
        languageSpinner.setSelection(LanguageHelper.getPosition(getResources().getConfiguration().locale.toString()));
        email = SharedPreferencesManager.INSTANCE.read(getActivity(),"user_email");
        loadingDialog = ProgressDialog.show(getActivity(), "",
                getString(R.string.loadingMessage), true);
        loadScreen(email);
        return rootView;
    }

    private void loadScreen(String emailUser) {

        APICommunicator apiCommunicator = new APICommunicator();
        @SuppressLint("SetTextI18n") Response.Listener responseListener = (Response.Listener<CustomRequest.CustomResponse>) response -> {
            JSONObject jsonObject;

            try {
                jsonObject = new JSONObject(response.response);
                verified = jsonObject.getBoolean("verified");
                Float balance;
                balance = BigDecimal.valueOf(jsonObject.getDouble("balance")).floatValue();
                userBalance.setText(balance.toString());
                userBalance.setVisibility(View.VISIBLE);
                if (balance < 0) userBalance.setTextColor(this.getResources().getColor(R.color.negative_balance));
                else if (balance > 0) userBalance.setTextColor(this.getResources().getColor(R.color.positive_balance));
                if (verified) {
                    accountStatusImage.setVisibility(View.VISIBLE);
                    accountStatus.setText(R.string.verified);
                    buyHours.setVisibility(View.VISIBLE);
                }
                else {
                    accountStatus.setText(R.string.standard);
                    verifyAccountHint.setVisibility(View.VISIBLE);
                    sendRequestText.setVisibility(View.VISIBLE);
                    sendRequestButton.setVisibility(View.VISIBLE);
                }
                loadingDialog.dismiss();

            } catch (JSONException e) {
                e.printStackTrace();
            }

        };
        Response.ErrorListener errorListener = error -> errorTreatment(error.networkResponse.statusCode);

        apiCommunicator.getRequest(getActivity().getApplicationContext(), URL + '/' + emailUser, responseListener, errorListener, null);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sendRequestButton.setOnClickListener(v -> {
            loadingDialog = ProgressDialog.show(getActivity(), "",
                    getString(R.string.loadingMessage), true);
            sendRequest(sendRequestText.getText().toString());
        });
        buyHours.setOnClickListener(v -> {
            //ready per comprar hores
        });



       /* languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView adapter, View v, int i, long lng) {
                int previousLocale = LanguageHelper.getPosition(getResources().getConfiguration().locale.toString());
                LanguageHelper.changeLocale(getContext().getResources(), i);
                if (previousLocale != i) {
                    Intent refresh = new Intent(getActivity(), MainActivity.class);
                    startActivity(refresh);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView)
            {

            }
        });*/

    }

    private void sendRequest(String message) {
        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = (Response.Listener<CustomRequest.CustomResponse>) response -> {
            loadingDialog.dismiss();
            Toast.makeText(getActivity().getApplicationContext(), R.string.verification_requested, Toast.LENGTH_LONG).show();
        };
        Response.ErrorListener errorListener = error -> errorTreatment(error.networkResponse.statusCode);

        apiCommunicator.postRequest(getActivity().getApplicationContext(), URL+'/'+email+"/verified", responseListener, errorListener, message);
    }

    private void errorTreatment(int errorCode) {
        String message;
        if (errorCode == 401)
            message = getString(R.string.unauthorized);
        else if (errorCode == 403)
            message = getString(R.string.forbidden);
        else if (errorCode == 404)
            message = getString(R.string.not_found);
        else
            message = getString(R.string.unexpectedError);

        loadingDialog.dismiss();
        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }


}
