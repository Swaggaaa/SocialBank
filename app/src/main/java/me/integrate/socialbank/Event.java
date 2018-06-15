package me.integrate.socialbank;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.StringRes;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Event {

    private int id;
    private int capacity;
    private int numberEnrolled;
    private String creatorEmail;
    private Date iniDate, endDate;
    private String location;
    private String title;
    private String description;
    private Bitmap image;
    private boolean isDemand;
    private double latitude;
    private double longitude;
    private Category category;

    public Event(JSONObject object) throws JSONException {
        this.id = object.getInt("id");
        this.capacity = object.getInt("capacity");
        this.numberEnrolled = object.getInt("numberEnrolled");
        this.creatorEmail = object.getString("creatorEmail");
        this.location = object.getString("location");
        this.title = object.getString("title");
        this.description = object.getString("description");
        this.image = getImageFromString(object.getString("image"));
        this.isDemand = object.getBoolean("demand");
        this.latitude = object.getDouble("latitude");
        this.longitude = object.getDouble("longitude");
        this.category = Category.valueOf(object.getString("category"));
        getDates(object);
    }

    public Event(String creatorEmail, boolean demand, String description, Date finishDate, int id, int capacity, int numberEnrolled, Bitmap decodedByte, Date initDate, double latitude, String location, double longitude, String title) {
        this.id = id;
        this.capacity = capacity;
        this.numberEnrolled = numberEnrolled;
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

    public Category getCategory() {
        return category;
    }

    private void getDates(JSONObject object) throws JSONException {
        String iniDate = object.getString("iniDate");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS");
        if (iniDate.equals("null")) this.iniDate = null;
        else {
            try {
                this.iniDate = df.parse(iniDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        String endDate = object.getString("endDate");
        if (endDate.equals("null")) this.endDate = null;
        else {
            try {
                this.endDate = df.parse(endDate);
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

    public int getCapacity() {
        return capacity;
    }

    public int getNumberEnrolled() {
        return numberEnrolled;
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

    public boolean isAvailable() {
        return (iniDate == null || (iniDate.compareTo(new Date()) > 0));
    }

    // 'true' if it's a no dated event or is going to start in more than 24 hours
    public boolean stillEditable() {
        return (iniDate == null ||
                ((iniDate.getTime()-(new Date()).getTime())/(1000.0*60.0*60.0*24.0)) > 1.0);
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

    public void setCategory(Category category) {
        this.category = category;
    }

    public boolean isIndividual() {
        return capacity == 1;
    }

    enum Category {
        LANGUAGE(R.string.category_language),
        CULTURE(R.string.category_culture),
        WORKSHOPS(R.string.category_workshops),
        SPORTS(R.string.category_sports),
        GASTRONOMY(R.string.category_gastronomy),
        LEISURE(R.string.category_leisure),
        OTHER(R.string.category_other);

        private @StringRes
        int label;

        Category(@StringRes int label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return App.getContext().getResources().getString(label);
        }
    }

    private Bitmap getImageFromString(String image) {

        if (!image.equals("")) {
            byte[] decodeString = Base64.decode(image, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
        }
        return null;

    }

}