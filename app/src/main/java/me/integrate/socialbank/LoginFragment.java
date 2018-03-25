package me.integrate.socialbank;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.HashMap;

public class LoginFragment extends Fragment {
    private static final String URL = "/login";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getView().findViewById(R.id.log_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postCredentials("user", "password");
                //registerSelected();

            }
        });
    }

    //función para llamar a la API
    private void postCredentials(String user, String password) {
        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                Toast.makeText(getActivity().getApplicationContext(), "OK!", Toast.LENGTH_LONG).show();

            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
            }
        };
        HashMap<String, String> params = new HashMap<>();
        params.put("user", user);
        params.put("password", password);

        apiCommunicator.postRequest(getActivity().getApplicationContext(), URL, responseListener, errorListener, params);
    }


    private void registerSelected() {
        Fragment registerFragment = new RegisterFragment();
        FragmentChangeListener fc = (FragmentChangeListener) getActivity();
        fc.replaceFragment(registerFragment);
    }
}
