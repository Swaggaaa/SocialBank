package me.integrate.socialbank;


import com.google.android.gms.maps.model.LatLng;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class EventLocation {
    private String address;
    private double latitude;
    private double longitude;

    public EventLocation (String address, double latitude, double longitude) {
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public EventLocation(String address) {
        EventLocation eventLocation = convertToCoordinates(address);
        if (eventLocation != null) {
            this.address = eventLocation.address;
            this.longitude = eventLocation.longitude;
            this.latitude = eventLocation.latitude;
        }
    }


    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    private EventLocation convertToCoordinates(String address) {
        URL url = getUrlForAddress(address);

        EventLocation coordinates = null;
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

}
