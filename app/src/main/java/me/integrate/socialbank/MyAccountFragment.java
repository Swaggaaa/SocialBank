package me.integrate.socialbank;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MyAccountFragment extends Fragment {
    private static final String URL = "/users";
    private TextView accountStatus;
    private TextView accountStatusImage;
    private TextView verifyAccountHint;
    private EditText sendRequestText;
    private Button sendRequestButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_my_account, container, false);
        accountStatus = (TextView)rootView.findViewById(R.id.AccountStatus);
        accountStatusImage = (TextView)rootView.findViewById(R.id.account_verified);
        verifyAccountHint = (TextView)rootView.findViewById(R.id.account_verify_hint);
        sendRequestText = (EditText)rootView.findViewById(R.id.editText_request);
        sendRequestButton = (Button)rootView.findViewById(R.id.button_send_request);
        loadScreen();
        return rootView;
    }

    private void loadScreen() {

    }
}
