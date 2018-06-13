package me.integrate.socialbank;


import android.content.Intent;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class EventFragment extends Fragment implements AddCommentFragment.OnCommentSelected {

    private static final String URL = "/events";
    private static final String SOCIALBANK_URL = "http://socialbank.com";

    protected ImageView imageView;
    private ImageView addComment;
    private TextView textEventTitle;
    private TextView textEventOrganizer;
    private TextView textEventCategory;
    protected TextView textEventDescription;
    private TextView textDemandEvent;
    private TextView textLocation;
    private TextView textIndividualOrGroup;
    private TextView textViewNumberPersonsEvent;
    private TextView textEventHours;
    private TextView textStartDate;
    private TextView textEndDate;
    protected EditText editDescription;


    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ProgressDialog loadingDialog;


    private List<Comment> comments;
    private String creator;
    private Date iniDate;
    protected int id;
    protected String descriptionEvent;

    public static EventFragment newInstance(Bundle params) {
        EventFragment eventFragment = new EventFragment();
        eventFragment.setArguments(params);
        return eventFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_event, container, false);
        imageView = (ImageView) rootView.findViewById(R.id.imageViewEvent);
        textEventTitle = (TextView) rootView.findViewById(R.id.textEventTitle);
        textEventOrganizer = (TextView) rootView.findViewById(R.id.textEventOrganizer);
        textEventCategory = (TextView) rootView.findViewById(R.id.textEventCategory);
        textEventDescription = (TextView) rootView.findViewById(R.id.textEventDescription);
        textDemandEvent = (TextView) rootView.findViewById(R.id.demand_event);
        textLocation = (TextView) rootView.findViewById(R.id.location_event);
        textIndividualOrGroup = (TextView) rootView.findViewById(R.id.individual_or_group);
        textViewNumberPersonsEvent = (TextView) rootView.findViewById(R.id.number_person);
        textEventHours = (TextView) rootView.findViewById(R.id.hours);
        textStartDate = (TextView) rootView.findViewById(R.id.start_date);
        textEndDate = (TextView) rootView.findViewById(R.id.end_date);
        editDescription = (EditText) rootView.findViewById(R.id.editDescription);
        addComment = (ImageView) rootView.findViewById(R.id.addComment);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_comment);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext(), false));

        comments = new ArrayList<>();

         loadingDialog = ProgressDialog.show(getActivity(), "",
                getString(R.string.loadingMessage), true);

        id = getArguments().getInt("id");
        getComments();
        showEventInformation();
        return rootView;
    }

            //Call API to obtain event's information
    void showEventInformation() {
        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = (Response.Listener<CustomRequest.CustomResponse>) response -> {
            try {
                Event event = new Event(new JSONObject(response.response));
                textEventTitle.setText(event.getTitle());
                creator = event.getCreatorEmail();
                textEventOrganizer.setText(creator);
                textEventCategory.setText(event.getCategory().toString());
                descriptionEvent = event.getDescription();
                textEventDescription.setText(descriptionEvent);
                textLocation.setText(event.getLocation());
                textDemandEvent.setText(event.getDemand() ? R.string.demand : R.string.offer);
                imageView.setImageBitmap(event.getImage());

                editDescription.setText(descriptionEvent);

                //TODO not hardcoded this values
                textIndividualOrGroup.setText("Individual");
                textViewNumberPersonsEvent.setText("1/1");

                iniDate = event.getIniDate();
                Date endDate = event.getEndDate();
                String hours = getHours(iniDate, endDate) + " " + getResources().getString(R.string.time_hours);
                textEventHours.setText(hours);
                textStartDate.setText(dateToString(iniDate));
                textEndDate.setText(dateToString(endDate));
                loadingDialog.dismiss();
            } catch (JSONException e) {
                Toast.makeText(EventFragment.this.getActivity().getApplicationContext(), R.string.JSONException, Toast.LENGTH_LONG).show();
            }
        };
        Response.ErrorListener errorListener = error -> errorTreatment(error.networkResponse.statusCode);

        apiCommunicator.getRequest(getActivity().getApplicationContext(), URL+'/'+ id, responseListener, errorListener, null);

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
        loadingDialog.dismiss();
        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private String getHours(Date hourIni, Date hourEnd) {
        if (hourIni != null && hourEnd != null ) {
            long diff = hourEnd.getTime() - hourIni.getTime();
            long seconds = diff/1000;
            long minutes = seconds/60;
            long hours = minutes/60;
            return String.valueOf(hours);
        } else return getResources().getString(R.string.notHour);
    }

    private String dateToString(Date date) {
        if (date == null) return getResources().getString(R.string.notDate);
        else{
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            return df.format(date);
        }
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.textEventOrganizer).setOnClickListener(v ->
        {
            Bundle b = new Bundle();
            b.putString("email", creator);
            Fragment profileFragment;
            profileFragment = !creator.equals(SharedPreferencesManager.INSTANCE.read(getActivity(), "user_email")) ? new ProfileFragment() : new MyProfileFragment();
            profileFragment.setArguments(b);
            FragmentChangeListener fc = (FragmentChangeListener) getActivity();
            fc.replaceFragment(profileFragment);
        });
        view.findViewById(R.id.invite_button).setOnClickListener(v ->
        {
            shareEvent();
        });
        addComment.setOnClickListener(v ->
        {
            Bundle b = new Bundle();
            b.putInt("id", id);
            FragmentManager fm  = getFragmentManager();
            AddCommentFragment addCommentFragment = new AddCommentFragment();
            addCommentFragment.setTargetFragment(EventFragment.this, 1);
            addCommentFragment.setArguments(b);
            addCommentFragment.show(fm, "prova");
        });

    }

    private void shareEvent() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.join_msg, textEventTitle.getText().toString(), SOCIALBANK_URL, id));
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    public void getComments() {
        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = (Response.Listener<CustomRequest.CustomResponse>) response -> {
            JSONArray jsonArray;
            try {

                jsonArray = new JSONArray(response.response);
                System.out.println(String.valueOf(jsonArray.length()));
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    comments.add(new Comment(jsonObject));
                }
                Collections.sort(comments, new Comparator<Comment>() {
                    @Override
                    public int compare(Comment comment, Comment t1) {
                        return t1.getCreateDate().compareTo(comment.getCreateDate());
                    }
                });
                mAdapter = new CommentAdapter(comments, getActivity(), (v1, position) -> {
                    Bundle bundle = new Bundle();
                    String email = comments.get(position).getEmailCreator();
                    bundle.putString("email", email);
                    FragmentChangeListener fc = (FragmentChangeListener) getActivity();
                    ProfileFragment profileFragment = !email.equals(SharedPreferencesManager.INSTANCE.read(getActivity(), "user_email")) ? new ProfileFragment() : new MyProfileFragment();
                    profileFragment.setArguments(bundle);
                    fc.replaceFragment(profileFragment);

               });

                mRecyclerView.setAdapter(mAdapter);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        };
        Response.ErrorListener errorListener = error -> errorTreatment(error.networkResponse.statusCode);
        apiCommunicator.getRequest(getActivity().getApplicationContext(), URL + '/' + id + '/' + "comments", responseListener, errorListener, null);

    }

    @Override
    public void sendComment() {
        comments.clear();
        getComments();
    }
}
