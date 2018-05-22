package me.integrate.socialbank;


import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;

import java.io.IOException;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;


public class MyEventFragment extends EventFragment{

    private Button updateButton;
    private ImageView editEvent;
    private ImageView changeEventPhoto;


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
        updateButton = (Button) view.findViewById(R.id.buttonUpdate);
        editEvent = (ImageView) view.findViewById(R.id.editEvent);
        changeEventPhoto = (ImageView) view.findViewById(R.id.loadPicture);
        delete_button = (Button) view.findViewById(R.id.delete_event);
        id = getArguments().getInt("id");
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        join_button.setVisibility(View.GONE);
        delete_button.setVisibility(View.VISIBLE);
        editEvent.setVisibility(View.VISIBLE);
        changeEventPhoto.setVisibility(View.VISIBLE);
        view.findViewById(R.id.loadPicture).setOnClickListener(v ->
                readGallery());
        editEvent.setOnClickListener(v ->
        {
            editDescription.setVisibility(View.VISIBLE);
            updateButton.setVisibility(View.VISIBLE);
            textEventDescription.setVisibility(View.GONE);
            updateButton.setEnabled(true);

        });
        updateButton.setOnClickListener(v ->
        {
            updateButton.setEnabled(false);

            if (editDescription.getText().toString().length() != 0) {
                descriptionEvent = editDescription.getText().toString();
                updateEvent();
                editDescription.setVisibility(View.GONE);
                updateButton.setVisibility(View.GONE);
                textEventDescription.setVisibility(View.VISIBLE);
                textEventDescription.setText(descriptionEvent);

            }
        });
        delete_button.setOnClickListener(v -> {

            AlertDialog.Builder dialogDelete = new AlertDialog.Builder(getContext());
            dialogDelete.setTitle(getResources().getString(R.string.are_sure));
            dialogDelete.setMessage(getResources().getString(R.string.confirm_delete_event));
            dialogDelete.setCancelable(false);
            dialogDelete.setPositiveButton(getResources().getString(R.string.confirm), (dialogInterface, i) -> {
                deleteEvent();
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.delete_event), Toast.LENGTH_LONG).show();
            });
            dialogDelete.setNegativeButton(getResources().getString(R.string.discard), (dialogInterface, i) -> {
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
        Response.ErrorListener errorListener = error -> errorTreatment(error.networkResponse.statusCode);

        apiCommunicator.deleteRequest(getActivity().getApplicationContext(), URL +'/'+ id, responseListener, errorListener, null);

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



    private void updateEvent() {
        HashMap<String, String> params = new HashMap<>();
        params.put("description", descriptionEvent);
        params.put("image", imageView!=null ? ImageCompressor.INSTANCE.compressAndEncodeAsBase64(
                ((BitmapDrawable)imageView.getDrawable()).getBitmap())
                : "");
        putCredentials(params);
    }

    private void putCredentials(HashMap<String, String> params) {
        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = response -> Toast.makeText(getActivity().getApplicationContext(), R.string.EventUpdated, Toast.LENGTH_LONG).show();
        Response.ErrorListener errorListener = error -> errorTreatment(error.networkResponse.statusCode);


        apiCommunicator.putRequest(getActivity().getApplicationContext(), URL + '/' + id, responseListener, errorListener, params);
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
                data.getData();
                Uri selectedImage = data.getData();
                Log.v(TAG, "Selected image uri" + selectedImage);
                Log.v(TAG, String.valueOf(selectedImage));
                loadImageFromUri(selectedImage);
                updateEvent();
            }
        }
        else{
            Log.v("Result","Something happened when tried to get the image");
        }
    }

    private void loadImageFromUri(Uri imageUri) {
        try {
            imageView.setImageBitmap(MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


