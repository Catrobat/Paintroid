package org.catrobat.paintroid;

/**
 * Created by Aiman M. Ayyal Awwad on 6/28/2016.
 */


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import java.util.Locale;


public class MultilanguageActivity extends Activity {
    Locale mLocale;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.language_settings);
        setTitle(R.string.activity_title);
    }

    public void Arabic_Lang(View view) {
        mLocale=new Locale("ar");
        setContextLocale(this, mLocale);
    }
    public void English_Lang(View view) {
        mLocale=new Locale("en");
        setContextLocale(this, mLocale);
    }

    public void Bosnian_Lang(View view) {
        mLocale=new Locale("bs");
        setContextLocale(this, mLocale);
    }

    public void Danish_Lang(View view) {
        mLocale=new Locale("da");
        setContextLocale(this, mLocale);
    }

    public void German_Lang(View view) {
        mLocale=new Locale("de");
        setContextLocale(this, mLocale);
    }

    public void English_AU_Lang(View view) {
        mLocale=new Locale("en","AU");
        setContextLocale(this, mLocale);
    }

    public void English_CA_Lang(View view) {
        mLocale=new Locale("en","CA");
        setContextLocale(this, mLocale);
    }

    public void English_GB_Lang(View view) {
        mLocale=new Locale("en","GB");
        setContextLocale(this, mLocale);
    }

    public void Spanish_Lang(View view) {
        mLocale=new Locale("es");
        setContextLocale(this, mLocale);
    }

    public void Farsi_Lang(View view) {
        mLocale=new Locale("fa");
        setContextLocale(this, mLocale);
    }

    public void French_Lang(View view) {
        mLocale=new Locale("fr");
        setContextLocale(this, mLocale);
    }

    public void Gujarati_Lang(View view) {
        mLocale=new Locale("gu");
        setContextLocale(this, mLocale);
    }

    public void Hindi_Lang(View view) {
        mLocale=new Locale("hi");
        setContextLocale(this, mLocale);
    }

    public void Croatian_Lang(View view) {
        mLocale=new Locale("hr");
        setContextLocale(this, mLocale);
    }

    public void Hungarian_Lang(View view) {
        mLocale=new Locale("hu");
        setContextLocale(this, mLocale);
    }

    public void Indonesian_Lang(View view) {
        mLocale=new Locale("id");
        setContextLocale(this, mLocale);
    }

    public void Italian_Lang(View view) {
        mLocale=new Locale("it");
        setContextLocale(this, mLocale);
    }

    public void Japanese_Lang(View view) {
        mLocale=new Locale("ja");
        setContextLocale(this, mLocale);
    }

    public void Korean_Lang(View view) {
        mLocale=new Locale("ko");
        setContextLocale(this, mLocale);
    }

    public void Macedonian_Lang(View view) {
        mLocale=new Locale("mk");
        setContextLocale(this, mLocale);
    }

    public void Malay_Lang(View view) {
        mLocale=new Locale("ms");
        setContextLocale(this, mLocale);
    }

    public void Dutch_Lang(View view) {
        mLocale=new Locale("nl");
        setContextLocale(this, mLocale);
    }

    public void Norwegian_Lang(View view) {
        mLocale=new Locale("nb","NO");
        setContextLocale(this, mLocale);
    }

    public void Polish_Lang(View view) {
        mLocale=new Locale("pl");
        setContextLocale(this, mLocale);
    }

    public void Pashto_Lang(View view) {
        mLocale=new Locale("ps");
        setContextLocale(this, mLocale);
    }

    public void Portuguese_Lang(View view) {
        mLocale=new Locale("pt");
        setContextLocale(this, mLocale);
    }

    public void Portuguese_Brazil_Lang(View view) {
        mLocale=new Locale("pt","BR");
        setContextLocale(this, mLocale);
    }

    public void Romanian_Lang(View view) {
        mLocale=new Locale("ro");
        setContextLocale(this, mLocale);
    }

    public void Russian_Lang(View view) {
        mLocale=new Locale("ru");
        setContextLocale(this, mLocale);
    }

    public void Sindhi_Lang(View view) {
        mLocale=new Locale("sd");
        setContextLocale(this, mLocale);
    }

    public void Slovenian_Lang(View view) {
        mLocale=new Locale("sl");
        setContextLocale(this, mLocale);
    }

    public void Serbian_CzechRepublic_Lang(View view) {
        mLocale=new Locale("sr","CS");
        setContextLocale(this, mLocale);
    }

    public void Serbian_Serbia_Lang(View view) {
        mLocale=new Locale("sr","SP");
        setContextLocale(this, mLocale);
    }

    public void Swedish_Lang(View view) {
        mLocale=new Locale("sv");
        setContextLocale(this, mLocale);
    }

    public void Tamil_Lang(View view) {
        mLocale=new Locale("ta");
        setContextLocale(this, mLocale);
    }

    public void Telugu_Lang(View view) {
        mLocale=new Locale("te");
        setContextLocale(this, mLocale);
    }

    public void Thai_Lang(View view) {
        mLocale=new Locale("th");
        setContextLocale(this, mLocale);
    }

    public void Turkish_Lang(View view) {
        mLocale=new Locale("tr");
        setContextLocale(this, mLocale);
    }

    public void Urdu_Pakistan_Lang(View view) {
        mLocale=new Locale("ur","PK");
        setContextLocale(this,mLocale);

    }

    public void Vietnamese_Lang(View view) {
        mLocale=new Locale("vi");
        setContextLocale(this, mLocale);
    }

    public void Chinese_China_Lang(View view) {
        mLocale=new Locale("zh","CN");
        setContextLocale(this, mLocale);
    }

    public void Chinese_Taiwan_Lang(View view) {
        mLocale=new Locale("zh","TW");
        setContextLocale(this, mLocale);
    }

    //set the locale (lang,country) configurations according to the selected one
    public void setContextLocale(Context context, Locale mLocale) {
        Locale.setDefault(mLocale);
        Resources resources = context.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Configuration conf = resources.getConfiguration();
        conf.locale = mLocale;
        conf.setLayoutDirection(mLocale);
        resources.updateConfiguration(conf, displayMetrics);
        RefreshForApp();//Restart the application for loading the new language configuration
    }

    public void RefreshForApp() {
        Intent intent = new Intent(MultilanguageActivity.this, MainActivity.class);
        startActivity(intent);
        finishAffinity();
    }
}





