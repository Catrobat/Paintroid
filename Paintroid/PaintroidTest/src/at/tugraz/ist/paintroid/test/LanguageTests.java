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
	}
		
		
	private Solo solo;
	private MainActivity mainActivity;
	
	public void testEnglish(){
		Log.d("PaintroidTest","Current language: " + Locale.getDefault().getDisplayLanguage());
		solo.clickOnImageButton(7);
		if(Locale.getDefault().getDisplayLanguage().equals("Deutsch")){
			solo.clickOnButton("Abbrechen");
		}

		String languageToLoad  = "en";
		Locale locale = new Locale(languageToLoad);
		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.locale = locale;
		//wie updaten???? -.-"
		Log.d("PaintroidTest","Current language: " + Locale.getDefault().getDisplayLanguage());
		
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.clickOnImageButton(7);
		solo.clickOnButton("Cancel");


	}
	
	
}
