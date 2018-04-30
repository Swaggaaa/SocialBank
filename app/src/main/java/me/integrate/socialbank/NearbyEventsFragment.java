package me.integrate.socialbank;

import android.content.Context;
import android.hardware.SensorManager;
import android.location.Criteria;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import static android.content.Context.LOCATION_SERVICE;

public class NearbyEventsFragment extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;
    private LatLng myPosition;

    private EditText address;
    private Button searchButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_nearby_events, container, false);
        address = (EditText) rootView.findViewById(R.id.editText);
        searchButton = (Button)rootView.findViewById(R.id.search_button);
        enableButton();

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // For showing a move to my location button
                googleMap.setMyLocationEnabled(true);

               /* Criteria criteria = new Criteria();

                LocationManager locationManager = (LocationManager)getActivity().getApplicationContext().getSystemService(LOCATION_SERVICE);
                String provider = locationManager.getBestProvider(criteria, true);
                Location location = locationManager.getLastKnownLocation(provider);
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                myPosition = new LatLng(latitude, longitude);
                */

                //For dropping a marker at a point on the Map
                LatLng sydney = new LatLng(-34, 151);
                //googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"));

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });

        return rootView;
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchButton.setEnabled(false);

                if (address.getText().toString().length() != 0) {
                    myPosition = convertToCoordinates(address.getText().toString());
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


    private LatLng convertToCoordinates(String address) {
        URL url = getUrlForAddress(address);

        LatLng coordinates = null;
        try {
            coordinates = new ConvertAddressToCoordinatesTask().execute(url).get();
        }catch (ExecutionException e1){
            e1.printStackTrace();
        }catch (InterruptedException e2) {
            e2.printStackTrace();
        }
        return coordinates;
    }

    private URL getUrlForAddress(String address) {

        address = address.replace(' ', '+');
        String urlString = "https://maps.googleapis.com/maps/api/geocode/json?address=" + address + "&sensor=true&key=AIzaSyBIeFwQVoHYg8xhtLU3bd7ujWNtY3wuTnw\n";

        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
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

    //Se extrae en función externa por si se quiere modificar el estilo
    private void enableButton() {
        searchButton.setEnabled(areFilled());
    }
}