package me.integrate.socialbank;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyAccountFragment extends Fragment implements PaymentMethodNonceCreatedListener,
        BraintreeErrorListener, BraintreeCancelListener {
    private static final String URL = "/users";
    private static final String PURCHASE_URL = "/purchase";
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
    private int currentLocale;

    private ViewPager viewPager;
    private TabLayout tabs;
    private Adapter adapter;


    enum TransactionResults {
        ACCEPTED, REJECTED
    }

    private Spinner languageSpinner;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        new LanguageHelper();
        hoursPackages = new ArrayList<>();
        hoursPackages.add(new HoursPackage("Basic Package", 99.9, 100));
        hoursPackages.add(new HoursPackage("Professional Organization Package", 399.9, 500));
        hoursPackages.add(new HoursPackage("Premium Organization Package", 1199.9, 2000));
        View rootView = inflater.inflate(R.layout.fragment_my_account, container, false);
        accountStatus = (TextView) rootView.findViewById(R.id.AccountStatus);
        accountStatusImage = (TextView) rootView.findViewById(R.id.account_verified);
        verifyAccountHint = (TextView) rootView.findViewById(R.id.account_verify_hint);
        sendRequestText = (EditText) rootView.findViewById(R.id.editText_request);
        sendRequestButton = (Button) rootView.findViewById(R.id.button_send_request);
        purchaseHoursButton = (Button) rootView.findViewById(R.id.button_buy_hours);
        accountBalanceHint = (TextView) rootView.findViewById(R.id.account_balance_hint);
        userBalance = (TextView) rootView.findViewById(R.id.user_balance);
        languageSpinner = (Spinner) rootView.findViewById(R.id.language_spinner);
        languageSpinner.setSelection(LanguageHelper.getPosition(getResources().getConfiguration().locale.toString()));
        email = SharedPreferencesManager.INSTANCE.read(getActivity(), "user_email");
        loadingDialog = ProgressDialog.show(getActivity(), "",
                getString(R.string.loadingMessage), true);
        loadScreen(email);
        adapter = new Adapter(getChildFragmentManager());
        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabs = (TabLayout) rootView.findViewById(R.id.result_tabs);
        tabs.setupWithViewPager(viewPager);
        return rootView;
    }

    private void setupViewPager(ViewPager viewPager) {


        adapter.addFragment(new Package100Fragment(), "Package 100");
        adapter.addFragment(new Package500Fragment(), "Package 500");
        adapter.addFragment(new Package2000Fragment(), "Package 2000");
        viewPager.setAdapter(adapter);


    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void loadScreen(String emailUser) {

        APICommunicator apiCommunicator = new APICommunicator();
        @SuppressLint("SetTextI18n") Response.Listener responseListener = (Response.Listener<CustomRequest.CustomResponse>) response ->
        {
            JSONObject jsonObject;

            try {
                jsonObject = new JSONObject(response.response);
                verified = jsonObject.getBoolean("verified");
                Float balance;
                balance = BigDecimal.valueOf(jsonObject.getDouble("balance")).floatValue();
                userBalance.setText(balance.toString());
                userBalance.setVisibility(View.VISIBLE);
                if (balance < 0)
                    userBalance.setTextColor(this.getResources().getColor(R.color.negative_balance));
                else if (balance > 0)
                    userBalance.setTextColor(this.getResources().getColor(R.color.positive_balance));
                if (verified) {
                    accountStatusImage.setVisibility(View.VISIBLE);
                    accountStatus.setText(R.string.verified);
                    purchaseHoursButton.setVisibility(View.VISIBLE);
                    viewPager.setVisibility(View.VISIBLE);
                    tabs.setVisibility(View.VISIBLE);
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
        Response.ErrorListener errorListener = error -> errorTreatment(error.networkResponse.statusCode);

        apiCommunicator.getRequest(getActivity().getApplicationContext(), URL + '/' + emailUser, responseListener, errorListener, null);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sendRequestButton.setOnClickListener(v ->
        {
            loadingDialog = ProgressDialog.show(getActivity(), "",
                    getString(R.string.loadingMessage), true);
            sendRequest(sendRequestText.getText().toString());
        });
        purchaseHoursButton.setOnClickListener(v ->
        {
            purchaseHours();
        });

        languageSpinner.setSelected(false);
        languageSpinner.setSelection(languageSpinner.getSelectedItemPosition(), false);

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView adapter, View v, int i, long lng) {
                LanguageHelper.changeLocale(getContext().getResources(), i);
                Intent refresh = new Intent(getActivity(), MainActivity.class);
                startActivity(refresh);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });

    }

    private void sendRequest(String message) {
        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = (Response.Listener<CustomRequest.CustomResponse>) response ->
        {
            loadingDialog.dismiss();
            Toast.makeText(getActivity().getApplicationContext(), R.string.verification_requested, Toast.LENGTH_LONG).show();
        };
        Response.ErrorListener errorListener = error -> errorTreatment(error.networkResponse.statusCode);

        apiCommunicator.postRequest(getActivity().getApplicationContext(), URL + '/' + email + "/verified", responseListener, errorListener, message);
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

    private void purchaseHours() {
        loadingDialog = ProgressDialog.show(getActivity(), "",
                getString(R.string.initializing_paypal), true);
        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = (Response.Listener<CustomRequest.CustomResponse>) response ->
        {
            String clientToken = response.response;
            try {
                mBrainTreeFragment = BraintreeFragment.newInstance(this.getActivity(), clientToken);
            } catch (InvalidArgumentException ex) {
                ex.printStackTrace();
            }

            mBrainTreeFragment.addListener(this);

            chosenHoursPackage = hoursPackages.get(tabs.getSelectedTabPosition());

            PayPalRequest paypalRequest = new PayPalRequest(String.valueOf(chosenHoursPackage.getPrice()))
                    .currencyCode("EUR")
                    .displayName(chosenHoursPackage.getName())
                    .intent(PayPalRequest.INTENT_AUTHORIZE);
            PayPal.requestOneTimePayment(mBrainTreeFragment, paypalRequest);
        };

        Response.ErrorListener errorListener = error ->
        {
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

        Map<String, Object> params = new HashMap<>();
        apiCommunicator.getRequest(getActivity().getApplicationContext(), PURCHASE_URL, responseListener, errorListener, params);

    }

    @Override
    public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {
        loadingDialog.dismiss();
        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = (Response.Listener<CustomRequest.CustomResponse>) response ->
        {
            loadingDialog.dismiss();
            TransactionResults transactionResults = null;
            int hours = 0;
            try {
                JSONObject jsonObject = new JSONObject(response.response);
                transactionResults = TransactionResults.valueOf(
                        jsonObject.getString("transactionResults"));
                hours = jsonObject.getInt("hours");
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
            if (transactionResults == TransactionResults.ACCEPTED) {
                userBalance.setText(String.valueOf(
                        Double.valueOf(userBalance.getText().toString()) + hours));
                Toast.makeText(getActivity().getApplicationContext(), R.string.thank_you_purchase, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity().getApplicationContext(), R.string.payment_rejected, Toast.LENGTH_LONG).show();
            }
        };

        Response.ErrorListener errorListener = error ->
        {
            loadingDialog.dismiss();
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

        Map<String, Object> params = new HashMap<>();
        params.put("packageName", chosenHoursPackage.getName());
        params.put("amount", String.valueOf(chosenHoursPackage.getPrice()));
        params.put("nonce", paymentMethodNonce.getNonce());
        apiCommunicator.postRequest(getActivity().getApplicationContext(), PURCHASE_URL, responseListener, errorListener, params);
        loadingDialog = ProgressDialog.show(getActivity(), "",
                getString(R.string.verifying_payment), true);
    }

    @Override
    public void onCancel(int requestCode) {
        loadingDialog.dismiss();
        Toast.makeText(getActivity().getApplicationContext(), R.string.payment_not_finished, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onError(Exception error) {
        loadingDialog.dismiss();
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
