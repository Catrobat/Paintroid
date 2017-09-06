package org.catrobat.paintroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Locale;

import static org.catrobat.paintroid.PaintroidApplication.defaultSystemLanguage;
import static org.catrobat.paintroid.PaintroidApplication.languageSharedPreferences;


public class Multilingual extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multilingual_activity);
        setTitle(R.string.menu_language);
        ListView listview = (ListView) findViewById(R.id.list_Languages);
        String[] Languages = getResources().getStringArray(R.array.Languages_items);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.multilingual_language_name, R.id.lang_text, Languages);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    setLocale(defaultSystemLanguage);
                    SharedPreferences.Editor editor = languageSharedPreferences.edit();
                    editor.clear().apply();
                    editor.commit();
                }
                if (position == 1) {
                    setLocale("az-rAZ");
                    SharedPreferences.Editor editor = languageSharedPreferences.edit();
                    editor.clear().apply();
                    editor.putString("Nur", "az-rAZ");
                    editor.commit();

                }
                if (position == 2) {
                    setLocale("bs");
                    SharedPreferences.Editor editor = languageSharedPreferences.edit();
                    editor.clear().apply();
                    editor.putString("Nur", "bs");
                    editor.commit();
                }
                if (position == 3) {
                    setLocale("ca-rES");
                    SharedPreferences.Editor editor = languageSharedPreferences.edit();
                    editor.clear().apply();
                    editor.putString("Nur", "ca-rES");
                    editor.commit();
                }
                if (position == 4) {
                    setLocale("cs-rCZ");
                    SharedPreferences.Editor editor = languageSharedPreferences.edit();
                    editor.clear().apply();
                    editor.putString("Nur", "cs-rCZ");
                    editor.commit();
                }
                if (position == 5) {
                    setLocale("sr-rCS");
                    SharedPreferences.Editor editor = languageSharedPreferences.edit();
                    editor.clear().apply();
                    editor.putString("Nur", "sr-rCS");
                    editor.commit();
                }
                if (position == 6) {
                    setLocale("sr-rSP");
                    SharedPreferences.Editor editor = languageSharedPreferences.edit();
                    editor.clear().apply();
                    editor.putString("Nur", "sr-rSP");
                    editor.commit();
                }
                if (position == 7) {
                    setLocale("da");
                    SharedPreferences.Editor editor = languageSharedPreferences.edit();
                    editor.clear().apply();
                    editor.putString("Nur", "da");
                    editor.commit();
                }
                if (position == 8) {
                    setLocale("de");
                    SharedPreferences.Editor editor = languageSharedPreferences.edit();
                    editor.clear().apply();
                    editor.putString("Nur", "de");
                    editor.commit();
                }
                if (position == 9) {
                    setLocale("en-rAU");
                    SharedPreferences.Editor editor = languageSharedPreferences.edit();
                    editor.clear().apply();
                    editor.putString("Nur", "en-rAU");
                    editor.commit();
                }
                if (position == 10) {
                    setLocale("en-rCA");
                    SharedPreferences.Editor editor = languageSharedPreferences.edit();
                    editor.clear().apply();
                    editor.putString("Nur", "en-rCA");
                    editor.commit();
                }
                if (position == 11) {
                    setLocale("en-rGB");
                    SharedPreferences.Editor editor = languageSharedPreferences.edit();
                    editor.clear().apply();
                    editor.putString("Nur", "en-rGB");
                    editor.commit();
                }
                if (position == 12) {
                    setLocale("es");
                    SharedPreferences.Editor editor = languageSharedPreferences.edit();
                    editor.clear().apply();
                    editor.putString("Nur", "es");
                    editor.commit();
                }
                if (position == 13) {
                    setLocale("fr");
                    SharedPreferences.Editor editor = languageSharedPreferences.edit();
                    editor.clear().apply();
                    editor.putString("Nur", "fr");
                    editor.commit();
                }
                if (position == 14) {
                    setLocale("gl-rES");
                    SharedPreferences.Editor editor = languageSharedPreferences.edit();
                    editor.clear().apply();
                    editor.putString("Nur", "gl-rES");
                    editor.commit();
                }
                if (position == 15) {
                    setLocale("hr");
                    SharedPreferences.Editor editor = languageSharedPreferences.edit();
                    editor.clear().apply();
                    editor.putString("Nur", "hr");
                    editor.commit();
                }
                if (position == 16) {
                    setLocale("id");
                    SharedPreferences.Editor editor = languageSharedPreferences.edit();
                    editor.clear().apply();
                    editor.putString("Nur", "id");
                    editor.commit();
                }
                if (position == 17) {
                    setLocale("it");
                    SharedPreferences.Editor editor = languageSharedPreferences.edit();
                    editor.clear().apply();
                    editor.putString("Nur", "it");
                    editor.commit();
                }
                if (position == 18) {
                    setLocale("hu");
                    SharedPreferences.Editor editor = languageSharedPreferences.edit();
                    editor.clear().apply();
                    editor.putString("Nur", "hu");
                    editor.commit();
                }
                if (position == 19) {
                    setLocale("mk");
                    SharedPreferences.Editor editor = languageSharedPreferences.edit();
                    editor.clear().apply();
                    editor.putString("Nur", "mk");
                    editor.commit();
                }
                if (position == 20) {
                    setLocale("ms");
                    SharedPreferences.Editor editor = languageSharedPreferences.edit();
                    editor.clear().apply();
                    editor.putString("Nur", "ms");
                    editor.commit();
                }
                if (position == 21) {
                    setLocale("nl");
                    SharedPreferences.Editor editor = languageSharedPreferences.edit();
                    editor.clear().apply();
                    editor.putString("Nur", "nl");
                    editor.commit();
                }
                if (position == 22) {
                    setLocale("no");
                    SharedPreferences.Editor editor = languageSharedPreferences.edit();
                    editor.clear().apply();
                    editor.putString("Nur", "no");
                    editor.commit();
                }
                if (position == 23) {
                    setLocale("pl");
                    SharedPreferences.Editor editor = languageSharedPreferences.edit();
                    editor.clear().apply();
                    editor.putString("Nur", "pl");
                    editor.commit();
                }
                if (position == 24) {
                    setLocale("pt-rBR");
                    SharedPreferences.Editor editor = languageSharedPreferences.edit();
                    editor.clear().apply();
                    editor.putString("Nur", "pt-rBR");
                    editor.commit();
                }
                if (position == 25) {
                    setLocale("pt");
                    SharedPreferences.Editor editor = languageSharedPreferences.edit();
                    editor.clear().apply();
                    editor.putString("Nur", "pt");
                    editor.commit();
                }
                if (position == 26) {
                    setLocale("ru");
                    SharedPreferences.Editor editor = languageSharedPreferences.edit();
                    editor.clear().apply();
                    editor.putString("Nur", "ru");
                    editor.commit();
                }
                if (position == 27) {
                    setLocale("ro");
                    SharedPreferences.Editor editor = languageSharedPreferences.edit();
                    editor.clear().apply();
                    editor.putString("Nur", "ro");
                    editor.commit();
                }
                if (position == 28) {
                    setLocale("sq");
                    SharedPreferences.Editor editor = languageSharedPreferences.edit();
                    editor.clear().apply();
                    editor.putString("Nur", "sq");
                    editor.commit();
                }
                if (position == 29) {
                    setLocale("sl");
                    SharedPreferences.Editor editor = languageSharedPreferences.edit();
                    editor.clear().apply();
                    editor.putString("Nur", "sl");
                    editor.commit();
                }
                if (position == 30) {
                    setLocale("sv");
                    SharedPreferences.Editor editor = languageSharedPreferences.edit();
                    editor.clear().apply();
                    editor.putString("Nur", "sv");
                    editor.commit();
                }
                if (position == 31) {
                    setLocale("vi");
                    SharedPreferences.Editor editor = languageSharedPreferences.edit();
                    editor.clear().apply();
                    editor.putString("Nur", "vi");
                    editor.commit();
                }
                if (position == 32) {
                    setLocale("tr");
                    SharedPreferences.Editor editor = languageSharedPreferences.edit();
                    editor.clear().apply();
                    editor.putString("Nur", "tr");
                    editor.commit();
                }
                if (position == 33) {
                    setLocale("ml");
                    SharedPreferences.Editor editor = languageSharedPreferences.edit();
                    editor.clear().apply();
                    editor.putString("Nur", "ml");
                    editor.commit();
                }
                if (position == 34) {
                    setLocale("ta");
                    SharedPreferences.Editor editor = languageSharedPreferences.edit();
                    editor.clear().apply();
                    editor.putString("Nur", "ta");
                    editor.commit();
                }
                if (position == 35) {
                    setLocale("te");
                    SharedPreferences.Editor editor = languageSharedPreferences.edit();
                    editor.clear().apply();
                    editor.putString("Nur", "te");
                    editor.commit();
                }
                if (position == 36) {
                    setLocale("th");
                    SharedPreferences.Editor editor = languageSharedPreferences.edit();
                    editor.clear().apply();
                    editor.putString("Nur", "th");
                    editor.commit();
                }
                if (position == 37) {
                    setLocale("gu");
                    SharedPreferences.Editor editor = languageSharedPreferences.edit();
                    editor.clear().apply();
                    editor.putString("Nur", "gu");
                    editor.commit();
                }
                if (position == 38) {
                    setLocale("hi");
                    SharedPreferences.Editor editor = languageSharedPreferences.edit();
                    editor.clear().apply();
                    editor.putString("Nur", "hi");
                    editor.commit();
                }
                if (position == 39) {
                    setLocale("ja");
                    SharedPreferences.Editor editor = languageSharedPreferences.edit();
                    editor.clear().apply();
                    editor.putString("Nur", "ja");
                    editor.commit();
                }
                if (position == 40) {
                    setLocale("ko");
                    SharedPreferences.Editor editor = languageSharedPreferences.edit();
                    editor.clear().apply();
                    editor.putString("Nur", "ko");
                    editor.commit();
                }
                if (position == 41) {
                    setLocale("zh-rCN");
                    SharedPreferences.Editor editor = languageSharedPreferences.edit();
                    editor.clear().apply();
                    editor.putString("Nur", "zh-rCN");
                    editor.commit();
                }
                if (position == 42) {
                    setLocale("zh-rTW");
                    SharedPreferences.Editor editor = languageSharedPreferences.edit();
                    editor.clear().apply();
                    editor.putString("Nur", "zh-rTW");
                    editor.commit();
                }
                if (position == 43) {
                    setLocale("ar");

                    SharedPreferences.Editor editor = languageSharedPreferences.edit();
                    editor.clear().apply();
                    editor.putString("Nur", "ar");
                    editor.commit();

                }
                if (position == 44) {
                    setLocale("ur");
                    SharedPreferences.Editor editor = languageSharedPreferences.edit();
                    editor.clear().apply();
                    editor.putString("Nur", "ur");
                    editor.commit();
                }
                if (position == 45) {
                    setLocale("fa");
                    SharedPreferences.Editor editor = languageSharedPreferences.edit();
                    editor.clear().apply();
                    editor.putString("Nur", "fa");
                    editor.commit();
                }
                if (position == 46) {
                    setLocale("ps");
                    SharedPreferences.Editor editor = languageSharedPreferences.edit();
                    editor.clear().apply();
                    editor.putString("Nur", "ps");
                    editor.commit();
                }
                if (position == 47) {
                    setLocale("sd");
                    SharedPreferences.Editor editor = languageSharedPreferences.edit();
                    editor.clear().apply();
                    editor.putString("Nur", "sd");
                    editor.commit();
                }
                if (position == 48) {
                    setLocale("he");
                    SharedPreferences.Editor editor = languageSharedPreferences.edit();
                    editor.clear().apply();
                    editor.putString("Nur", "he");
                    editor.commit();
                }

            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finishAffinity();
        super.onBackPressed();
    }

    static public void setContextLocale(Context context, String lang) {
        Locale Language = new Locale(lang);
        Resources resources = context.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Configuration conf = resources.getConfiguration();
        conf.locale = Language;
        Language.setDefault(Language);
        conf.setLayoutDirection(Language);
        resources.updateConfiguration(conf, displayMetrics);

    }

    public void setLocale(String lang) {
        setContextLocale(this, lang);
        Intent intent = new Intent(Multilingual.this, MainActivity.class);
        startActivity(intent);
        finishAffinity();
    }
}
