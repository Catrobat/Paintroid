package org.catrobat.paintroid;

import android.content.Context;
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

import java.util.ArrayList;
import java.util.Locale;

import static org.catrobat.paintroid.PaintroidApplication.SHARED_PREFERENCES_LANGUAGE_TAG;
import static org.catrobat.paintroid.PaintroidApplication.defaultSystemLanguage;
import static org.catrobat.paintroid.PaintroidApplication.languageSharedPreferences;


public class Multilingual extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multilingual_activity);
        setTitle(R.string.menu_language);
        ListView listview = (ListView) findViewById(R.id.list_Languages);

        String[] languages = getResources().getStringArray(R.array.Languages_items);
        ArrayList<String> languageNames = new ArrayList<>();
        final ArrayList<String> languageTags = new ArrayList<>();
        int languageNameIndex = 0;
        int languageTagIndex = 1;
        for (String language : languages) {
            String[] separated = language.split(",");
            languageNames.add(separated[languageNameIndex]);
            if (separated.length > languageTagIndex) {
                languageTags.add(separated[languageTagIndex]);
            } else {
                languageTags.add(defaultSystemLanguage);
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        R.layout.multilingual_language_name, R.id.lang_text, languageNames);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String languageTag = languageTags.get(position);
                setLocale(languageTag);
                SharedPreferences.Editor editor = languageSharedPreferences.edit();
                editor.clear().apply();
                if (!languageTag.equals(defaultSystemLanguage)) {
                    editor.putString(SHARED_PREFERENCES_LANGUAGE_TAG, languageTag);
                }
                editor.commit();
            }
        });

    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
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
        setResult(RESULT_OK);
        finish();
    }
}
