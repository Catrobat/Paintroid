package org.catrobat.paintroid.dialog;

import android.content.res.Resources;
import android.graphics.PointF;
import android.os.Build;
import android.util.DisplayMetrics;

import androidx.annotation.RequiresApi;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.common.Constants;
import org.catrobat.paintroid.common.MainActivityConstants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class SaveInformationDialogTest {


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
        //test code here
    }

    @Test
    public void testDialogIsNotCancelable() {
        //test code here
    }

    @Test
    public void testDialogIsNotCancelableOnBack() {
        //test code here
    }

    @After
    public void tearDown() {
        dialog.dismiss();
    }
}