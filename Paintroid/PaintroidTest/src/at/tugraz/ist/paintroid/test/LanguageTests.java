package at.tugraz.ist.paintroid.test;

import java.util.Locale;

import android.content.res.Configuration;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import at.tugraz.ist.paintroid.MainActivity;

import com.jayway.android.robotium.solo.Solo;


public class LanguageTests extends ActivityInstrumentationTestCase2<MainActivity>{
	
	public LanguageTests() {
		super("at.tugraz.ist.paintroid", MainActivity.class);
	}

	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
		
		String languageToLoad_before  = "de";
		Locale locale_before = new Locale(languageToLoad_before);
		Locale.setDefault(locale_before);
		
		Configuration config_before = new Configuration();
		config_before.locale = locale_before;
		
		mainActivity = (MainActivity) solo.getCurrentActivity();
		mainActivity.getBaseContext().getResources().updateConfiguration(config_before, mainActivity.getBaseContext().getResources().getDisplayMetrics());
		
		solo = new Solo(getInstrumentation(), getActivity());
		mainActivity = (MainActivity) solo.getCurrentActivity();
	}
	
	final int FILE = 8;
		
	private Solo solo;
	private MainActivity mainActivity;
	
	public void testEnglish(){
		Log.d("PaintroidTest","Current language: " + Locale.getDefault().getDisplayLanguage());
		
		assertEquals("Deutsch", Locale.getDefault().getDisplayLanguage());
		
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("Abbrechen");

		String languageToLoad_after  = "en";
		Locale locale_after = new Locale(languageToLoad_after);
		Locale.setDefault(locale_after);
		
		Configuration config_after = new Configuration();
		config_after.locale = locale_after;
		
		mainActivity = (MainActivity) solo.getCurrentActivity();
		mainActivity.getBaseContext().getResources().updateConfiguration(config_after, mainActivity.getBaseContext().getResources().getDisplayMetrics());
		
		solo = new Solo(getInstrumentation(), getActivity());
		mainActivity = (MainActivity) solo.getCurrentActivity();
		
		Log.d("PaintroidTest","Current language: " + Locale.getDefault().getDisplayLanguage());
		
		assertEquals("English", Locale.getDefault().getDisplayLanguage());
		
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("Cancel");

	}
	
	public void testFrench(){
		Log.d("PaintroidTest","Current language: " + Locale.getDefault().getDisplayLanguage());
		
		assertEquals("Deutsch", Locale.getDefault().getDisplayLanguage());
		
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("Abbrechen");

		String languageToLoad_after  = "fr";
		Locale locale_after = new Locale(languageToLoad_after);
		Locale.setDefault(locale_after);
		
		Configuration config_after = new Configuration();
		config_after.locale = locale_after;
		
		mainActivity = (MainActivity) solo.getCurrentActivity();
		mainActivity.getBaseContext().getResources().updateConfiguration(config_after, mainActivity.getBaseContext().getResources().getDisplayMetrics());
		
		solo = new Solo(getInstrumentation(), getActivity());
		mainActivity = (MainActivity) solo.getCurrentActivity();
		
		Log.d("PaintroidTest","Current language: " + Locale.getDefault().getDisplayLanguage());
		
		assertEquals("français", Locale.getDefault().getDisplayLanguage());
		
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("Annuler");
	}
	
	public void testGerman(){
		String languageToLoad_before  = "fr";
		Locale locale_before = new Locale(languageToLoad_before);
		Locale.setDefault(locale_before);
		
		Configuration config_before = new Configuration();
		config_before.locale = locale_before;
		
		mainActivity = (MainActivity) solo.getCurrentActivity();
		mainActivity.getBaseContext().getResources().updateConfiguration(config_before, mainActivity.getBaseContext().getResources().getDisplayMetrics());
		
		solo = new Solo(getInstrumentation(), getActivity());
		mainActivity = (MainActivity) solo.getCurrentActivity();
		
		Log.d("PaintroidTest","Current language: " + Locale.getDefault().getDisplayLanguage());
		
		assertEquals("français", Locale.getDefault().getDisplayLanguage());
		
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("Annuler");

		String languageToLoad_after  = "de";
		Locale locale_after = new Locale(languageToLoad_after);
		Locale.setDefault(locale_after);
		
		Configuration config_after = new Configuration();
		config_after.locale = locale_after;
		
		mainActivity = (MainActivity) solo.getCurrentActivity();
		mainActivity.getBaseContext().getResources().updateConfiguration(config_after, mainActivity.getBaseContext().getResources().getDisplayMetrics());
		
		solo = new Solo(getInstrumentation(), getActivity());
		mainActivity = (MainActivity) solo.getCurrentActivity();
		
		Log.d("PaintroidTest","Current language: " + Locale.getDefault().getDisplayLanguage());
		
		assertEquals("Deutsch", Locale.getDefault().getDisplayLanguage());
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("Abbrechen");
	}
	
}
