package me.integrate.socialbank;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Event {

    private int id;
    private String creatorEmail;
    private Date iniDate, endDate;
    private String location;
    private String title;
    private String description;
    private Bitmap image;
    private int picture;
    private boolean isDemand;
    private double latitude;
    private double longitude;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");




    public Event(String creatorEmail, boolean demand, String description, Date finishDate, int id, Bitmap decodedByte, Date initDate, double latitude, String location, double longitude, String title) {
        this.id = id;
        this.title = title;
        this.iniDate = initDate;
        this.image = decodedByte;
        this.location = location;
        this.description = description;
        this.endDate = finishDate;
        this.isDemand = demand;
        this.longitude = longitude;
        this.latitude = latitude;
        this.creatorEmail = creatorEmail;
    }

    public Event(JSONObject object) throws JSONException {
        this.id = object.getInt("id");
        this.creatorEmail = object.getString("creatorEmail");
        this.location = object.getString("location");
        this.title = object.getString("title");
        this.description = object.getString("description");
        this.image = getImageFromString(object.getString("image"));
        this.isDemand = object.getBoolean("demand");
        this.latitude = object.getDouble("latitude");
        this.longitude = object.getDouble("longitude");
        getDates(object);
    }

    public void getDates(JSONObject object) throws JSONException {
        String iniDate = object.getString("iniDate");
        if (iniDate.equals("null")) this.iniDate = null;
        else {
            try {
                this.iniDate = sdf.parse(iniDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        String endDate = object.getString("endDate");
        if (endDate.equals("null")) this.endDate = null;
        else {
            try {
                this.endDate = sdf.parse(endDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public String getCreatorEmail() { return creatorEmail; }

    public double getLatitude() {return latitude; }

    public double getLongitude() {
        return longitude;
    }

    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public boolean getDemand() {
        return isDemand;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }

    public Date getIniDate() {
        return iniDate;
    }

    public void setIniDate(Date iniDate) {
        this.iniDate = iniDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    private Bitmap getImageFromString(String image) {

        if (!image.equals("")) {
            byte[] decodeString = Base64.decode(image, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
        }
        return null;

    }
}