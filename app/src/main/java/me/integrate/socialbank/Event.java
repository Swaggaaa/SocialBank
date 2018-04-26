package me.integrate.socialbank;

public class Event {

    private String title;
    private String hour;
    private String place;
    private String date;
    private String individual;
    private int photoID;

    Event(String title, String hour, String place, String date, String individual, int photoId) {
        this.title = title;
        this.hour = hour;
        this.photoID = photoId;
        this.place = place;
        this.date = date;
        this.individual = individual;
    }

    public String getTitle() {
        return title;
    }

    public String getHour() {
        return hour;
    }

    public String getPlace() {
        return place;
    }

    public String getDate() {
        return date;
    }

    public String getIndividual() {
        return individual;
    }

    public int getImagen() {
        return photoID;
    }


}