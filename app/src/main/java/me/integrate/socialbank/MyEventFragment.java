package me.integrate.socialbank;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import java.io.IOException;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;


public class MyEventFragment extends EventFragment {

    private Button updateButton;
    private boolean isFABOpen;
    private TextView editEventText;
    private TextView changeEventPhotoText;
    private TextView deleteEventText;
    FloatingActionButton editEvent;
    FloatingActionButton deleteEvent;
    FloatingActionButton changeEventPhoto;
    FloatingActionButton openMenu;


    private static final String URL = "/events";

    public static MyEventFragment newInstance(Bundle params) {
        MyEventFragment myEventFragment = new MyEventFragment();
        myEventFragment.setArguments(params);
        return myEventFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        updateButton = (Button) view.findViewById(R.id.buttonUpdate);
        editEvent = (FloatingActionButton) view.findViewById(R.id.editEvent);
        changeEventPhoto = (FloatingActionButton) view.findViewById(R.id.loadPicture);
        deleteEvent = (FloatingActionButton) view.findViewById(R.id.deleteEvent);
        editEventText = (TextView) view.findViewById(R.id.editEventText);
        changeEventPhotoText = (TextView) view.findViewById(R.id.changeEventPhotoText);
        deleteEventText = (TextView) view.findViewById(R.id.deleteEventText);

        openMenu = (FloatingActionButton) view.findViewById(R.id.openMenu);

        openMenu.setVisibility(View.VISIBLE);
        editEvent.setVisibility(View.VISIBLE);
        deleteEvent.setVisibility(View.VISIBLE);
        changeEventPhoto.setVisibility(View.VISIBLE);

        id = getArguments().getInt("id");
        isFABOpen = false;
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.loadPicture).setOnClickListener(v -> {
            closeFABMenu();
            readGallery();

        });
        editEvent.setOnClickListener(v ->
        {
            editDescription.setVisibility(View.VISIBLE);
            updateButton.setVisibility(View.VISIBLE);
            textEventDescription.setVisibility(View.GONE);
            updateButton.setEnabled(true);
            closeFABMenu();

        });
        view.findViewById(R.id.openMenu).setOnClickListener(view1 -> {
            if (!isFABOpen) {
                showFABMenu();
            } else {
                closeFABMenu();
            }
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
        deleteEvent.setOnClickListener(v -> {
            closeFABMenu();
            AlertDialog.Builder dialogDelete = new AlertDialog.Builder(getContext());
            dialogDelete.setTitle(getResources().getString(R.string.are_sure));
            dialogDelete.setMessage(getResources().getString(R.string.confirm_delete_event));
            dialogDelete.setCancelable(false);
            dialogDelete.setPositiveButton(getResources().getString(R.string.confirm), (dialogInterface, i) -> {
                deleteEvent();
            });
            dialogDelete.setNegativeButton(getResources().getString(R.string.discard), (dialogInterface, i) -> {
            });
            dialogDelete.show();
        });
    }

    private void showFABMenu() {
        isFABOpen = true;
        editEventText.bringToFront();
        editEventText.setVisibility(View.VISIBLE);
        editEventText.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        editEvent.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        changeEventPhotoText.bringToFront();
        changeEventPhotoText.setVisibility(View.VISIBLE);
        changeEventPhotoText.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
        changeEventPhoto.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
        deleteEventText.bringToFront();
        deleteEventText.setVisibility(View.VISIBLE);
        deleteEventText.animate().translationY(-getResources().getDimension(R.dimen.standard_155));
        deleteEvent.animate().translationY(-getResources().getDimension(R.dimen.standard_155));
        openMenu.animate().rotation(45).setInterpolator(AnimationUtils.loadInterpolator(getContext(), android.R.interpolator.fast_out_slow_in)).start();
    }

    private void closeFABMenu() {
        isFABOpen = false;
        editEvent.animate().translationY(0);
        editEventText.animate().translationY(0);
        editEventText.setVisibility(View.GONE);
        changeEventPhoto.animate().translationY(0);
        changeEventPhotoText.animate().translationY(0);
        changeEventPhotoText.setVisibility(View.GONE);
        deleteEvent.animate().translationY(0);
        deleteEventText.animate().translationY(0);
        deleteEventText.setVisibility(View.GONE);
        openMenu.animate().rotation(0).setInterpolator(AnimationUtils.loadInterpolator(getContext(), android.R.interpolator.fast_out_slow_in)).start();

    }

    //Call API for delete an event
    void deleteEvent() {
        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = (Response.Listener<CustomRequest.CustomResponse>) response -> {
            if (getArguments().getBoolean("MyProfile")) profileSelected();
            else boardSelected();
            Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.delete_event), Toast.LENGTH_LONG).show();
        };
        Response.ErrorListener errorListener = error -> errorTreatment(error.networkResponse.statusCode);

        apiCommunicator.deleteRequest(getActivity().getApplicationContext(), URL + '/' + id, responseListener, errorListener, null);

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
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        params.put("image", bitmap != null ? ImageCompressor.INSTANCE.compressAndEncodeAsBase64(
                bitmap) : "");
        putCredentials(params);
    }

    private void putCredentials(HashMap<String, String> params) {
        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = response -> Toast.makeText(getActivity().getApplicationContext(), R.string.eventUpdated, Toast.LENGTH_LONG).show();
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

        if (resultCode == RESULT_OK) {
            if (requestCode == 2) {
                data.getData();
                Uri selectedImage = data.getData();
                Log.v(TAG, "Selected image uri" + selectedImage);
                Log.v(TAG, String.valueOf(selectedImage));
                loadImageFromUri(selectedImage);
                updateEvent();
            }
        } else {
            Log.v("Result", "Something happened when tried to get the image");
        }
    }

    private void loadImageFromUri(Uri imageUri) {
        try {
            imageView.setImageBitmap(getImage(MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


