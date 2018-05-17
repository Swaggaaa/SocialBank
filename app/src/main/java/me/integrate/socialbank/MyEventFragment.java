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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        editDescription = (EditText) view.findViewById(R.id.editDescription);
        updateButton = (Button) view.findViewById(R.id.buttonUpdate);
        FloatingActionButton editEvent = (FloatingActionButton) view.findViewById(R.id.editEvent);
        editEvent.setVisibility(View.VISIBLE);
        ImageView changeUserPhoto = (ImageView) view.findViewById(R.id.loadPicture);
        changeUserPhoto.setVisibility(View.VISIBLE);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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


