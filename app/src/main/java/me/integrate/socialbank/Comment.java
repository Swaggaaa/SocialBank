package me.integrate.socialbank;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Comment {

    private String user;
    private String text;
    private String surname;
    private String emailCreator;
    private Date createDate;
    private int id;

    Comment(JSONObject jsonObject) {
        try {
            this.user = jsonObject.getString("userName");
            this.surname = jsonObject.getString("userSurname");
            this.text = jsonObject.getString("content");
            this.emailCreator = jsonObject.getString("creatorEmail");
            this.id = jsonObject.getInt("id");
            getDates(jsonObject);
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    private void getDates(JSONObject object) throws JSONException {
        String iniDate = object.getString("createdAt");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS");
        if (iniDate.equals("null")) this.createDate = null;
        else {
            try {
                this.createDate = df.parse(iniDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public Date getCreateDate() {return createDate;}

    public void setCreateDate(Date createDate) { this.createDate = createDate;}

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
