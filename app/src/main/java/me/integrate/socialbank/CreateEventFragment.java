package me.integrate.socialbank;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;
import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

public class CreateEventFragment extends Fragment {
    //TODO: figure url out
    private static final String URL = "/event";

    private ImageButton buttonImg;
    private Button buttonAsk;
    private Button buttonOffer;
    private Button buttonYesFixed;
    private Button buttonNoFixed;
    private Button buttonYesGroup;
    private Button buttonNoGroup;
    private Button buttonCreate;
    private EditText name;
    private EditText address;
    private EditText editTextStartDate;
    private EditText editTextEndDate;
    private EditText editTextStartHour;
    private EditText editTextEndHour;
    private EditText description;
    private LinearLayout layoutDate;
    private TableLayout groupTable;
    private TableRow capacityRow;
    private int eventType;
    private int eventFixed;
    private int eventGroup;

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int MY_PERMISSIONS_REQUEST_MANAGE_DOCUMENTS = 2;
    private boolean canWeRead;
    private boolean thereisPic;
    private String strStartDate;
    private String strEndDate;
    private Date dateStart;

    private Uri uriImg;

    private void postCredentials(HashMap<String, String> params) {
        //TODO: All, look for API's manual
        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = new Response.Listener<CustomRequest.CustomResponse>() {
            @Override
            public void onResponse(CustomRequest.CustomResponse response) {
                Toast.makeText(getActivity().getApplicationContext(), "Esdeveniment creat", Toast.LENGTH_LONG).show();
                eventsSelected();
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String message;
                int errorCode = error.networkResponse.statusCode;
                if (errorCode == 401)
                    message = "Unauthorized";
                else if(errorCode == 403)
                    message = "Forbidden";
                else if(errorCode == 404)
                    message = "Not Found";
                else
                    message = "Unexpected error";
                Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        };


        apiCommunicator.postRequest(getActivity().getApplicationContext(), URL, responseListener, errorListener, params);
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
        buttonCreate = (Button) rootView.findViewById(R.id.buttonCreate);

        name = (EditText) rootView.findViewById(R.id.editTextName);
        address = (EditText) rootView.findViewById(R.id.editTextAddress);
        editTextStartDate = (EditText) rootView.findViewById(R.id.editTextStartDate);
        editTextEndDate = (EditText) rootView.findViewById(R.id.editTextEndDate);
        editTextStartHour = (EditText) rootView.findViewById(R.id.editTextStartHour);
        editTextEndHour = (EditText) rootView.findViewById(R.id.editTextEndHour);
        description = (EditText) rootView.findViewById(R.id.editTextDescription);

        layoutDate = (LinearLayout) rootView.findViewById(R.id.layoutDate);

        groupTable = (TableLayout) rootView.findViewById(R.id.groupTable);
        capacityRow = (TableRow) rootView.findViewById(R.id.capacityRow);

        thereisPic = false;
        canWeRead = checkPermissions();

        eventType = -1;
        eventFixed = -1;

        if( !isVerified() )
            groupTable.setVisibility(View.GONE);

        //Fixed schedule disabled by default
        buttonNoFixed.setTextColor(getResources().getColor(R.color.colorPrimary));
        layoutDate.setVisibility(View.GONE);
        eventFixed = 0;

        //Group Service disabled by default
        buttonNoGroup.setTextColor(getResources().getColor(R.color.colorPrimary));
        capacityRow.setVisibility(View.GONE);
        eventGroup = 0;

        editTextEndDate.setEnabled(false);
        buttonCreate.setEnabled(false);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.buttonImg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readGallery();
                enableButton();
            }
        });
        view.findViewById(R.id.buttonCreate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> params = new HashMap<>();
                if( areFilled_msg() ) { //Not required, theorically, just in case sth fails
                    //TODO: match params with API (look for API's manual)

                    params.put("description", description.getText().toString());
                    params.put("endDate", strEndDate);
                    params.put("iniDate", strStartDate);
                    params.put("startHour", editTextStartHour.getText().toString());
                    params.put("endHour", editTextEndHour.getText().toString());

                    params.put("location", address.getText().toString());
                    params.put("title", name.getText().toString());
                    //params.put("img", convertUriToBase64(uriImg));

                    postCredentials(params);
                }
            }
        });
        view.findViewById(R.id.buttonAsk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonAsk.setTextColor(getResources().getColor(R.color.colorPrimary));
                buttonOffer.setTextColor(getResources().getColor(R.color.colorTextButton));
                eventType = 1;
                enableButton();
            }
        });
        view.findViewById(R.id.buttonOffer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonOffer.setTextColor(getResources().getColor(R.color.colorPrimary));
                buttonAsk.setTextColor(getResources().getColor(R.color.colorTextButton));
                eventType = 0;
                enableButton();
            }
        });
        view.findViewById(R.id.buttonYesFixed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonYesFixed.setTextColor(getResources().getColor(R.color.colorPrimary));
                buttonNoFixed.setTextColor(getResources().getColor(R.color.colorTextButton));
                layoutDate.setVisibility(View.VISIBLE);
                eventFixed = 1;
                enableButton();
            }
        });
        view.findViewById(R.id.buttonNoFixed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonNoFixed.setTextColor(getResources().getColor(R.color.colorPrimary));
                buttonYesFixed.setTextColor(getResources().getColor(R.color.colorTextButton));
                layoutDate.setVisibility(View.GONE);
                eventFixed = 0;
                enableButton();
            }
        });
        view.findViewById(R.id.buttonYesGroup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonYesGroup.setTextColor(getResources().getColor(R.color.colorPrimary));
                buttonNoGroup.setTextColor(getResources().getColor(R.color.colorTextButton));
                capacityRow.setVisibility(View.VISIBLE);
                eventGroup = 1;
                enableButton();
            }
        });
        view.findViewById(R.id.buttonNoGroup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonNoGroup.setTextColor(getResources().getColor(R.color.colorPrimary));
                buttonYesGroup.setTextColor(getResources().getColor(R.color.colorTextButton));
                capacityRow.setVisibility(View.GONE);
                eventGroup = 0;
                enableButton();
            }
        });
        view.findViewById(R.id.editTextStartDate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showStartDatePickerDialog();
                editTextEndDate.setEnabled(true);
            }
        });
        view.findViewById(R.id.editTextEndDate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEndDatePickerDialog();
                enableButton();
            }
        });
        name.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                enableButton();
            }
        });
        address.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                enableButton();
            }
        });
        description.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                enableButton();
            }
        });
        editTextStartHour.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                enableButton();
            }
        });
        editTextEndHour.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                enableButton();
            }
        });

    }

    public boolean isVerified() {
        //TODO: funcio que comprova si l'usuari es entitat verificada i retorna boolean
        return false;
    }

    private boolean areFilled_msg() {
        if( !thereisPic )
            Toast.makeText(getActivity(), "You must set an image", Toast.LENGTH_SHORT).show();
        else if( name.getText().toString().isEmpty()
                || address.getText().toString().isEmpty()
                || description.getText().toString().isEmpty())
            Toast.makeText(getActivity(), "Fill all fields", Toast.LENGTH_SHORT).show();
        else if( eventFixed == -1 )
            Toast.makeText(getActivity(), "Set schedule used", Toast.LENGTH_SHORT).show();
        else if( eventType == -1 )
            Toast.makeText(getActivity(), "Set event type", Toast.LENGTH_SHORT).show();
        else return true;
        return false;
    }

    private boolean areFilled() {
        return ((thereisPic) && !(name.getText().toString().isEmpty()) && !(address.getText().toString().isEmpty()) &&
                !(description.getText().toString().isEmpty()) && !(eventFixed == -1) && !(eventType == -1)
                && (eventFixed == 0 || (eventFixed == 1 && !editTextEndDate.getText().toString().isEmpty()
                && !editTextStartDate.getText().toString().isEmpty()
                && !editTextEndHour.getText().toString().isEmpty()
                && !editTextStartHour.getText().toString().isEmpty() ) ));
    }

    private void enableButton() {
        buttonCreate.setEnabled( areFilled() );
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
            uriImg = imageUri;
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

    private void showStartDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because january is zero
                final String selectedDate = day + " / " + (month + 1) + " / " + year;

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);

                dateStart = calendar.getTime();

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                strStartDate = format.format(calendar.getTime());
                editTextStartDate.setText(selectedDate);
                enableButton();
            }
        });
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    private void showEndDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because january is zero
                final String selectedDate = day + " / " + (month + 1) + " / " + year;

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);

                if( calendar.getTime().after(dateStart) ) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    strEndDate = format.format(calendar.getTime());
                    editTextEndDate.setText(selectedDate);
                    enableButton();
                } else
                    Toast.makeText(getActivity(), "End date must be greater than Start date", Toast.LENGTH_SHORT).show();
            }
        });
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }
}
