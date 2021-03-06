package me.integrate.socialbank;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class EventFragment extends Fragment implements AddCommentFragment.OnCommentSelected {

    private static final String URL = "/events";
    private static final String SOCIALBANK_URL = "http://socialbank.com";

    protected Button join_button;

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

    private Integer capacity;
    private Integer numberEnrolled;

    private CardView exchangeTokenCard;
    private TextView exchangeToken;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ProgressDialog loadingDialog;


    private List<Comment> comments;
    protected String creator;
    protected String title;
    protected int id;
    protected String descriptionEvent;
    protected Date iniDate;
    protected Date endDate;
    CardView payHoursCard;
    Button payButton;


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
        addComment = (ImageView) rootView.findViewById(R.id.addComment);
        exchangeTokenCard = (CardView) rootView.findViewById(R.id.card_exchangeToken);
        exchangeToken = (TextView) rootView.findViewById(R.id.exchangeToken);
        payHoursCard = (CardView) rootView.findViewById(R.id.card_payHours);
        payButton = (Button) rootView.findViewById(R.id.pay_button);

        join_button = (Button) rootView.findViewById(R.id.join_button);
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
                title = event.getTitle();
                textEventTitle.setText(title);
                creator = event.getCreatorEmail();
                SpannableString creatorEmail = new SpannableString(event.getCreatorEmail());
                creatorEmail.setSpan(new UnderlineSpan(), 0, creatorEmail.length(), 0);
                textEventOrganizer.setText(creatorEmail);
                textEventCategory.setText(event.getCategory().toString());
                descriptionEvent = event.getDescription();
                textEventDescription.setText(descriptionEvent);
                textLocation.setText(event.getLocation());
                textDemandEvent.setText(event.getDemand() ? R.string.demand : R.string.offer);
                imageView.setImageBitmap(getImage(event.getImage()));

                capacity = event.getCapacity();
                numberEnrolled = event.getNumberEnrolled();

                if(event.isIndividual())
                    textIndividualOrGroup.setText(R.string.individual);
                else
                    textIndividualOrGroup.setText(R.string.groupal);

                textViewNumberPersonsEvent.setText(numberEnrolled+"/"+capacity);

                iniDate = event.getIniDate();
                endDate = event.getEndDate();
                long hoursL = getHours(iniDate, endDate);
                String hours =  (hoursL > 0) ? String.valueOf(hoursL) : getResources().getString(R.string.notHour) + " " + getResources().getString(R.string.time_hours);
                textEventHours.setText(hours);
                textStartDate.setText(dateToString(iniDate));
                textEndDate.setText(dateToString(endDate));

                if (event.getExchangeToken() != null) {
                    exchangeToken.setText(event.getExchangeToken());
                    exchangeTokenCard.setVisibility(View.VISIBLE);
                }

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

    protected long getHours(Date hourIni, Date hourEnd) {
        long hours = 0;
        if (hourIni != null && hourEnd != null ) {
            long diff = hourEnd.getTime() - hourIni.getTime();
            hours = diff/1000/ 60 / 60;
        }
        return hours;
    }

    protected String getCreator() {
        return creator;
    }

    protected void changesEnrollment(boolean join) {
        if (join) numberEnrolled++;
        else  numberEnrolled--;
        textViewNumberPersonsEvent.setText(numberEnrolled+"/"+capacity);
    }

    protected boolean isEventFull() {
        return Objects.equals(capacity, numberEnrolled);
    }

    private String dateToString(Date date) {
        if (date == null) return getResources().getString(R.string.notDate);
        else{
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            return df.format(date);
        }
    }

    protected Bitmap getImage(Bitmap bitmap)
    {
        if (bitmap != null) {
            double originWidth = bitmap.getWidth();
            double originHeight = bitmap.getHeight();

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            double displayWidht = displayMetrics.widthPixels;

            if (displayWidht > originWidth) {
                double destHeight = originHeight * (displayWidht / originWidth);
                bitmap = Bitmap.createScaledBitmap(bitmap, (int) displayWidht, (int) destHeight, false);
                imageView.setImageBitmap(bitmap);
            }
        }
        return bitmap;
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
                shareEvent());
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
                Collections.sort(comments, (comment, t1) -> t1.getCreateDate().compareTo(comment.getCreateDate()));
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
