package me.integrate.socialbank;

import android.content.Intent;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import java.io.IOException;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

public class MyProfileFragment extends ProfileFragment {
    private static final String URL = "/users";
    private TextView userBalance;
    private boolean thereisPic;
    private boolean isFABOpen;
    private TextView editProfileText;
    private TextView changePictureText;
    private TextView userBalanceText;
    FloatingActionButton editProfile;
    FloatingActionButton changeUserPhoto;
    FloatingActionButton openMenu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        editProfile = (FloatingActionButton) view.findViewById(R.id.editProfile);
        changeUserPhoto = (FloatingActionButton) view.findViewById(R.id.loadPicture);
        userBalanceText = (TextView) view.findViewById(R.id.userBalanceText);
        editProfile.setVisibility(View.VISIBLE);
        changeUserPhoto.setVisibility(View.VISIBLE);
        userBalance = (TextView) view.findViewById(R.id.hoursBalance);
        userBalance.setVisibility(View.VISIBLE);
        userBalanceText.setVisibility(View.VISIBLE);
        reportUserButton.setVisibility(View.GONE);
        editProfileText = (TextView) view.findViewById(R.id.editProfileText);
        changePictureText = (TextView) view.findViewById(R.id.changePasswordText);
        isFABOpen = false;
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
            Fragment boardFragment = new EditProfileFragment();
            FragmentChangeListener fc = (FragmentChangeListener) getActivity();
            fc.replaceFragment(boardFragment);
        });
        view.findViewById(R.id.openMenu).setOnClickListener(view1 -> {
            if (!isFABOpen) {
                showFABMenu();
            } else {
                closeFABMenu();
            }
        });
    }

    private void showFABMenu() {
        isFABOpen = true;
        editProfileText.bringToFront();
        editProfileText.setVisibility(View.VISIBLE);
        editProfileText.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        editProfile.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        changePictureText.bringToFront();
        changePictureText.setVisibility(View.VISIBLE);
        changePictureText.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
        changeUserPhoto.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
    }

    private void closeFABMenu() {
        isFABOpen = false;
        editProfile.animate().translationY(0);
        changeUserPhoto.animate().translationY(0);
        editProfileText.animate().translationY(0);
        editProfileText.setVisibility(View.GONE);
        changePictureText.animate().translationY(0);
        changePictureText.setVisibility(View.GONE);
    }

    private void updateProfile() {
        HashMap<String, String> params = new HashMap<>();
        params.put("name", nameUser);
        params.put("surname", lastNameUser);
        params.put("birthdate", dateUser);
        params.put("gender", genderUser);
        params.put("description", descriptionUser);
        params.put("email", emailUser);
        params.put("image", thereisPic ? ImageCompressor.INSTANCE.compressAndEncodeAsBase64(
                ((BitmapDrawable) userPicture.getDrawable()).getBitmap())
                : "");

        putCredentials(params);
    }

    private void putCredentials(HashMap<String, String> params) {
        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = response -> {
            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.image_update), Toast.LENGTH_LONG).show();

        };
        Response.ErrorListener errorListener = error -> Toast.makeText(getActivity().getApplicationContext(), getString(R.string.something_wrong), Toast.LENGTH_LONG).show();


        apiCommunicator.putRequest(getActivity().getApplicationContext(), URL + '/' + emailUser, responseListener, errorListener, params);
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
                thereisPic = true;
                data.getData();
                Uri selectedImage = data.getData();
                Log.v(TAG, "Selected image uri" + selectedImage);
                Log.v(TAG, String.valueOf(selectedImage));
                loadImageFromUri(selectedImage);
                updateProfile();
            }
        } else {
            Log.v("Result", "Something happened when tried to get the image");
        }
    }

    private void loadImageFromUri(Uri imageUri) {
        try {
            userPicture.setImageBitmap(getImageRounded(MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
