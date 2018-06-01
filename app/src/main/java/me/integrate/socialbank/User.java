package me.integrate.socialbank;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

class User {

    private String name;
    private String surname;
    private String email;
    private Bitmap image;

    User(JSONObject jsonObject) {
        try {
            this.name = jsonObject.getString("name");
            this.surname = jsonObject.getString("surname");
            String base64 = jsonObject.getString("image");
            byte[] decodeString = Base64.decode(base64, Base64.DEFAULT);
            this.image = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
            this.email = jsonObject.getString("email");
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public Bitmap getImageRounded() {

        if (image != null) {
            image = ImageHelper.cropBitmapToSquare(image);
            return ImageHelper.getRoundedCornerBitmap(image, 120);
        }
        return null;

    }
}
