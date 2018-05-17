package me.integrate.socialbank;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ProfileFragment extends Fragment {
    private static final String URL = "/users";
    ImageView userPicture;
    private TextView userName;
    private TextView userEmailToShow;
    private TextView userBalance;
    private TextView userDescription;
    private TextView myEvents;
    String emailUser;
    String nameUser;
    String lastNameUser;
    String dateUser;
    String genderUser;
    String descriptionUser;
    Button reportUser;
    Button confirmReport;
    Button discardReport;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    private ProgressDialog loadingDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_see_my_profile, container, false);
        userPicture = (ImageView) rootView.findViewById(R.id.myProfileImage);
        userName = (TextView) rootView.findViewById(R.id.myProfileName);
        userEmailToShow = (TextView) rootView.findViewById(R.id.userEmailToShow);
        userBalance = (TextView) rootView.findViewById(R.id.hoursBalance);
        userDescription = (TextView) rootView.findViewById(R.id.aboutMe);
        myEvents = (TextView)rootView.findViewById(R.id.events);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view_user_profile);
        reportUser = (Button)rootView.findViewById(R.id.buttonReportUser);
        confirmReport = (Button)rootView.findViewById(R.id.confirmReport);
        confirmReport.setVisibility(View.GONE);
        discardReport = (Button)rootView.findViewById(R.id.discardReport);
        discardReport.setVisibility(View.GONE);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        loadingDialog = ProgressDialog.show(getActivity(), "",
                getString(R.string.loadingMessage), true);
        fillFields();
        getUserEvents();
        return rootView;
    }


    private void fillFields() {
        Bundle b = this.getArguments();
        if(b != null){
            emailUser = b.getString("email");
        }
        else {
            emailUser = SharedPreferencesManager.INSTANCE.read(getActivity(),"user_email");
        }
        getUserInfo(emailUser);
    }

    private void getUserInfo(String emailUser) {
        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = (Response.Listener<CustomRequest.CustomResponse>) response -> {
            JSONObject jsonObject;
            Float balance = null;
            try{
                jsonObject = new JSONObject(response.response);
                nameUser = jsonObject.getString("name");
                String events = getString(R.string.personal_events);
                myEvents.setText(nameUser+events);
                lastNameUser = jsonObject.getString("surname");
                dateUser = jsonObject.getString("birthdate");
                genderUser = jsonObject.getString("gender");
                descriptionUser = jsonObject.getString("description");
                String completeName = nameUser + " " + lastNameUser;
                userName.setText(completeName);
                balance = BigDecimal.valueOf(jsonObject.getDouble("balance")).floatValue();
                userBalance.setText(balance.toString());
                userEmailToShow.setText(jsonObject.getString("email"));
                userDescription.setText(descriptionUser);
                String image = jsonObject.getString("image");
                if (!image.equals("")) {
                    byte[] decodeString = Base64.decode(image, Base64.DEFAULT);
                    userPicture.setImageBitmap(BitmapFactory.decodeByteArray(
                            decodeString, 0, decodeString.length));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (balance < 0) userBalance.setTextColor(Color.RED);
            else if (balance > 0) userBalance.setTextColor(Color.GREEN);
            else userBalance.setTextColor(Color.BLUE);

        };
        Response.ErrorListener errorListener = error -> {
            Toast.makeText(getActivity().getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
            Fragment boardFragment = new BoardFragment();
            FragmentChangeListener fc = (FragmentChangeListener) getActivity();
            fc.replaceFragment(boardFragment);
        };

        apiCommunicator.getRequest(getActivity().getApplicationContext(), URL+'/'+emailUser, responseListener, errorListener, null);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        reportUser.setOnClickListener(v -> {
            AlertDialog.Builder dialogDelete = new AlertDialog.Builder(getContext());
            dialogDelete.setTitle(getResources().getString(R.string.are_sure));
            dialogDelete.setMessage(getResources().getString(R.string.confirm_report_user));
            dialogDelete.setCancelable(false);
            dialogDelete.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //reportUserAPI();
                    Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.user_reported), Toast.LENGTH_LONG).show();
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

    private void getUserEvents() {
        HashMap<String, String> params = new HashMap<>();
        params.put("email", emailUser);
        getAllEvents(params);
    }

    //Call to the API
    private void getAllEvents(HashMap<String, String> params) {

        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = (Response.Listener<CustomRequest.CustomResponse>) response -> {
            List<Event> items = new ArrayList<>();
            JSONArray jsonArray;
            try {
                jsonArray = new JSONArray(response.response);
                System.out.println(String.valueOf(jsonArray.length()));
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    items.add(new Event(jsonObject));

                }

                mAdapter = new EventAdapter(items, getActivity(), (v1, position) -> {
                    Bundle bundle = new Bundle();
                    Event event = items.get(position);

                    bundle.putInt("id", event.getId());
                    bundle.putBoolean("MyProfile", true);
                    Fragment eventFragment;
                    if (event.getCreatorEmail().equals(emailUser) && correctDate(event.getIniDate())) {
                        eventFragment = MyEventFragment.newInstance(bundle);
                    }
                    else eventFragment = EventFragment.newInstance(bundle);
                    FragmentChangeListener fc = (FragmentChangeListener) getActivity();
                    fc.replaceFragment(eventFragment);
                });

                mRecyclerView.setAdapter(mAdapter);
                loadingDialog.dismiss();

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

            loadingDialog.dismiss();
            Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
        };
        apiCommunicator.getRequest(getActivity().getApplicationContext(), URL +'/'+ emailUser + "/events", responseListener, errorListener, params);
    }

    private boolean correctDate(Date iniDate) {
        if (iniDate == null) return true;
        else {
            Date currentDate = new Date();
            long hours = iniDate.getTime() - currentDate.getTime();
            hours = hours/ 1000 / 60 / 60;
            return hours >= 24;
        }
    }

    private byte[] bitmapToByteArray(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }
        else return null;
    }
}
