package me.integrate.socialbank;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

public class CreateEventFragment extends Fragment {

    private static final String URL = "/recover";
    private ImageButton buttonImg;
    private Button buttonAsk;
    private Button buttonOffer;
    private Button buttonYesFixed;
    private Button buttonNoFixed;
    private Button buttonYesGroup;
    private Button buttonNoGroup;
    private EditText name;
    private EditText address;
    private EditText description;
    private TableLayout groupTable;
    private TableRow capacityRow;
    private int eventType;
    private int eventFixed;
    private int eventGroup;

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int MY_PERMISSIONS_REQUEST_MANAGE_DOCUMENTS = 2;
    private boolean canWeRead;
    private boolean thereisPic;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_create_event, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_createEvent) {
            if( areFilled() )
                //TODO: fer be
                postCredentials("asd","gsdf");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void postCredentials(String newPassword, String token) {
        //TODO: tot, canviar camps dentrada
        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = new Response.Listener<CustomRequest.CustomResponse>() {
            @Override
            public void onResponse(CustomRequest.CustomResponse response) {
                Toast.makeText(getActivity().getApplicationContext(), "holi", Toast.LENGTH_LONG).show();
                eventsSelected();
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String message;
                int errorCode = error.networkResponse.statusCode;
                if (errorCode >= 500  &&  errorCode <= 511)
                    message = "Server error";
                else if(errorCode == 400)
                    message = "Bad request";
                else if(errorCode == 404)
                    message = "Wrong token";
                else
                    message = "Unexpected error";
                Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        };

        apiCommunicator.putRequest(getActivity().getApplicationContext(), URL.concat("/".concat(token)), responseListener, errorListener, newPassword);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_create_event, container, false);

        buttonImg = (ImageButton) rootView.findViewById(R.id.buttonImg);
        buttonAsk = (Button) rootView.findViewById(R.id.buttonAsk);
        buttonOffer = (Button) rootView.findViewById(R.id.buttonOffer);
        buttonYesFixed = (Button) rootView.findViewById(R.id.buttonYesFixed);
        buttonNoFixed = (Button) rootView.findViewById(R.id.buttonNoFixed);
        buttonYesGroup = (Button) rootView.findViewById(R.id.buttonYesGroup);
        buttonNoGroup = (Button) rootView.findViewById(R.id.buttonNoGroup);

        name = (EditText) rootView.findViewById(R.id.editTextName);
        address = (EditText) rootView.findViewById(R.id.editTextAddress);
        description = (EditText) rootView.findViewById(R.id.editTextDescription);

        groupTable = (TableLayout) rootView.findViewById(R.id.groupTable);
        capacityRow = (TableRow) rootView.findViewById(R.id.capacityRow);

        thereisPic = false;
        canWeRead = checkPermissions();

        eventType = -1;
        eventFixed = -1;

        if( isVerified() )
            groupTable.setVisibility(View.GONE);

        //Group Service disabled by default
        buttonNoGroup.setTextColor(getResources().getColor(R.color.colorPrimary));
        capacityRow.setVisibility(View.GONE);
        eventGroup = 0;

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.buttonImg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readGallery();
            }
        });
        view.findViewById(R.id.buttonAsk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonAsk.setTextColor(getResources().getColor(R.color.colorPrimary));
                buttonOffer.setTextColor(getResources().getColor(R.color.colorTextButton));
                eventType = 1;
            }
        });
        view.findViewById(R.id.buttonOffer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonOffer.setTextColor(getResources().getColor(R.color.colorPrimary));
                buttonAsk.setTextColor(getResources().getColor(R.color.colorTextButton));
                eventType = 0;
            }
        });
        view.findViewById(R.id.buttonYesFixed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonYesFixed.setTextColor(getResources().getColor(R.color.colorPrimary));
                buttonNoFixed.setTextColor(getResources().getColor(R.color.colorTextButton));
                eventFixed = 1;
            }
        });
        view.findViewById(R.id.buttonNoFixed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonNoFixed.setTextColor(getResources().getColor(R.color.colorPrimary));
                buttonYesFixed.setTextColor(getResources().getColor(R.color.colorTextButton));
                eventFixed = 0;
            }
        });
        view.findViewById(R.id.buttonYesGroup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonYesGroup.setTextColor(getResources().getColor(R.color.colorPrimary));
                buttonNoGroup.setTextColor(getResources().getColor(R.color.colorTextButton));
                capacityRow.setVisibility(View.VISIBLE);
                eventFixed = 1;
            }
        });
        view.findViewById(R.id.buttonNoGroup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonNoGroup.setTextColor(getResources().getColor(R.color.colorPrimary));
                buttonYesGroup.setTextColor(getResources().getColor(R.color.colorTextButton));
                capacityRow.setVisibility(View.GONE);
                eventGroup = 0;

            }
        });

    }

    public boolean isVerified() {
        //TODO: funcio que comprova si l'usuari es entitat verificada i retorna boolean
        return false;
    }

    private boolean areFilled() {
        if( !thereisPic )
            Toast.makeText(getActivity(), "You must set an image", Toast.LENGTH_SHORT).show();
        else if( !name.getText().toString().isEmpty()
                && !address.getText().toString().isEmpty()
                && !description.getText().toString().isEmpty())
            Toast.makeText(getActivity(), "Fill all fields", Toast.LENGTH_SHORT).show();
        else if( eventFixed == -1 )
            Toast.makeText(getActivity(), "Set schedule used", Toast.LENGTH_SHORT).show();
        else if( eventType == -1 )
            Toast.makeText(getActivity(), "Set event type", Toast.LENGTH_SHORT).show();
        else return true;
        return false;
    }

    private void readGallery() {
        PermissionChecker.checkReadExternalStoragePermissions(getActivity(),MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
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
                if(canWeRead && requestCode == 2){
                    Log.v(TAG, "Selected image uri" + selectedImage);
                }
                Log.v(TAG, String.valueOf(selectedImage));
                loadImageFromUri(selectedImage);
            }
        }
        else{
            Log.v("Result","Something happened when tried to get the image");
        }
    }

    private void loadImageFromUri(Uri imageUri) {
        try {
            buttonImg.setImageBitmap(MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String convertUriToBase64(Uri imageUri) {
        String imagePath = imageUri.toString();
        Bitmap bm = BitmapFactory.decodeFile(imagePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] byteArrayImage = baos.toByteArray();
        String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
        return encodedImage;
    }

    private boolean checkPermissions() {
        PermissionChecker.checkReadExternalStoragePermissions(getActivity(), MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        return ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_DENIED;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    canWeRead = true; //Permission granted
                else
                    canWeRead = false; //Permission denied
                return;
            }
            case  MY_PERMISSIONS_REQUEST_MANAGE_DOCUMENTS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    canWeRead = true; //Permission granted
                else
                    canWeRead = false; //Permission denied
                return;
            }
        }
    }

    private void eventsSelected() {
        //TODO: Choose the proper Fragment (not created yet
        Fragment eventsFragment = new LoginFragment();
        FragmentChangeListener fc = (FragmentChangeListener) getActivity();
        fc.replaceFragment(eventsFragment);
    }
}
