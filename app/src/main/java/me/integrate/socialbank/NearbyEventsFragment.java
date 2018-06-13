package me.integrate.socialbank;

import android.annotation.SuppressLint;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.LOCATION_SERVICE;

public class NearbyEventsFragment extends Fragment {

    private static final String URL = "/events";

    MapView mMapView;
    private GoogleMap googleMap;
    private LatLng myPosition;
    private EditText address;
    private Button searchButton;
    private Map<Marker, Event> eventsMap;
    private boolean verified;


    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_nearby_events, container, false);
        address = (EditText) rootView.findViewById(R.id.editText);
        searchButton = (Button)rootView.findViewById(R.id.search_button);
        enableButton();
        verified = Boolean.parseBoolean(SharedPreferencesManager.INSTANCE.read(getActivity(),"verified"));

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        eventsMap = new HashMap<>();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            Toast.makeText(getActivity().getApplicationContext(), R.string.unexpectedError, Toast.LENGTH_LONG).show();
        }

        mMapView.getMapAsync(mMap -> {
            googleMap = mMap;

            googleMap.setMyLocationEnabled(true);
            googleMap.setOnInfoWindowClickListener(marker -> {
                Event event = eventsMap.get(marker);
                Bundle bundle = new Bundle();
                bundle.putInt("id", event.getId());
                Fragment eventFragment;
                boolean eventCreator = event.getCreatorEmail().equals(SharedPreferencesManager.INSTANCE.read(getActivity(),"user_email"));
                if( eventCreator && event.stillEditable() )
                    eventFragment = MyEventFragment.newInstance(bundle);
                else if( !eventCreator && event.isAvailable() && !verified)
                    eventFragment = MyJoinEventFragment.newInstance(bundle);
                else
                    eventFragment = EventFragment.newInstance(bundle);
                FragmentChangeListener fc = (FragmentChangeListener) getActivity();
                fc.replaceFragment(eventFragment);

            });

            LocationManager mLocationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(LOCATION_SERVICE);
            List<String> providers = mLocationManager.getProviders(true);
            Location bestLocation = null;
            for (String provider : providers) {
                Location l = mLocationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    // Found best last known location: %s", l);
                    bestLocation = l;
                }
            }

            double latitude = bestLocation.getLatitude();
            double longitude = bestLocation.getLongitude();

            showNearbyEvents();
            myPosition = new LatLng(latitude, longitude);
            CameraPosition cameraPosition = new CameraPosition.Builder().target(myPosition).zoom(12).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        });

        return rootView;
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchButton.setOnClickListener(view1 -> {
            searchButton.setEnabled(false);

            if (address.getText().toString().length() != 0) {
                EventLocation eventLocation = new EventLocation(address.getText().toString());
                if (eventLocation.getAddress() == null) {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.AddressNotFound, Toast.LENGTH_LONG).show();
                } else {
                    myPosition = new LatLng(eventLocation.getLatitude(), eventLocation.getLongitude());
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(myPosition).zoom(12).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
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
    }

    private void showNearbyEvents() {
        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = (Response.Listener<CustomRequest.CustomResponse>) response -> {
            JSONArray jsonArray;
            try {
                jsonArray = new JSONArray(response.response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    Event event = new Event(jsonArray.getJSONObject(i));
                    LatLng eventPosition = new LatLng(event.getLatitude(), event.getLongitude());
                    Marker marker = googleMap.addMarker(new MarkerOptions().position(eventPosition).title(event.getTitle()).snippet(event.getDescription()));
                    eventsMap.put(marker, event);
                }
            } catch (JSONException e) {
                Toast.makeText(getActivity().getApplicationContext(), R.string.JSONException, Toast.LENGTH_LONG).show();
            }
        };
        Response.ErrorListener errorListener = error -> errorTreatment(error.networkResponse.statusCode);

        apiCommunicator.getRequest(getActivity().getApplicationContext(), URL, responseListener, errorListener, null);

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
        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }


    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private boolean areFilled() {
        return !address.getText().toString().isEmpty();
    }

    private void enableButton() {
        searchButton.setEnabled(areFilled());
    }
}