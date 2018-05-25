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

    Comment(JSONObject jsonObject) {
        try {
            this.user = jsonObject.getString("name");
            this.surname = jsonObject.getString("surname");
            //TODO no se aun como se llama
            this.text = jsonObject.getString("text");
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

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
}
