package me.integrate.socialbank;


import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;

import java.io.IOException;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;


public class MyEventFragment extends EventFragment{

    private boolean thereisPic;
    ImageView eventPicture;
    EditText editDescription;
    Button updateButton;

    private static final String URL = "/events";
    private Button delete_button;

    public static MyEventFragment newInstance(Bundle params) {
        MyEventFragment myEventFragment = new MyEventFragment();
        myEventFragment.setArguments(params);
        return myEventFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        editDescription = (EditText) view.findViewById(R.id.editDescription);
        updateButton = (Button) view.findViewById(R.id.buttonUpdate);
        FloatingActionButton editEvent = (FloatingActionButton) view.findViewById(R.id.editEvent);
        editEvent.setVisibility(View.VISIBLE);
        ImageView changeUserPhoto = (ImageView) view.findViewById(R.id.loadPicture);
        changeUserPhoto.setVisibility(View.VISIBLE);

        delete_button = (Button) view.findViewById(R.id.delete_event);
        id = getArguments().getInt("id");
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        delete_button.setVisibility(View.VISIBLE);
        delete_button.setOnClickListener(v -> {

            AlertDialog.Builder dialogDelete = new AlertDialog.Builder(getContext());
            dialogDelete.setTitle(getResources().getString(R.string.are_sure));
            dialogDelete.setMessage(getResources().getString(R.string.confirm_delete_event));
            dialogDelete.setCancelable(false);
            dialogDelete.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    deleteEvent();
                    Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.delete_event), Toast.LENGTH_LONG).show();
                }
            });
            dialogDelete.setNegativeButton(getResources().getString(R.string.discard), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            dialogDelete.show();
        });
    }

    //Call API for delete an event
    void deleteEvent() {
        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = (Response.Listener<CustomRequest.CustomResponse>) response -> {
            if (getArguments().getBoolean("MyProfile")) profileSelected();
            else boardSelected();
        };
        Response.ErrorListener errorListener = error -> {
            String message;
            int errorCode = error.networkResponse.statusCode;
            if (errorCode == 401)
                message = getString(R.string.Unauthorized);
            else if(errorCode == 403)
                message = getString(R.string.Forbidden);
            else if(errorCode == 404)
                message = getString(R.string.NotFound);
            else
                message = getString(R.string.UnexpectedError);

            Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
        };

        apiCommunicator.deleteRequest(getActivity().getApplicationContext(), URL +'/'+ id, responseListener, errorListener, null);

    }

    private void boardSelected() {
        Fragment boardFragment = new BoardFragment();
        FragmentChangeListener fc = (FragmentChangeListener) getActivity();
        fc.replaceFragment(boardFragment);
    }

    private void profileSelected() {
        Fragment profileFragment = new ProfileFragment();
        FragmentChangeListener fc = (FragmentChangeListener) getActivity();
        fc.replaceFragment(profileFragment);
    }


}
        thereisPic = false;
        view.findViewById(R.id.loadPicture).setOnClickListener(v ->
        {
            readGallery();
        });
        view.findViewById(R.id.editProfile).setOnClickListener(v ->
        {
            editDescription.setVisibility(View.VISIBLE);
            updateButton.setVisibility(View.VISIBLE);
            textEventDescription.setVisibility(View.GONE);

        });
        view.findViewById(R.id.buttonUpdate).setOnClickListener(v ->
        {
            updateButton.setEnabled(false);

            if (editDescription.getText().toString().length() != 0) {
                descriptionEvent = editDescription.getText().toString();


            }
        });
    }


    private void updateProfile() {
        HashMap<String, String> params = new HashMap<>();
        params.put("description", descriptionEvent);
        params.put("image", thereisPic ? ImageCompressor.INSTANCE.compressAndEncodeAsBase64(
                ((BitmapDrawable)eventPicture.getDrawable()).getBitmap())
                : "");

        putCredentials(params);
    }

    private void putCredentials(HashMap<String, String> params) {
        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = response -> {
            Toast.makeText(getActivity().getApplicationContext(), "Image changed!", Toast.LENGTH_LONG).show();

        };
        Response.ErrorListener errorListener = error -> Toast.makeText(getActivity().getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();


        apiCommunicator.putRequest(getActivity().getApplicationContext(), "event/" + id, responseListener, errorListener, params);
    }


    private void readGallery() {
        Intent pickAnImage = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickAnImage.setType("image/*");

        startActivityForResult(pickAnImage, 2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == 2 ) {
                thereisPic = true;
                data.getData();
                Uri selectedImage = data.getData();
                Log.v(TAG, "Selected image uri" + selectedImage);
                Log.v(TAG, String.valueOf(selectedImage));
                loadImageFromUri(selectedImage);
                updateProfile();
            }
        }
        else{
            Log.v("Result","Something happened when tried to get the image");
        }
    }

    private void loadImageFromUri(Uri imageUri) {
        try {
            eventPicture.setImageBitmap(MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


