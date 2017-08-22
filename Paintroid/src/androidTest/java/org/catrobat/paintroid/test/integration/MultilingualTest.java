package org.catrobat.paintroid.test.integration;

import android.os.Build;
import android.support.annotation.RequiresApi;

import android.test.ActivityInstrumentationTestCase2;
import android.util.LayoutDirection;
import android.view.Display;


import com.robotium.solo.Solo;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.Multilingual;
import org.catrobat.paintroid.R;

import java.util.Locale;

//tested on SAMSUNG Galaxy S5
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
public class MultilingualTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public MultilingualTest() {
        super(MainActivity.class);
    }

    private Solo solo;
    private int CurrentDirection;
    private static final int LTR = LayoutDirection.LTR;
    private static final int RTL = LayoutDirection.RTL;
    private static final Locale ArabicLocale = new Locale("ar");
    private static final Locale UrduLocale = new Locale("ur");
    private static final Locale FarsiLocale = new Locale("fa");
    private static final Locale DeutschLocale = Locale.GERMAN;
    // German Strings
    private static String Save_Image_German = "Bild speichern";
    private static String Save_Copy_German = "Kopie speichern";
    private static String Load_Image_German = "Bild laden";
    private static String New_Image_German = "Neues Bild";
    private static String Fullscreen_German = "Vollbild";
    private static String Help_German = "Hilfe";
    //Arabic Strings
    private static String Save_Image_Arabic = "حفظ الصورة";
    private static String Save_Copy_Arabic = "حفظ كـ نسخة";
    private static String Load_Image_Arabic = "تحميل الصورة";
    private static String New_Image_Arabic = "صورة جديدة";
    private static String Fullscreen_Arabic = "ملء الشاشة";
    private static String Help_Arabic = "المساعدة";
    //Urdu Strings
    private static String Save_Image_Urdu = "تصویر محفوظ کریں";
    private static String Save_Copy_Urdu = "نقل محفوظ کریں.";
    private static String Load_Image_Urdu = "تصویر لادنا";
    private static String New_Image_Urdu = "نئی تصویر";
    private static String Fullscreen_Urdu = "پورا پردہ";
    private static String Help_Urdu = "مدد";
    //Farsi Strings
    private static String Save_Image_Farsi = "ذخیره تصویر";
    private static String Save_Copy_Farsi = "ذخیره رونوشت";
    private static String Load_Image_Farsi = "بارگذاری تصویر";
    private static String New_Image_Farsi = "تصویر جدید";
    private static String Fullscreen_Farsi = "تمام صفحه";
    private static String Help_Farsi = "راهنما";

    @Override
    public void setUp() throws Exception {
        solo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void testChangeLanguageToDeutsch() throws Exception {
        solo.assertCurrentActivity("Current Activity is not MainActivity", MainActivity.class);
        if (getActivity().getResources().getConfiguration().getLayoutDirection() == LayoutDirection.LTR) {
            swipeToRight();
        } else {
            swipeTo_Left();
        }

        solo.sleep(1000);
        solo.clickOnText(solo.getString(R.string.menu_language));
        solo.sleep(1000);
        solo.assertCurrentActivity("Current Activity is not Multilingual", Multilingual.class);
        solo.scrollDown();
        solo.clickInList(4); // English(UK)
        solo.sleep(1000);
        swipeToRight();
        solo.sleep(1000);
        solo.clickOnText(solo.getString(R.string.menu_language));
        solo.sleep(1000);
        solo.assertCurrentActivity("Current Activity is not Multilingual", Multilingual.class);
        solo.scrollDown();
        solo.clickInList(1); // Deutsch
        solo.sleep(2000);
        solo.assertCurrentActivity("Current Activity is not MainActivity", MainActivity.class);
        assertFalse(isRTL());
        CurrentDirection = getActivity().getResources().getConfiguration().getLayoutDirection();
        Locale CurrentLocale = Locale.getDefault();
        assertEquals("Locale is not German", CurrentLocale, DeutschLocale);
        assertEquals("LayoutDirection ist not LTR", LTR, CurrentDirection);
        swipeToRight();
        solo.sleep(2000);
        assertTrue(solo.searchText(Save_Image_German));
        assertTrue(solo.searchText(Save_Copy_German));
        assertTrue(solo.searchText(Load_Image_German));
        assertTrue(solo.searchText(New_Image_German));
        assertTrue(solo.searchText(Fullscreen_German));
        assertTrue(solo.searchText(Help_German));
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void testChangeLanguageToArabic() throws Exception {
        solo.assertCurrentActivity("Current Activity is not MainActivity", MainActivity.class);
        if (getActivity().getResources().getConfiguration().getLayoutDirection() == LayoutDirection.LTR) {
            swipeToRight();
        } else {
            swipeTo_Left();
        }
        solo.sleep(1000);
        solo.clickOnText(solo.getString(R.string.menu_language));
        solo.sleep(1000);
        solo.assertCurrentActivity("Current Activity is not Multilingual", Multilingual.class);
        solo.scrollDown();
        solo.clickInList(4); // English(UK)
        solo.sleep(1000);
        swipeToRight();
        solo.sleep(1000);
        solo.clickOnText(solo.getString(R.string.menu_language));
        solo.sleep(1000);
        solo.assertCurrentActivity("Current Activity is not Multilingual", Multilingual.class);
        solo.scrollDown();
        solo.scrollDown();
        solo.scrollDown();
        solo.scrollDown();
        solo.scrollDown();
        solo.clickInList(4); // Arabic
        solo.sleep(1000);
        solo.assertCurrentActivity("Current Activity is not MainActivity", MainActivity.class);
        assertTrue(isRTL());
        CurrentDirection = getActivity().getResources().getConfiguration().getLayoutDirection();
        Locale CurrentLocale = Locale.getDefault();
        assertEquals("Locale is not Arabic", CurrentLocale, ArabicLocale);
        assertEquals("LayoutDirection ist not RTL", RTL, CurrentDirection);
        swipeTo_Left();
        solo.sleep(2000);
        assertTrue(solo.searchText(Save_Image_Arabic));
        assertTrue(solo.searchText(Save_Copy_Arabic));
        assertTrue(solo.searchText(Load_Image_Arabic));
        assertTrue(solo.searchText(New_Image_Arabic));
        assertTrue(solo.searchText(Fullscreen_Arabic));
        assertTrue(solo.searchText(Help_Arabic));
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void testChangeLanguageToUrdu() throws Exception {
        solo.assertCurrentActivity("Current Activity is not MainActivity", MainActivity.class);
        if (getActivity().getResources().getConfiguration().getLayoutDirection() == LayoutDirection.LTR) {
            swipeToRight();
        } else {
            swipeTo_Left();
        }
        solo.sleep(1000);
        solo.clickOnText(solo.getString(R.string.menu_language));
        solo.sleep(1000);
        solo.assertCurrentActivity("Current Activity is not Multilingual", Multilingual.class);
        solo.scrollDown();
        solo.clickInList(4); // English(UK)
        solo.sleep(1000);
        swipeToRight();
        solo.sleep(1000);
        solo.clickOnText(solo.getString(R.string.menu_language));
        solo.sleep(1000);
        solo.assertCurrentActivity("Current Activity is not Multilingual", Multilingual.class);
        solo.scrollDown();
        solo.scrollDown();
        solo.scrollDown();
        solo.scrollDown();
        solo.scrollDown();
        solo.clickInList(5); // Urdu
        solo.sleep(1000);
        solo.assertCurrentActivity("Current Activity is not MainActivity", MainActivity.class);
        assertTrue(isRTL());
        CurrentDirection = getActivity().getResources().getConfiguration().getLayoutDirection();
        Locale CurrentLocale = Locale.getDefault();
        assertEquals("Locale is not Urdu", CurrentLocale, UrduLocale);
        assertEquals("LayoutDirection ist not RTL", RTL, CurrentDirection);
        swipeTo_Left();
        solo.sleep(2000);
        assertTrue(solo.searchText(Save_Image_Urdu));
        assertTrue(solo.searchText(Save_Copy_Urdu));
        assertTrue(solo.searchText(Load_Image_Urdu));
        assertTrue(solo.searchText(New_Image_Urdu));
        assertTrue(solo.searchText(Fullscreen_Urdu));
        assertTrue(solo.searchText(Help_Urdu));
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void testChangeLanguageToFarsi() throws Exception {
        solo.assertCurrentActivity("Current Activity is not MainActivity", MainActivity.class);
        if (getActivity().getResources().getConfiguration().getLayoutDirection() == LayoutDirection.LTR) {
            swipeToRight();
        } else {
            swipeTo_Left();
        }
        solo.sleep(1000);
        solo.clickOnText(solo.getString(R.string.menu_language));
        solo.sleep(1000);
        solo.assertCurrentActivity("Current Activity is not Multilingual", Multilingual.class);
        solo.scrollDown();
        solo.clickInList(4); // English(UK)
        solo.sleep(1000);
        swipeToRight();
        solo.sleep(1000);
        solo.clickOnText(solo.getString(R.string.menu_language));
        solo.sleep(1000);
        solo.assertCurrentActivity("Current Activity is not Multilingual", Multilingual.class);
        solo.scrollDown();
        solo.scrollDown();
        solo.scrollDown();
        solo.scrollDown();
        solo.scrollDown();
        solo.clickInList(6); // Farsi
        solo.sleep(1000);
        solo.assertCurrentActivity("Current Activity is not MainActivity", MainActivity.class);
        assertTrue(isRTL());
        CurrentDirection = getActivity().getResources().getConfiguration().getLayoutDirection();
        Locale CurrentLocale = Locale.getDefault();
        assertEquals("Locale is not Farsi", CurrentLocale, FarsiLocale);
        assertEquals("LayoutDirection ist not RTL", RTL, CurrentDirection);
        swipeTo_Left();
        solo.sleep(2000);
        assertTrue(solo.searchText(Save_Image_Farsi));
        assertTrue(solo.searchText(Save_Copy_Farsi));
        assertTrue(solo.searchText(Load_Image_Farsi));
        assertTrue(solo.searchText(New_Image_Farsi));
        assertTrue(solo.searchText(Fullscreen_Farsi));
        assertTrue(solo.searchText(Help_Farsi));
    }


    private void swipeToRight() {
        Display display = solo.getCurrentActivity().getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        float xStart = 0;
        float xEnd = width / 0.1F;
        solo.drag(xStart, xEnd, height / 2, height / 2, 1);
    }

    private void swipeTo_Left() {
        Display display = solo.getCurrentActivity().getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        float xStart = width - 1;
        float xEnd = width / 2;
        solo.drag(xStart, xEnd, height / 2, height / 2, 1);
    }

    public static boolean isRTL() {
        return isRTL(Locale.getDefault());
    }

    public static boolean isRTL(Locale locale) {
        final int directionality = Character.getDirectionality(locale.getDisplayName().charAt(0));
        return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT ||
                directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC;
    }

}
