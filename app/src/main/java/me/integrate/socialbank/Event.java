package me.integrate.socialbank;

import android.content.Context;
import android.graphics.Bitmap;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Event {

    private String creatorEmail;
    private boolean demand;
    private String title;
    private Date initDate;
    private String location;
    private Date finishDate;
    private int latitude;
    private int longitude;
    private String description;
    private String photoEvent;
    private int id;
    private Bitmap decodedByte;

    Context context;

    Event(String creatorEmail, boolean demand, String description, Date finishDate, int id, Bitmap decodedByte, Date initDate, int latitude, String location, int longitude, String title, Context context) {
        this.id = id;
        this.title = title;
        this.initDate = initDate;
        this.decodedByte = decodedByte;
        this.location = location;
        this.description = description;
        this.finishDate = finishDate;
        this.demand = demand;
        this.longitude = longitude;
        this.latitude = latitude;
        this.creatorEmail = creatorEmail;
        this.context = context;
    }

    public String getCreatorEmail() { return creatorEmail; }

    public int getLatitude() {return latitude; }

    public int getLongitude() {
        return longitude;
    }

    public String getTitle() {
        return title;
    }

    public Date getInitDate() {
        return initDate;
    }

    public String getInitString() {
        if (initDate == null) {
            return context.getResources().getString(R.string.notInitDate);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
            return sdf.format(finishDate);
        }
    }

    public String getLocation() {
        return location;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public String getFinishString() {
        if (finishDate == null) {
            return context.getResources().getString(R.string.notFinishDate);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
            return sdf.format(finishDate);
        }
    }

    public String getDemandOrOffer() {
        if(demand) return context.getResources().getString(R.string.demand);
        else return context.getResources().getString(R.string.offer);
    }

    public boolean getDemand() {
        return demand;
    }

    public Bitmap getImagen() {
        return decodedByte;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }
}