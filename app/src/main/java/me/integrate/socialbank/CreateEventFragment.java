package me.integrate.socialbank;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

public class CreateEventFragment extends Fragment {
    private static final String URL = "/events";

    private ImageView imageView;

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
    private EditText editTextCapacity;
    private LinearLayout layoutDate;
    private TableRow capacityRow;
    private Boolean demand;
    private Boolean eventFixed;
    private String capacity;
    private Spinner category;
    TableLayout groupTable;

    private boolean thereIsPic;
    private String verified;
    private String strStartDate;
    private String strEndDate;
    private Date dateStart;
    private Date dateEnd;
    private boolean sameDay;
    private Integer startHour;
    private Integer startMin;
    private Integer endHour;
    private Integer endMin;
    private Vector<String> categories;

    double userHours;

    private void iniVector() {
        categories = new Vector();
        categories.add("Other");
        categories.add("Gastronomy");
        categories.add("Language");
        categories.add("Workshops");
        categories.add("Culture");
        categories.add("Sports");
        categories.add("Leisue");

    }

    private void postEvent(HashMap<String, Object> params) {
        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = (Response.Listener<CustomRequest.CustomResponse>) response ->
        {
            Toast.makeText(getActivity().getApplicationContext(), R.string.event_created_msg, Toast.LENGTH_LONG).show();
            boardSelected();
        };
        Response.ErrorListener errorListener = error ->
        {
            buttonCreate.setText(R.string.create_event);
            buttonCreate.setEnabled(true);
            String message;
            int errorCode = error.networkResponse.statusCode;
            if (errorCode == 401)
                message = "Unauthorized";
            else if (errorCode == 403)
                message = "Forbidden";
            else if (errorCode == 404)
                message = "Not Found";
            else
                message = "Unexpected error";
            Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
        };

        apiCommunicator.postRequest(getActivity().getApplicationContext(), URL, responseListener, errorListener, params);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_create_event, container, false);

        imageView = (ImageView) rootView.findViewById(R.id.imageView);

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
        editTextCapacity = (EditText) rootView.findViewById(R.id.editTextCapacity);

        layoutDate = (LinearLayout) rootView.findViewById(R.id.layoutDate);

        groupTable = (TableLayout) rootView.findViewById(R.id.groupTable);
        capacityRow = (TableRow) rootView.findViewById(R.id.capacityRow);

        thereIsPic = false;
        sameDay = false;

        capacity = "1";

        iniVector();
        groupTable.setVisibility(View.GONE);
        getUserInfo();

        category = (Spinner) rootView.findViewById(R.id.editTextCategory);

        //Fixed schedule disabled by default
        buttonNoFixed.setTextColor(getResources().getColor(R.color.colorPrimary));
        layoutDate.setVisibility(View.GONE);
        eventFixed = false;

        //Group Service disabled by default
        buttonNoGroup.setTextColor(getResources().getColor(R.color.colorPrimary));
        capacityRow.setVisibility(View.GONE);

        editTextEndDate.setEnabled(false);
        buttonCreate.setEnabled(false);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.buttonCreate).setOnClickListener(view1 ->
        {
            if (demand && !enoughHours())
                Toast.makeText(getActivity(), getString(R.string.Not_hours_msg), Toast.LENGTH_SHORT).show();
            else
                jsonEvent();

         });
        view.findViewById(R.id.imageView).setOnClickListener(v ->
        {
            readGallery();
            enableButton();
        });
        view.findViewById(R.id.buttonAsk).setOnClickListener(view12 ->
        {
            buttonAsk.setTextColor(getResources().getColor(R.color.colorPrimary));
            buttonOffer.setTextColor(getResources().getColor(R.color.colorTextButton));
            demand = true;
            enableButton();
        });
        view.findViewById(R.id.buttonOffer).setOnClickListener(view13 ->
        {
            buttonOffer.setTextColor(getResources().getColor(R.color.colorPrimary));
            buttonAsk.setTextColor(getResources().getColor(R.color.colorTextButton));
            demand = false;
            enableButton();
        });
        view.findViewById(R.id.buttonYesFixed).setOnClickListener(view14 ->
        {
            buttonYesFixed.setTextColor(getResources().getColor(R.color.colorPrimary));
            buttonNoFixed.setTextColor(getResources().getColor(R.color.colorTextButton));
            layoutDate.setVisibility(View.VISIBLE);
            eventFixed = true;
            enableButton();
        });
        view.findViewById(R.id.buttonNoFixed).setOnClickListener(view15 ->
        {
            buttonNoFixed.setTextColor(getResources().getColor(R.color.colorPrimary));
            buttonYesFixed.setTextColor(getResources().getColor(R.color.colorTextButton));
            layoutDate.setVisibility(View.GONE);
            eventFixed = false;
            enableButton();
        });
        view.findViewById(R.id.buttonYesGroup).setOnClickListener(view16 ->
        {
            buttonYesGroup.setTextColor(getResources().getColor(R.color.colorPrimary));
            buttonNoGroup.setTextColor(getResources().getColor(R.color.colorTextButton));
            capacityRow.setVisibility(View.VISIBLE);
            enableButton();
        });
        view.findViewById(R.id.buttonNoGroup).setOnClickListener(view17 ->
        {
            buttonNoGroup.setTextColor(getResources().getColor(R.color.colorPrimary));
            buttonYesGroup.setTextColor(getResources().getColor(R.color.colorTextButton));
            capacityRow.setVisibility(View.GONE);
            enableButton();
        });
        view.findViewById(R.id.editTextStartDate).setOnClickListener(view18 ->
        {
            showStartDatePickerDialog();
            editTextEndDate.setEnabled(true);
        });
        view.findViewById(R.id.editTextEndDate).setOnClickListener(view19 ->
        {
            showEndDatePickerDialog();
            enableButton();
        });
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                enableButton();
            }
        });
        address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                enableButton();
            }
        });
        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                enableButton();
            }
        });
        editTextCapacity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                capacity = editTextCapacity.getText().toString();
            }
        });
        view.findViewById(R.id.editTextStartHour).setOnClickListener(view19 ->
        {
            showStartHourPickerDialog();
            enableButton();
        });
        view.findViewById(R.id.editTextEndHour).setOnClickListener(view19 ->
        {
            showEndHourPickerDialog();
            enableButton();
        });
    }

    private void jsonEvent() {
        HashMap<String, Object> params = new HashMap<>();
        String dataIni = null;
        String dataEnd = null;
        if (eventFixed) {
            dataIni = strStartDate.concat("T").concat(editTextStartHour.getText().toString()).concat(":00Z");
            dataEnd = strEndDate.concat("T").concat(editTextEndHour.getText().toString()).concat(":00Z");
        }
        EventLocation eventLocation = new EventLocation(address.getText().toString());

        params.put("category", categories.get(category.getSelectedItemPosition()).toUpperCase());
        params.put("creatorEmail", getUserEmail());
        params.put("demand", demand ? "true" : "false");
        params.put("description", description.getText().toString());
        params.put("endDate", dataEnd);
        params.put("iniDate", dataIni);
        params.put("location", eventLocation.getAddress());
        params.put("latitude", String.valueOf(eventLocation.getLatitude()));
        params.put("longitude", String.valueOf(eventLocation.getLongitude()));
        params.put("title", name.getText().toString());
        params.put("image", thereIsPic ? ImageCompressor.INSTANCE.compressAndEncodeAsBase64(
                ((BitmapDrawable) imageView.getDrawable()).getBitmap())
                : "");
        params.put("capacity", capacity);

        buttonCreate.setText(R.string.loading);
        buttonCreate.setEnabled(false);
        postEvent(params);
    }

    private String getUserEmail() {
        return SharedPreferencesManager.INSTANCE.read(getActivity(), "user_email");
    }

    private boolean areFilled() {
        return (/*(thereIsPic) && */!(name.getText().toString().isEmpty()) && !(address.getText().toString().isEmpty()) &&
                !(description.getText().toString().isEmpty()) && (eventFixed != null) && (demand != null)
                && (!eventFixed || (eventFixed && !editTextEndDate.getText().toString().isEmpty()
                && !editTextStartDate.getText().toString().isEmpty()
                && !editTextEndHour.getText().toString().isEmpty()
                && !editTextStartHour.getText().toString().isEmpty())));
    }

    private void enableButton() {
        buttonCreate.setEnabled(areFilled());
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
                thereIsPic = true;
                data.getData();
                Uri selectedImage = data.getData();
                Log.v(TAG, "Selected image uri" + selectedImage);
                Log.v(TAG, String.valueOf(selectedImage));
                loadImageFromUri(selectedImage);
            }
        } else {
            Log.v("Result", "Something happened when tried to get the image");
        }
    }

    private void loadImageFromUri(Uri imageUri) {
        try {
            imageView.setImageBitmap(MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void boardSelected() {
        Fragment boardFragment = new BoardFragment();
        FragmentChangeListener fc = (FragmentChangeListener) getActivity();
        fc.replaceFragment(boardFragment);
    }

    private void showStartDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance((datePicker, year, month, day) ->
        {
            // +1 because january is zero
            final String selectedDate = day + " / " + (month + 1) + " / " + year;

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);

            dateStart = calendar.getTime();

            if ((dateEnd == null || dateStart.before(dateEnd)) && dateStart.after(Calendar.getInstance().getTime())) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                strStartDate = format.format(calendar.getTime());
                editTextStartDate.setText(selectedDate);
                sameDay = editTextStartDate.getText().toString().contains(editTextEndDate.getText().toString());

                editTextEndHour.setText("");
                enableButton();
            } else
                Toast.makeText(getActivity(), R.string.Wrong_date_msg, Toast.LENGTH_SHORT).show();

        });
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    private void showEndDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance((datePicker, year, month, day) -> {
            // +1 because january is zero
            final String selectedDate = day + " / " + (month + 1) + " / " + year;

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);

            dateEnd = calendar.getTime();

            if (dateEnd.after(dateStart)) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                strEndDate = format.format(dateEnd);
                editTextEndDate.setText(selectedDate);
                sameDay = editTextStartDate.getText().toString().contains(editTextEndDate.getText().toString());
                if (!rightHour()) {
                    Toast.makeText(getActivity(), R.string.Pick_hour_msg, Toast.LENGTH_SHORT).show();
                    editTextStartHour.setText("");
                    editTextEndHour.setText("");
                }
                enableButton();
            } else
                Toast.makeText(CreateEventFragment.this.getActivity(), R.string.endDateBigger, Toast.LENGTH_SHORT).show();
        });
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    private void showStartHourPickerDialog() {
        TimePickerFragment newFragment = TimePickerFragment.newInstance((timePicker, h, m) -> {
            startHour = h;
            startMin = m;
            if (!sameDay || rightHour())
                editTextStartHour.setText(getFullHour(h, m));
            else {
                Toast.makeText(getActivity(), R.string.Pick_hour_msg, Toast.LENGTH_SHORT).show();
                editTextStartHour.getText().clear();
                startHour = startMin = null;
            }

        });
        newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
    }

    private void showEndHourPickerDialog() {
        TimePickerFragment newFragment = TimePickerFragment.newInstance((timePicker, h, m) -> {
            endHour = h;
            endMin = m;
            if (!sameDay || rightHour())
                editTextEndHour.setText(getFullHour(h, m));
            else {
                Toast.makeText(getActivity(), R.string.Pick_hour_msg, Toast.LENGTH_SHORT).show();
                editTextEndHour.getText().clear();
                endHour = endMin = -1;
            }
        });
        newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
    }

    private boolean rightHour() {
        return (((startHour == null && startMin == null) || (endHour == null && endMin == null)) || ((startHour < endHour) || (startHour == endHour && startMin < endMin)));
    }

    private String getFullHour(int h, int m) {
        // Add 0 if number has less than 2 digits and build hour in a proper format "hh:mm"
        String hour, minute;
        if (h < 10) hour = "0" + String.valueOf(h);
        else hour = String.valueOf(h);
        if (m < 10) minute = "0" + String.valueOf(m);
        else minute = String.valueOf(m);

        return hour + ":" + minute;
    }

    private void getUserInfo() {
        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = (Response.Listener<CustomRequest.CustomResponse>) response -> {
            try {
                JSONObject jsonObject = new JSONObject(response.response);
                String balance = jsonObject.get("balance").toString();
                verified = jsonObject.get("verified").toString();
                userHours = Double.valueOf(balance);

                if (verified.equals("true"))
                    groupTable.setVisibility(View.VISIBLE);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        };
        Response.ErrorListener errorListener = error -> {
            String message;
            int errorCode = error.networkResponse.statusCode;
            if (errorCode == 401)
                message = "Unauthorized";
            else if (errorCode == 403)
                message = "Forbidden";
            else if (errorCode == 404)
                message = "Not Found";
            else
                message = "Unexpected error";
            Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
        };
        apiCommunicator.getRequest(getActivity().getApplicationContext(), "/users/" + getUserEmail(), responseListener, errorListener, null);

    }

    private double getEventHours() { //Without rounding
        double hours = -1;
        if (startHour != null && endHour != null && dateStart != null && dateEnd != null) {
            hours = (dateEnd.getTime() - dateStart.getTime()) / 3600000;
            hours += ((endHour + (endMin * 0.01)) - (startHour + (startMin * 0.01)));
        }
        return hours;
    }

    private boolean enoughHours() {
        return userHours >= getEventHours();
    }
}
