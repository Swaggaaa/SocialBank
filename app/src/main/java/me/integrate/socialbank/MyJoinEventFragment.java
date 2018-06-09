package me.integrate.socialbank;


import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;

public class MyJoinEventFragment extends EventFragment {

    private static final String URL = "/users";
    private String emailUser;

    private float balance;
    private boolean verified;
    private boolean joined;

    public static MyJoinEventFragment newInstance(Bundle params) {
        MyJoinEventFragment myJoinEventFragment = new MyJoinEventFragment();
        myJoinEventFragment.setArguments(params);
        return myJoinEventFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        emailUser = SharedPreferencesManager.INSTANCE.read(getActivity(),"user_email");
        getUserInfo();
        if(!verified) joinedOrNot();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        join_button.setOnClickListener(v->
        {
            HashMap<String, String> params = new HashMap<>();
            params.put("id", String.valueOf(id));
            if (joined) {
                AlertDialog.Builder dialogDelete = new AlertDialog.Builder(getContext());
                dialogDelete.setTitle(getResources().getString(R.string.are_sure));
                dialogDelete.setMessage(getResources().getString(R.string.confirm_disjoin_event));
                dialogDelete.setCancelable(false);
                dialogDelete.setPositiveButton(getResources().getString(R.string.confirm), (dialogInterface, i) -> {
                    //Call to API's function
                    cancelJoinEvent(params);
                    join_button.setText(getResources().getString(R.string.join));
                });
                dialogDelete.setNegativeButton(getResources().getString(R.string.discard), (dialogInterface, i) -> {
                });
                dialogDelete.show();
            } else if (hasHours() && !isEventFull())
                joinEvent(params);
            else if (!hasHours())
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.not_hours_msg), Toast.LENGTH_LONG).show();
            else
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.event_full_msg), Toast.LENGTH_LONG).show();
        });
    }

    private void getUserInfo() {
        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = (Response.Listener<CustomRequest.CustomResponse>) response -> {
            JSONObject jsonObject;
            try{
                jsonObject = new JSONObject(response.response);
                balance = BigDecimal.valueOf(jsonObject.getDouble("balance")).floatValue();
                verified = jsonObject.getBoolean("verified");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        };
        Response.ErrorListener errorListener = error ->  errorTreatment(error.networkResponse.statusCode);

        apiCommunicator.getRequest(getActivity().getApplicationContext(), URL+'/'+ emailUser, responseListener, errorListener, null);
    }

    private void errorTreatment(int errorCode) {
        String message;
        if (errorCode == 401)
            message = getString(R.string.Unauthorized);
        else if(errorCode == 403)
            message = getString(R.string.Forbidden);
        else if(errorCode == 404)
            message = getString(R.string.NotFound);
        else
            message = getString(R.string.UnexpectedError);

        Toast.makeText(getActivity().getApplicationContext(), errorCode + " - " + message, Toast.LENGTH_LONG).show();
    }

    private boolean hasHours() {
        long aux = getHours(iniDate,endDate);
        int hours = (int) aux;
        int bal = (int) balance;
        return bal - hours >= -10;
    }

    //To JOIN an event
    private void joinEvent(HashMap<String, String> params) {
        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = (Response.Listener<CustomRequest.CustomResponse>) response -> {
            Toast.makeText(getActivity().getApplicationContext(), R.string.joined_msg, Toast.LENGTH_LONG).show();
            checkJoin();
            changesEnrollment(true);
        };
        Response.ErrorListener errorListener = error ->  errorTreatment(error.networkResponse.statusCode);
        apiCommunicator.postRequest(getActivity().getApplicationContext(), "/events/" + id + "/enrollments", responseListener, errorListener, params);
    }

    //To CANCEL your enrollment to an event
    private void cancelJoinEvent(HashMap<String, String> params) {
        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = (Response.Listener<CustomRequest.CustomResponse>) response -> {
            Toast.makeText(getActivity().getApplicationContext(), R.string.canceljoin_msg, Toast.LENGTH_LONG).show();
            checkJoin();
            changesEnrollment(false);
        };
        Response.ErrorListener errorListener = error ->  errorTreatment(error.networkResponse.statusCode);
        apiCommunicator.deleteRequest(getActivity().getApplicationContext(), "/events/" + id + "/enrollments", responseListener, errorListener, params);
    }

    //Check if that user is already joined
    private void joinedOrNot() {
        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = (Response.Listener<CustomRequest.CustomResponse>) response -> {
            loadJoinButton(response.response);
        };
        Response.ErrorListener errorListener = error ->  errorTreatment(error.networkResponse.statusCode);
        apiCommunicator.getRequest(getActivity().getApplicationContext(), "/events/" + id + "/enrollments", responseListener, errorListener, null);
    }

    private void loadJoinButton(String users) {
        if(users.contains(emailUser)) joined = false;
        else joined = true;
        checkJoin();
    }

    private void checkJoin() {
        joined = !joined;
        if (!joined) join_button.setText(R.string.join);
        else  join_button.setText(R.string.cancel_btn);
        join_button.setVisibility(View.VISIBLE);
    }

}
