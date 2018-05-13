package me.integrate.socialbank;

import android.app.Application;
import android.content.Context;

public class App extends Application {

    // TODO: find better solution
    private static Context mContext;

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }
}