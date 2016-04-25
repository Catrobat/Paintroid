package org.catrobat.paintroid.test.integration;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.BidiFormatter;
import android.util.DisplayMetrics;
import android.util.LayoutDirection;
import android.widget.ImageButton;

import org.catrobat.paintroid.R;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Locale;

/**
 * Created by Aiman Ayyal Awwad on 2/24/2015.
 */
public class MainActivityLocalizationTest extends BaseIntegrationTestClass {
    Context mContext;
    Resources res;
    private static final BidiFormatter LTR_FMT = BidiFormatter.getInstance(false /* LTR context */);
    private static final BidiFormatter RTL_FMT = BidiFormatter.getInstance(true /* RTL context */);
    private static final String EN = "abba";
    private static final String AR = "أيمن";


    public MainActivityLocalizationTest() throws Exception {
        super();
    }


    public void testABCLanguageInterface() {
        String buttonLanguage = getActivity().getString(R.string.menu_language_settings);
        clickOnMenuItem(buttonLanguage);
        mSolo.clickOnRadioButton(0);
        mSolo.clickOnButton(mSolo.getString(R.string.done));
        PointF point = new PointF(mCurrentDrawingSurfaceBitmap.getWidth() / 2,
                mCurrentDrawingSurfaceBitmap.getHeight() / 2);
        mSolo.clickOnScreen(point.x, point.y);
    }

    public void testLanguageOfLayoutIsArabic() {
        String language = Locale.getDefault().getLanguage();
        assertEquals(language, "ar");
    }


    public void testPreconditions() {
        assertNotNull(getActivity().getString(R.string.menu_new_image));
        assertNotNull(getActivity().getString(R.string.menu_about));
        assertNotNull(getActivity().getString(R.string.terms_of_use_and_service_content));
        assertNotNull(getActivity().getString(R.string.menu_save_image));
        assertNotNull(getActivity().getString(R.string.menu_save_copy));
    }

    public void testUndoIsLoadedCorrectly() {
        ImageButton undoButton = (ImageButton) mSolo.getView(R.id.btn_top_undo);
        Bitmap bmpUndo = ((BitmapDrawable) undoButton.getDrawable()).getBitmap();
        Drawable mDrawable = getActivity().getResources().getDrawable(R.drawable.icon_menu_undo_disabled);
        ImageButton undoActual = new ImageButton(getActivity());
        undoActual.setImageDrawable(mDrawable);
        Bitmap bmpUndoAct = ((BitmapDrawable) undoActual.getDrawable()).getBitmap();
        assertSame(bmpUndo, bmpUndoAct);
    }

    public void testRedoIsLoadedCorrectly() {
        ImageButton redoButton = (ImageButton) mSolo.getView(R.id.btn_top_redo);
        Bitmap bmpUndo = ((BitmapDrawable) redoButton.getDrawable()).getBitmap();
        Drawable mDrawable = getActivity().getResources().getDrawable(R.drawable.icon_menu_redo_disabled);
        ImageButton undoActual = new ImageButton(getActivity());
        undoActual.setImageDrawable(mDrawable);
        Bitmap bmpUndoAct = ((BitmapDrawable) undoActual.getDrawable()).getBitmap();
        assertSame(bmpUndo, bmpUndoAct);
    }


    public void testMirroringOfUndoIsTheSameAsOfRedo() {
        ImageButton redoButton1 = (ImageButton) mSolo.getView(R.id.btn_top_redo);
        Bitmap bitmap1 = ((BitmapDrawable) redoButton1.getDrawable()).getBitmap();
        mSolo.clickOnView(mButtonTopRedo);
        ImageButton redoButton2 = (ImageButton) mSolo.getView(R.id.btn_top_undo);
        Bitmap bitmap2 = ((BitmapDrawable) redoButton2.getDrawable()).getBitmap();
        Bitmap mirroredBitmap = doMirroring(bitmap2);
        assertTrue(imagesAreEquals(bitmap1, mirroredBitmap));
    }


    public void testMirroringOfRedoIsTheSameAsUndo() {
        PointF point = new PointF(mCurrentDrawingSurfaceBitmap.getWidth() / 2,
                mCurrentDrawingSurfaceBitmap.getHeight() / 2);
        mSolo.clickOnScreen(point.x, point.y);
        ImageButton undoButton = (ImageButton) mSolo.getView(R.id.btn_top_undo);
        Bitmap bmpUndo = ((BitmapDrawable) undoButton.getDrawable()).getBitmap();
        Drawable mDrawable = getActivity().getResources().getDrawable(R.drawable.icon_menu_redo);
        ImageButton undoActual = new ImageButton(getActivity());
        undoActual.setImageDrawable(mDrawable);
        Bitmap bmpUndoAct = ((BitmapDrawable) undoActual.getDrawable()).getBitmap();
        Bitmap mirroredBitmap = doMirroring(bmpUndoAct);
        assertTrue(imagesAreEquals(bmpUndo, mirroredBitmap));
    }

    public boolean imagesAreEquals(Bitmap bitmap1, Bitmap bitmap2) {
        ByteBuffer buffer1 = ByteBuffer.allocate(bitmap1.getHeight() * bitmap1.getRowBytes());
        bitmap1.copyPixelsToBuffer(buffer1);
        ByteBuffer buffer2 = ByteBuffer.allocate(bitmap2.getHeight() * bitmap2.getRowBytes());
        bitmap2.copyPixelsToBuffer(buffer2);
        return Arrays.equals(buffer1.array(), buffer2.array());

    }

    public Bitmap doMirroring(Bitmap bitmap) {
        float[] mirrorY = {-1, 0, 0,
                0, 1, 0,
                0, 0, 1
        };
        Matrix matrixMirrorY = new Matrix();
        matrixMirrorY.setValues(mirrorY);
        Matrix matrix = new Matrix();
        matrix.postConcat(matrixMirrorY);
        Bitmap mirroredBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        return mirroredBitmap;

    }

    public void testOrientationForMainActivityLayoutIsRightToLeft() {

        Locale locale = Locale.getDefault();
        assertTrue(isRTL(locale));
        assertEquals(getOrientation(locale), LayoutDirection.RTL);
    }

    public static boolean isRTL(Locale locale) {
        final int directionality = Character.getDirectionality(locale.getDisplayName().charAt(0));
        return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT ||
                                  directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC;
    }

    public static int getOrientation(Locale locale) {

        String language = locale.getLanguage();
        if ("ar".equals(language) || "he".equals(language)
                || "fa".equals(language) || "ur".equals(language)) {
            return LayoutDirection.RTL;
        } else {
            return LayoutDirection.LTR;
        }
    }

    public void testExistanceOfDefaultResourceFile() {
        boolean Exist = false;
        Context mContext = getActivity().getBaseContext();
        Resources res = getActivity().getResources();
        int checkExistenceForString = mContext.getResources().getIdentifier("app_name", "string",
                                                                       mContext.getPackageName());
        int checkExistenceForStyle = mContext.getResources().getIdentifier("CustomPaintroidDialog",
                                                               "style", mContext.getPackageName());
        if (checkExistenceForString != 0 && checkExistenceForStyle!=0) {
         // The resouce exists...
            Exist = true;
        } else {
         // checkExistence == 0  // The resouce does NOT exist!!
            Exist = false;
        }
        assertTrue(Exist);
        String actual =res.getString(checkExistenceForString);
        String expected=mSolo.getString(R.string.app_name);
        assertEquals(actual,expected);
    }

    public void testDisplayedStringSameAsResourceFile() {
        boolean Exist = false;
        Context mContext = getActivity().getBaseContext();
        res = getActivity().getResources();
        int checkExistenceForString = mContext.getResources().getIdentifier("button_brush", "string", mContext.getPackageName());
        if (checkExistenceForString != 0 ) {  // the resouce exists...
            Exist = true;
        } else {  // checkExistence == 0  // the resouce does NOT exist!!
            Exist = false;
        }
        assertTrue(Exist);
        String actual = res.getString(checkExistenceForString);
        String expected = mSolo.getString(R.string.button_brush);
        assertEquals(actual, expected);
    }


    public void testFallBackForDefaultLanguage() {
       // String buttonLanguage = getActivity().getString(R.string.menu_language_settings);
       // clickOnMenuItem(buttonLanguage);
       // mSolo.clickOnRadioButton(15);
        setLocale(Locale.CHINA);
        String applicationStr=mSolo.getString(R.string.app_name);
        assertEquals(applicationStr,"Pocket Paint");
    }


    public void testIsRTLContext() {
        assertEquals(false, LTR_FMT.isRtlContext());
        assertEquals(true, RTL_FMT.isRtlContext());
        assertEquals(false, BidiFormatter.getInstance(Locale.ENGLISH).isRtlContext());
        assertEquals(true, BidiFormatter.getInstance(true).isRtlContext());
    }


    public void testIsRTL() {
        assertEquals(true, BidiFormatter.getInstance(true).isRtl(AR));
        assertEquals(true, BidiFormatter.getInstance(false).isRtl(AR));
        assertEquals(false, BidiFormatter.getInstance(true).isRtl(EN));
        assertEquals(false, BidiFormatter.getInstance(false).isRtl(EN));
    }


    public void testBuilderIsRtlContext() {
        assertEquals(false, new BidiFormatter.Builder(false).build().isRtlContext());
        assertEquals(true, new BidiFormatter.Builder(true).build().isRtlContext());
    }

    public void setLocale(Locale locale) {
        Locale myLocale = locale;
        Resources res = getActivity().getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

    }
}
