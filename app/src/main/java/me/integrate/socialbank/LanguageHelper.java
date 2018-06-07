package me.integrate.socialbank;

import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class LanguageHelper {
    static List<String> language;


    LanguageHelper() {
        language = new ArrayList<>();

        //Ordered as in values - arrays.xml - languages
        language.add("es_ES");
        language.add("ca");
        language.add("en");
    }


    public static void changeLocale(Resources res, int position) {
        Configuration config;
        config = new Configuration(res.getConfiguration());
        language.get(position);
        config.locale = new Locale(language.get(position));
        res.updateConfiguration(config, res.getDisplayMetrics());
    }

    public static int getPosition(String lang) {
        return  language.indexOf(lang);
    }

}
