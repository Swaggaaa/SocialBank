package me.integrate.socialbank;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
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
    private static final String URL = "/events";

    private ImageView imageView;

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

                Toast.makeText(getActivity(), "ERROR", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity().getApplicationContext(), String.valueOf(errorCode), Toast.LENGTH_LONG).show();
            }
        };

        apiCommunicator.postRequest(getActivity().getApplicationContext(), URL, responseListener, errorListener, params);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_create_event, container, false);

        imageView = (ImageView) rootView.findViewById(R.id.imageView);

        //buttonImg = (ImageButton) rootView.findViewById(R.id.buttonImg);
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
        /*view.findViewById(R.id.buttonImg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readGallery();
                enableButton();
            }
        });*/
        view.findViewById(R.id.imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readGallery();
                enableButton();
            }
        });
        view.findViewById(R.id.buttonCreate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "button create", Toast.LENGTH_SHORT).show();

                HashMap<String, String> params = new HashMap<>();
                //TODO: Set to 00
                String dataIni = "2018-04-26T15:10:02Z";
                String dataEnd = "2018-04-26T15:10:02Z";

                //if( areFilled_msg() ) { //Not required, theorically, just in case sth fails
                //TODO: Still not working
                String emailUser = SharedPreferencesManager.INSTANCE.read(getActivity(),"user_email");

                Toast.makeText(getActivity(), "email " + emailUser , Toast.LENGTH_SHORT).show();

                params.put("creatorEmail", emailUser);
                params.put("description", description.getText().toString());
                if(eventFixed == 1) {
                    dataIni = strEndDate.concat(" ").concat(editTextEndHour.getText().toString()).concat(":00:00");
                    dataEnd = strStartDate.concat(" ").concat(editTextStartHour.getText().toString()).concat(":00:00");
                }
                params.put("endDate", dataEnd);
                params.put("iniDate", dataIni);
                params.put("location", address.getText().toString());
                params.put("title", name.getText().toString());
                params.put("image", "");

                postCredentials(params);
                //}
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
        view.findViewById(R.id.editTextStartHour).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b) {
                    editTextStartHour.setText("");
                }
                else if(editTextStartHour.getText().length()==1) {
                        String hora = editTextStartHour.getText().toString();
                    editTextStartHour.setText("0".concat(hora));
                }
            }
        });
        editTextStartHour.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(editTextStartHour.getText().length()>0) {
                    int num = Integer.parseInt(editTextStartHour.getText().toString());
                    if (!(num>=0 && num<=23)) {
                        Toast.makeText(getActivity(),"Please enter the code in the range of 0-23",Toast.LENGTH_SHORT).show();
                        editTextStartHour.setText("");
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                enableButton();
            }
        });
        view.findViewById(R.id.editTextEndHour).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b) {
                editTextEndHour.setText("");
                }
                else if(editTextEndHour.getText().length()==1) {
                        String hora = editTextStartHour.getText().toString();
                    editTextEndHour.setText("0".concat(hora));
                }
            }
        });
        editTextEndHour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(editTextEndHour.getText().length()>0) {
                    int num = Integer.parseInt(editTextEndHour.getText().toString());
                    if (!(num>=0 && num<=23)) {
                        Toast.makeText(getActivity(),"Please enter the code in the range of 0-23",Toast.LENGTH_SHORT).show();
                        editTextEndHour.setText("");
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(editTextEndHour.getText().length()==1) {
                    String hora = editTextEndHour.getText().toString();
                    editTextEndHour.setText("0".concat(hora));
                }
                enableButton();
            }
        });

    }

    public boolean isVerified() {
        //TODO: API function still not implemented
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
            //buttonImg.setImageBitmap(MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri));
            imageView.setImageBitmap(MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri));
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
        //TODO: Choose the proper Fragment (not created yet)
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
