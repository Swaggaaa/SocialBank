package me.integrate.socialbank;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class ConvertAddressToCoordinatesTask extends AsyncTask<URL,Integer,EventLocation > {
    @Override
    protected EventLocation doInBackground(URL... urls) {
        EventLocation eventLocation = null;
        try {

            HttpURLConnection conn = (HttpURLConnection) urls[0].openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() == 500) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String output = "", full = "";
            while ((output = br.readLine()) != null) {
                System.out.println(output);
                full += output;
            }

            //full = "{\"results\":[{\"place_id\":\"ChIJ2eUgeAK6j4ARbn5u_wAGqWA\",\"address_components\":[{\"long_name\":\"1600\",\"types\":[\"street_number\"],\"short_name\":\"1600\"},{\"long_name\":\"Amphitheatre Pkwy\",\"types\":[\"route\"],\"short_name\":\"Amphitheatre Pkwy\"},{\"long_name\":\"Mountain View\",\"types\":[\"locality\",\"political\"],\"short_name\":\"Mountain View\"},{\"long_name\":\"Santa Clara County\",\"types\":[\"administrative_area_level_2\",\"political\"],\"short_name\":\"Santa Clara County\"},{\"long_name\":\"California\",\"types\":[\"administrative_area_level_1\",\"political\"],\"short_name\":\"CA\"},{\"long_name\":\"United States\",\"types\":[\"country\",\"political\"],\"short_name\":\"US\"},{\"long_name\":\"94043\",\"types\":[\"postal_code\"],\"short_name\":\"94043\"}],\"formatted_address\":\"1600 Amphitheatre Parkway, Mountain View, CA 94043, USA\",\"types\":[\"street_address\"],\"geometry\":{\"viewport\":{\"southwest\":{\"lng\":-122.0855988802915,\"lat\":37.4211274197085},\"northeast\":{\"lng\":-122.0829009197085,\"lat\":37.4238253802915}},\"location_type\":\"ROOFTOP\",\"location\":{\"lng\":-122.0842499,\"lat\":37.4224764}}}],\"status\":\"OK\"}";


            JSONObject json = new JSONObject(full);


            JSONObject results = json.getJSONArray("results").getJSONObject(0);
            String address = results.getString("formatted_address");
            JSONObject coordinates = results.getJSONObject("geometry").getJSONObject("location");
            String longitude = coordinates.getString("lng");
            String latitude = coordinates.getString("lat");

            System.out.println(latitude);

            eventLocation = new EventLocation(address, Double.parseDouble(latitude), Double.parseDouble(longitude));
            return eventLocation;


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return eventLocation;
    }
}
