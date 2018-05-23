package me.integrate.socialbank;


import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class MyJoinEventFragment extends EventFragment {

    private static final String URL = "/users";
    private String emailUser;


    public static MyJoinEventFragment newInstance(Bundle params) {
        MyJoinEventFragment myJoinEventFragment = new MyJoinEventFragment();
        myJoinEventFragment.setArguments(params);
        return myJoinEventFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        emailUser = SharedPreferencesManager.INSTANCE.read(getActivity(),"user_email");

        return view;
    }

    //Call to the api for the events by creator
    private void getAllEventsByUser() {

        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = (Response.Listener<CustomRequest.CustomResponse>) response -> {
            JSONArray jsonArray;
            try {
                jsonArray = new JSONArray(response.response);
                System.out.println(String.valueOf(jsonArray.length()));
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Event event = new Event(jsonObject);
                    if (event.getId() == id) join_button.setText("Disjoin");

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        };
        Response.ErrorListener errorListener = error -> errorTreatment(error.networkResponse.statusCode);

        apiCommunicator.getRequest(getActivity().getApplicationContext(), URL +'/'+ emailUser + "/events", responseListener, errorListener, null);
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

        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }


    //TODO change set text
    //TODO hacer llamadas api
    //TODO controlar número asistentes
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getAllEventsByUser();
        //if (!join_button.getText().equals("Disjoin")) join_button.setText("Join");
        join_button.setVisibility(View.VISIBLE);
        join_button.setOnClickListener(v->
        {
            if (join_button.getText().equals("Disjoin"))
            {
                AlertDialog.Builder dialogDelete = new AlertDialog.Builder(getContext());
                dialogDelete.setTitle(getResources().getString(R.string.are_sure));
                dialogDelete.setMessage(getResources().getString(R.string.confirm_delete_event));
                dialogDelete.setCancelable(false);
                dialogDelete.setPositiveButton(getResources().getString(R.string.confirm), (dialogInterface, i) -> {
                    //Call to the api function
                    join_button.setText("Join");
                    Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.delete_event), Toast.LENGTH_LONG).show();

                });
                dialogDelete.setNegativeButton(getResources().getString(R.string.discard), (dialogInterface, i) -> {
                });
                dialogDelete.show();

            } else {
                join_button.setText("Join");
                //call to the api function
            }

        });
    }

}
