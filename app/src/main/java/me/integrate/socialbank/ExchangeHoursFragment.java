package me.integrate.socialbank;

import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;

public class ExchangeHoursFragment extends Fragment {

    private EditText editTextToken;
    private Button buttonExchangeToken;
    private ProgressDialog loadingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_exchange_hours, container, false);
        editTextToken = (EditText) rootView.findViewById(R.id.editTextToken);
        buttonExchangeToken = (Button) rootView.findViewById(R.id.buttonExchangeToken);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buttonExchangeToken.setOnClickListener(v -> exchangeToken());
    }

    private void exchangeToken() {
        String token = editTextToken.getText().toString();
        if (token.isEmpty()) {
            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.insert_token_first), Toast.LENGTH_LONG).show();
            return;
        }
        loadingDialog = ProgressDialog.show(getActivity(), "",
                getString(R.string.loadingMessage), true);

        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = (Response.Listener<CustomRequest.CustomResponse>) response ->
        {
            loadingDialog.dismiss();
            Toast.makeText(getActivity().getApplicationContext(), R.string.exchanged_success, Toast.LENGTH_LONG).show();
        };
        Response.ErrorListener errorListener = error -> errorTreatment(error.networkResponse.statusCode);

        apiCommunicator.postRequest(getActivity().getApplicationContext(),
                "/events/" + extractIdFromToken(token) + "/exchange",
                responseListener, errorListener, editTextToken.getText().toString());

    }

    private String extractIdFromToken(String token) {
        return token.split("-")[0];
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
