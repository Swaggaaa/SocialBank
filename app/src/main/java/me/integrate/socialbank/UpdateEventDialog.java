package me.integrate.socialbank;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class UpdateEventDialog extends DialogFragment {
    private static final String URL = "/events";
    private EditText input;


    public interface OnInputSelected {
        void sendInput(String input);
    }

    public OnInputSelected mOnInputSelected;

    public AlertDialog updateEventDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.update_event));
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_update_event, null);
        builder.setView(rootView);
        input = (EditText) rootView.findViewById(R.id.update_event);

        builder.setPositiveButton(getResources().getString(R.string.confirm), (dialogInterface, i) -> {
            if (input.getText().toString().length() != 0) {
                mOnInputSelected.sendInput(input.getText().toString());

            }
        });
        builder.setNegativeButton(getResources().getString(R.string.discard), (dialogInterface, i) -> {

        });
        return builder.create();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return updateEventDialog();

    }

    @Override
    public void onAttach (Context context) {
        super.onAttach(context);
        try {
            mOnInputSelected = (OnInputSelected) getTargetFragment();
        } catch (ClassCastException e) {

        }
    }

}