package me.integrate.socialbank;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddCommentFragment extends DialogFragment {
    private static final String URL = "/events";
    private EditText comment;
    private Button submit;

    public AlertDialog addCommentFragment() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add comment");
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.fragment_add_comment, null);
        builder.setView(rootView);
        comment = (EditText) rootView.findViewById(R.id.new_comment);
        submit = (Button) rootView.findViewById(R.id.button_comment);

        submit.setOnClickListener(v -> {
            DialogInterface dialog;
            submit.setEnabled(false);
            if (comment.getText().toString().length() != 0) {
                postComment(comment.getText().toString());
            }

        });
        comment.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                enableButton();
            }
        });
        return builder.create();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return addCommentFragment();

    }

    private void postComment(String comment) {

        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = (Response.Listener<CustomRequest.CustomResponse>) response -> {

            Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.new_comment_created), Toast.LENGTH_LONG).show();

        };
        Response.ErrorListener errorListener = error -> errorTreatment(error.networkResponse.statusCode);
        Map<String, String> params = new HashMap<>();
        params.put("content", comment);

        apiCommunicator.postRequest(getActivity().getApplicationContext(), URL + '/' + "comments", responseListener, errorListener, params);
    }

    private void errorTreatment(int errorCode) {
        String message;
        if (errorCode == 401)
            message = getString(R.string.Unauthorized);
        else if (errorCode == 403)
            message = getString(R.string.Forbidden);
        else if (errorCode == 404)
            message = getString(R.string.NotFound);
        else
            message = getString(R.string.UnexpectedError);

        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private boolean areFilled() {
        return !comment.getText().toString().isEmpty();
    }

    //Se extrae en función externa por si se quiere modificar el estilo
    private void enableButton() {
        submit.setEnabled(areFilled());
    }


}
