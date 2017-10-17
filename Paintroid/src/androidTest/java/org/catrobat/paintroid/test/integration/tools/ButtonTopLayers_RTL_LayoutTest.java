package org.catrobat.paintroid.test.integration.tools;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.ImageButton;

import com.robotium.solo.Solo;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.junit.Ignore;

import java.util.Locale;

@Ignore
public class ButtonTopLayers_RTL_LayoutTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public ButtonTopLayers_RTL_LayoutTest() {
        super(MainActivity.class);
    }

    private Solo solo;

    @Override
    public void setUp() throws Exception {
        solo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    // Make sure your PhoneLanguage is one of the RTL Languages
    //tested on SAMSUNG GALAXY S5
    public void testButtonTopLayer() throws Exception {
        solo.assertCurrentActivity("Current Activity is not MainActivity", MainActivity.class);
        solo.sleep(500);
        assertTrue(isRTL());
        solo.sleep(500);
        ImageButton LAYERS = (ImageButton) solo.getView(R.id.btn_top_layers);
        assertTrue(LAYERS.isClickable());
        solo.clickOnView(LAYERS);
        ImageButton AddLayer = (ImageButton) solo.getView(R.id.layer_side_nav_button_add);
        ImageButton DeleteLayer = (ImageButton) solo.getView(R.id.layer_side_nav_button_delete);
        assertTrue(AddLayer.isShown());
        assertTrue(AddLayer.getVisibility() == View.VISIBLE);
        assertTrue(AddLayer.isClickable());
        assertTrue(DeleteLayer.isShown());
        assertTrue(DeleteLayer.getVisibility() == View.VISIBLE);
        assertTrue(DeleteLayer.isClickable());

    }

    private static boolean isRTL() {
        return isRTL(Locale.getDefault());
    }

    private static boolean isRTL(Locale locale) {
        final int directionality = Character.getDirectionality(locale.getDisplayName().charAt(0));
        return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT ||
                directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC;
    }
}
