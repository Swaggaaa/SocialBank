package me.integrate.socialbank;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;


public class Event {

    private int id;
    private String creatorEmail;
    private String iniDate, endDate;
    private String location;
    private String title;
    private String description;
    private Bitmap image;
    private int picture;
    private boolean isDemand;
    private double latitude;
    private double longitude;

    public Event(int id, String creatorEmail, String iniDate, String endDate, String location, String title, String description, Bitmap image, boolean isDemand, double latitude, double longitude) {
        this.id = id;
        this.creatorEmail = creatorEmail;
        this.iniDate = iniDate;
        this.endDate = endDate;
        this.location = location;
        this.title = title;
        this.description = description;
        this.image = image;
        this.isDemand = isDemand;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Event(int id, String creatorEmail, String iniDate, String endDate, String location, String title, String description, int picture, boolean isDemand, double latitude, double longitude) {
        this.id = id;
        this.creatorEmail = creatorEmail;
        this.iniDate = iniDate;
        this.endDate = endDate;
        this.location = location;
        this.title = title;
        this.description = description;
        this.picture = picture;
        this.isDemand = isDemand;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Event(JSONObject object) throws JSONException{
        this.id = object.getInt("id");
        this.creatorEmail = object.getString("creatorEmail");
        this.iniDate = object.getString("iniDate");
        this.endDate = object.getString("endDate");
        this.location = object.getString("location");
        this.title = object.getString("title");
        this.description = object.getString("description");
        this.image = getImageFromString(object.getString("image"));
        this.isDemand = object.getBoolean("demand");
        this.latitude = object.getDouble("latitude");
        this.longitude = object.getDouble("longitude");
    }

    private Bitmap getImageFromString(String image) {

        //TODO quitar
        if (!image.equals("")) {
            byte[] decodeString = Base64.decode(image, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
        }
        return null;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreatorEmail() {
        return creatorEmail;
    }

    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }

    public String getIniDate() {
        return iniDate;
    }

    public void setIniDate(String iniDate) {
        this.iniDate = iniDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public Boolean isDemand() {
        return isDemand;
    }

    public void setDemand(Boolean demand) {
        isDemand = demand;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }


}