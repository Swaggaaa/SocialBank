package me.integrate.socialbank;

import android.graphics.Bitmap;

public class Event {

    private String title;
    private int id;
    private String initDate;
    private String place;
    private String finishDate;
    private String individual;
    private String description;
    private int image;
    private Bitmap photoID;

    Event(int id, String title, String initDate, String place, String finishDate, String individual, String description, Bitmap photoId) {
        this.id = id;
        this.title = title;
        this.initDate = initDate;
        this.photoID = photoId;
        this.place = place;
        this.description = description;
        this.finishDate = finishDate;
        this.individual = individual;
    }

    Event(int id, String title, String initDate, String place, String finishDate, String individual, String description, int image) {
        this.id = id;
        this.title = title;
        this.initDate = initDate;
        this.place = place;
        this.image = image;
        this.description = description;
        this.finishDate = finishDate;
        this.individual = individual;
    }

    public String getTitle() {
        return title;
    }

    public String getInitDate() {
        return initDate;
    }

    public String getPlace() {
        return place;
    }

    public String getFinishDate() {
        return finishDate;
    }

    public String getIndividual() {
        return individual;
    }

    public Bitmap getImagen() {
        return photoID;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }
}