package me.integrate.socialbank;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
    private TextView userBalanceText;
    private TextView reportUsserText;
    private TextView editProfileText;
    private TextView changePictureText;
    String emailUser;
    String nameUser;
    String lastNameUser;
    String dateUser;
    String genderUser;
    String descriptionUser;
    private boolean isFABOpen;
    FloatingActionButton openMenu;
    FloatingActionButton reportUserButton;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;


    private RecyclerView awardRecyclerView;
    private RecyclerView.Adapter awardAdapter;

    private List<String> items = new ArrayList<>();

    protected ProgressDialog loadingDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_see_my_profile, container, false);
        userPicture = (ImageView) rootView.findViewById(R.id.myProfileImage);
        userName = (TextView) rootView.findViewById(R.id.myProfileName);
        userEmailToShow = (TextView) rootView.findViewById(R.id.userEmailToShow);
        userBalance = (TextView) rootView.findViewById(R.id.hoursBalance);
        userBalance.setVisibility(View.GONE);
        userDescription = (TextView) rootView.findViewById(R.id.aboutMe);
        userBalanceText = (TextView) rootView.findViewById(R.id.userBalanceText);
        userBalanceText.setVisibility(View.GONE);
        myEvents = (TextView)rootView.findViewById(R.id.events);
        reportUsserText = (TextView) rootView.findViewById(R.id.reportUserText);
        editProfileText = (TextView) rootView.findViewById(R.id.editProfileText);
        changePictureText = (TextView) rootView.findViewById(R.id.changePasswordText);
        awardRecyclerView = (RecyclerView) rootView.findViewById(R.id.award_recycler_view);
        awardRecyclerView.setHasFixedSize(true);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view_user_profile);
        mRecyclerView.setHasFixedSize(true);
        isFABOpen = false;
        items.clear();
        openMenu = (FloatingActionButton) rootView.findViewById(R.id.openMenu);
        reportUserButton = (FloatingActionButton) rootView.findViewById(R.id.reportProfile);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        RecyclerView.LayoutManager awardLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, false);
        awardRecyclerView.setLayoutManager(awardLayoutManager);
        mRecyclerView.setLayoutManager(mLayoutManager);
        loadingDialog = ProgressDialog.show(getActivity(), "", getString(R.string.loadingMessage), true);
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
        @SuppressLint("ResourceAsColor") Response.Listener responseListener = (Response.Listener<CustomRequest.CustomResponse>) response -> {
            JSONObject jsonObject;
            Float balance = null;
            try{
                jsonObject = new JSONObject(response.response);
                nameUser = jsonObject.getString("name");
                String events_en = getString(R.string.personal_events);
                String events = getString(R.string.personal_events_ES);
                myEvents.setText(events+nameUser+events_en);
                lastNameUser = jsonObject.getString("surname");
                dateUser = jsonObject.getString("birthdate");
                genderUser = jsonObject.getString("gender");
                descriptionUser = jsonObject.getString("description");
                String completeName = nameUser + " " + lastNameUser;
                userName.setText(completeName);
                balance = BigDecimal.valueOf(jsonObject.getDouble("balance")).floatValue();
                userBalance.setText(balance.toString());
                if (balance < 0) userBalance.setTextColor(this.getResources().getColor(R.color.negative_balance));
                else if (balance > 0) userBalance.setTextColor(this.getResources().getColor(R.color.positive_balance));
                userEmailToShow.setText(jsonObject.getString("email"));
                if(!descriptionUser.equals("null")) userDescription.setText(descriptionUser);
                String image = jsonObject.getString("image");
                if (!image.equals("")&& !image.equals("null")) {
                    byte[] decodeString = Base64.decode(image, Base64.DEFAULT);
                    userPicture.setImageBitmap(getImageRounded(BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length)));
                }

                JSONArray jsonArray = jsonObject.getJSONArray("awards");
                for (int i = 0; i < jsonArray.length(); ++i) {
                    items.add(jsonArray.getString(i));
                }

                awardAdapter = new AwardAdapter(items, (v1, position) -> {
                });

                awardRecyclerView.setAdapter(awardAdapter);
                loadingDialog.dismiss();

            } catch (JSONException e) {
                e.printStackTrace();
            }



        };
        Response.ErrorListener errorListener = error -> {
            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
            Fragment boardFragment = new BoardFragment();
            FragmentChangeListener fc = (FragmentChangeListener) getActivity();
            fc.replaceFragment(boardFragment);
        };

        apiCommunicator.getRequest(getActivity().getApplicationContext(), URL+'/'+emailUser, responseListener, errorListener, null);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        reportUserButton.setOnClickListener(v -> {
            AlertDialog.Builder dialogDelete = new AlertDialog.Builder(getContext());
            dialogDelete.setTitle(getResources().getString(R.string.are_sure));
            dialogDelete.setMessage(getResources().getString(R.string.confirm_report_user));
            dialogDelete.setCancelable(false);
            dialogDelete.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("email", emailUser);
                    sendReportUser(params);
                }
            });
            dialogDelete.setNegativeButton(getResources().getString(R.string.discard), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            dialogDelete.show();
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
        reportUserButton.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        reportUsserText.setVisibility(View.VISIBLE);
        reportUsserText.bringToFront();
        reportUsserText.animate().translationY(-getResources().getDimension(R.dimen.standard_55));

    }

    private void closeFABMenu() {
        isFABOpen = false;
        reportUserButton.animate().translationY(0);
        reportUsserText.animate().translationY(0);
        reportUsserText.setVisibility(View.GONE);
    }

    private void sendReportUser(HashMap<String, Object> params) {
        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = (Response.Listener<CustomRequest.CustomResponse>) response -> {
            Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.user_reported), Toast.LENGTH_LONG).show();
        };
        Response.ErrorListener errorListener = error -> errorTreatment(error.networkResponse.statusCode);

        apiCommunicator.postRequest(getActivity().getApplicationContext(), URL+'/'+emailUser+"/report", responseListener, errorListener, params);
    }

    private void errorTreatment(int errorCode) {
        String message;
        if (errorCode == 401)
            message = getString(R.string.unauthorized);
        else if (errorCode == 403)
            message = getString(R.string.forbidden);
        else if (errorCode == 404)
            message = getString(R.string.not_found);
        else if (errorCode == 409)
            message = getString(R.string.user_already_reported);
        else
            message = getString(R.string.unexpectedError);

        loadingDialog.dismiss();
        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private void getUserEvents() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("email", emailUser);
        getAllEvents(params);
    }

    //Call to the API
    private void getAllEvents(HashMap<String, Object> params) {

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
                    if (event.getCreatorEmail().equals(SharedPreferencesManager.INSTANCE.read(getActivity(), "user_email")) && correctDate(event.getIniDate())) {
                        eventFragment = MyEventFragment.newInstance(bundle);
                    }
                    else eventFragment = EventFragment.newInstance(bundle);
                    FragmentChangeListener fc = (FragmentChangeListener) getActivity();
                    fc.replaceFragment(eventFragment);
                });

                mRecyclerView.setAdapter(mAdapter);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        };
        Response.ErrorListener errorListener = error -> errorTreatment(error.networkResponse.statusCode);
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

    protected Bitmap getImageRounded(Bitmap image) {
        image = ImageHelper.cropBitmapToSquare(image);
        image = ImageHelper.getRoundedCornerBitmap(image, 420);
        return image;

    }

}
