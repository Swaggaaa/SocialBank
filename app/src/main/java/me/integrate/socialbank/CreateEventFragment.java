package me.integrate.socialbank;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
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

import java.io.File;

public class CreateEventFragment extends Fragment {

    private static final String URL = "/recover";
    private Button buttonCreate;
    private ImageButton buttonImg;
    private Button buttonAsk;
    private Button buttonOffer;
    private Button buttonYesFixed;
    private Button buttonNoFixed;
    private Button buttonYesGroup;
    private Button buttonNoGroup;
    private TableLayout groupTable;
    private TableRow capacityRow;
    private int eventType;
    private int eventFixed;
    private int eventGroup;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_create_event, container, false);

        buttonCreate = (Button) rootView.findViewById(R.id.buttonCreate);
        buttonImg = (ImageButton) rootView.findViewById(R.id.buttonImg);
        buttonAsk = (Button) rootView.findViewById(R.id.buttonAsk);
        buttonOffer = (Button) rootView.findViewById(R.id.buttonOffer);
        buttonYesFixed = (Button) rootView.findViewById(R.id.buttonYesFixed);
        buttonNoFixed = (Button) rootView.findViewById(R.id.buttonNoFixed);
        buttonYesGroup = (Button) rootView.findViewById(R.id.buttonYesGroup);
        buttonNoGroup = (Button) rootView.findViewById(R.id.buttonNoGroup);
        groupTable = (TableLayout) rootView.findViewById(R.id.groupTable);
        capacityRow = (TableRow) rootView.findViewById(R.id.capacityRow);

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
        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonCreate.setEnabled(false);
            }
        });
        view.findViewById(R.id.buttonImg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getApplicationContext(), "IMAGE BUTTON!", Toast.LENGTH_LONG).show();
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

}
