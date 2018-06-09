package me.integrate.socialbank;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

public class Comment {

    private String user;
    private String text;
    private String surname;
    private String emailCreator;
    private int id;

    Comment(JSONObject jsonObject) {
        try {
            this.user = jsonObject.getString("userName");
            this.surname = jsonObject.getString("userSurname");
            this.text = jsonObject.getString("content");
            this.emailCreator = jsonObject.getString("creatorEmail");
            this.id = jsonObject.getInt("id");
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    public int getId() { return id;}

    public void setId (int id) { this.id = id;}

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getComment() {
        return text;
    }

    public void setComment(String text) {
        this.text = text;
    }

    public String getEmailCreator() {return emailCreator;}

    public void setEmailCreator(String emailCreator) {this.emailCreator = emailCreator;}
}
