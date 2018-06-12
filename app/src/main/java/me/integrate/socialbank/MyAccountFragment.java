package me.integrate.socialbank;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.Nullable;
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
import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.PayPal;
import com.braintreepayments.api.exceptions.BraintreeError;
import com.braintreepayments.api.exceptions.ErrorWithResponse;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.interfaces.BraintreeCancelListener;
import com.braintreepayments.api.interfaces.BraintreeErrorListener;
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;
import com.braintreepayments.api.models.PayPalRequest;
import com.braintreepayments.api.models.PaymentMethodNonce;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyAccountFragment extends Fragment implements PaymentMethodNonceCreatedListener,
        BraintreeErrorListener, BraintreeCancelListener {
    private static final String URL = "/users";
    private TextView accountStatus;
    private TextView accountStatusImage;
    private TextView verifyAccountHint;
    private EditText sendRequestText;
    private Button sendRequestButton;
    private Button purchaseHoursButton;
    private TextView accountBalanceHint;
    private TextView userBalance;
    private boolean verified;
    private ProgressDialog loadingDialog;
    private String email;
    private BraintreeFragment mBrainTreeFragment;
    private List<HoursPackage> hoursPackages;
    private HoursPackage chosenHoursPackage;


    enum TransactionResults {
        ACCEPTED, REJECTED
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_my_account, container, false);
        hoursPackages.add(new HoursPackage("Basic Package", 99.9, 100));
        hoursPackages.add(new HoursPackage("Professional Organization Package", 399.9, 500));
        hoursPackages.add(new HoursPackage("Premium Organization Package", 1199.9, 2000));
        accountStatus = (TextView) rootView.findViewById(R.id.AccountStatus);
        accountStatusImage = (TextView) rootView.findViewById(R.id.account_verified);
        verifyAccountHint = (TextView) rootView.findViewById(R.id.account_verify_hint);
        sendRequestText = (EditText) rootView.findViewById(R.id.editText_request);
        sendRequestButton = (Button) rootView.findViewById(R.id.button_send_request);
        purchaseHoursButton = (Button) rootView.findViewById(R.id.button_buy_hours);
        accountBalanceHint = (TextView) rootView.findViewById(R.id.account_balance_hint);
        userBalance = (TextView) rootView.findViewById(R.id.user_balance);
        email = SharedPreferencesManager.INSTANCE.read(getActivity(), "user_email");
        loadingDialog = ProgressDialog.show(getActivity(), "",
                getString(R.string.loadingMessage), true);
        loadScreen(email);
        return rootView;
    }

    private void loadScreen(String emailUser) {

        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = (Response.Listener<CustomRequest.CustomResponse>) response -> {
            JSONObject jsonObject;

            try {
                jsonObject = new JSONObject(response.response);
                verified = jsonObject.getBoolean("verified");
                Float balance = null;
                balance = BigDecimal.valueOf(jsonObject.getDouble("balance")).floatValue();
                userBalance.setText(balance.toString());
                userBalance.setVisibility(View.VISIBLE);
                if (balance < 0) userBalance.setTextColor(Color.RED);
                else if (balance > 0) userBalance.setTextColor(Color.GREEN);
                else userBalance.setTextColor(Color.BLUE);
                if (verified) {
                    accountStatusImage.setVisibility(View.VISIBLE);
                    accountStatus.setText(R.string.verified);
                    purchaseHoursButton.setVisibility(View.VISIBLE);
                } else {
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
        Response.ErrorListener errorListener = error -> {
            String message;
            int errorCode = error.networkResponse.statusCode;
            if (errorCode == 401)
                message = getString(R.string.unauthorized);
            else if (errorCode == 403)
                message = getString(R.string.forbidden);
            else if (errorCode == 404)
                message = getString(R.string.NotFound);
            else if (errorCode == 409)
                message = getString(R.string.user_already_reported);
            else
                message = getString(R.string.unexpectedError);
            loadingDialog.dismiss();
            Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
        };

        apiCommunicator.getRequest(getActivity().getApplicationContext(), URL + '/' + emailUser, responseListener, errorListener, null);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sendRequestButton.setOnClickListener(v -> {
            loadingDialog = ProgressDialog.show(getActivity(), "",
                    getString(R.string.loadingMessage), true);
            HashMap<String, String> params = new HashMap<>();
            params.put("message ", sendRequestText.getText().toString());
            sendRequest(params);
        });
        purchaseHoursButton.setOnClickListener(v -> {
            purchaseHours();
        });
    }

    private void sendRequest(HashMap<String, String> params) {
        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = (Response.Listener<CustomRequest.CustomResponse>) response -> {
            loadingDialog.dismiss();
            Toast.makeText(getActivity().getApplicationContext(), R.string.verification_requested, Toast.LENGTH_LONG).show();
        };
        Response.ErrorListener errorListener = error -> {
            loadingDialog.dismiss();
            String message;
            int errorCode = error.networkResponse.statusCode;
            if (errorCode == 401)
                message = getString(R.string.unauthorized);
            else if (errorCode == 403)
                message = getString(R.string.forbidden);
            else if (errorCode == 404)
                message = getString(R.string.NotFound);
            else if (errorCode == 409)
                message = getString(R.string.awaiting_approval);
            else
                message = getString(R.string.unexpectedError);
            Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();

        };

        apiCommunicator.postRequest(getActivity().getApplicationContext(), URL + '/' + email + "/verified", responseListener, errorListener, params);
    }

    private void purchaseHours() {
        try {
            mBrainTreeFragment = BraintreeFragment.newInstance(this.getActivity(), "sandbox_zr8s9syj_y3bwvxzq3zwfhvw9");
        } catch (InvalidArgumentException ex) {
            ex.printStackTrace();
        }

        mBrainTreeFragment.addListener(this);

        //TODO: JOAN; AQUI MARCA EL PACKAGE KLK
        chosenHoursPackage = hoursPackages.get(0);

        PayPalRequest paypalRequest = new PayPalRequest(String.valueOf(chosenHoursPackage.getPrice()))
                .currencyCode("EUR")
                .displayName(chosenHoursPackage.getName())
                .intent(PayPalRequest.INTENT_AUTHORIZE);
        PayPal.requestOneTimePayment(mBrainTreeFragment, paypalRequest);
    }

    @Override
    public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {
        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = (Response.Listener<CustomRequest.CustomResponse>) response -> {
            TransactionResults transactionResults = TransactionResults.valueOf(response.response);
            if (transactionResults == TransactionResults.ACCEPTED) {
                Toast.makeText(getActivity().getApplicationContext(), R.string.thank_you_purchase, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity().getApplicationContext(), R.string.payment_rejected, Toast.LENGTH_LONG).show();
            }
        };

        Response.ErrorListener errorListener = error -> {
            String message;
            int errorCode = error.networkResponse == null ? -1 : error.networkResponse.statusCode;
            if (errorCode == 401)
                message = "Unauthorized";
            else if (errorCode == 403)
                message = "Forbidden";
            else if (errorCode == 404)
                message = "Not Found";
            else
                message = "Unexpected error";

            loadingDialog.dismiss();
            Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
        };

        Map<String, String> params = new HashMap<>();
        params.put("amount", String.valueOf(chosenHoursPackage.getPrice()));
        params.put("nonce", paymentMethodNonce.getNonce());
        apiCommunicator.postRequest(getActivity().getApplicationContext(), URL, responseListener, errorListener, params);
    }

    @Override
    public void onCancel(int requestCode) {
        Toast.makeText(getActivity().getApplicationContext(), R.string.payment_not_finished, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onError(Exception error) {
        if (error instanceof ErrorWithResponse) {
            ErrorWithResponse errorWithResponse = (ErrorWithResponse) error;
            BraintreeError cardErrors = errorWithResponse.errorFor("creditCard");
            if (cardErrors != null) {
                // There is an issue with the credit card.
                BraintreeError expirationMonthError = cardErrors.errorFor("expirationMonth");
                if (expirationMonthError != null) {
                    // There is an issue with the expiration month.
                    System.out.println(expirationMonthError.getMessage());
                }
            }
        }
    }
}
