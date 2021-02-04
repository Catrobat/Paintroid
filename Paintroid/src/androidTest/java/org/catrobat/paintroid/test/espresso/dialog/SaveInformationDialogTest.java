package org.catrobat.paintroid.test.espresso.dialog;

import android.content.res.Resources;
import android.graphics.PointF;
import android.util.DisplayMetrics;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.common.Constants;
import org.catrobat.paintroid.common.MainActivityConstants;
import org.catrobat.paintroid.dialog.SaveInformationDialog;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;


@RunWith(JUnit4.class)
public class SaveInformationDialogTest{

    @Rule
    private MainActivity mainActivity;
    int permissionCode = MainActivityConstants.PERMISSION_EXTERNAL_STORAGE_SAVE_COPY;
    int imageNumber = 0;
    SaveInformationDialog dialog = SaveInformationDialog.newInstance(permissionCode, imageNumber, true);

    public SaveInformationDialogTest(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }


    @Before
    public void setUp() {

        dialog.show(mainActivity.getSupportFragmentManager(), Constants.ABOUT_DIALOG_FRAGMENT_TAG);
    }


    @Test
    public void testDialogIsShown() {
            onView(withId(R.id.save_info_dialog))
                    .check(matches(isDisplayed()));
    }


    @Test
    public void testDialogIsNotCancelable() {

            DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
            PointF point = new PointF((float) -metrics.widthPixels / 4, (float) -metrics.heightPixels / 4);
            onView(withId(R.id.save_info_dialog))
                    .perform(touchAt(point))
                    .check(matches(isDisplayed()));

    }
    

    @Test
    public void testDialogIsNotCancelableOnBack() {

            pressBack();
            onView(withId(R.id.save_info_dialog))
                    .check(matches(isDisplayed()));

    }



    @After
    public void tearDown() {
        dialog.dismiss();
    }
}