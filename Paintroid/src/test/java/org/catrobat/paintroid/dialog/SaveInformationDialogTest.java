package org.catrobat.paintroid.dialog;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.common.Constants;
import org.catrobat.paintroid.common.MainActivityConstants;
import org.junit.After;
import org.junit.Before;
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



    @After
    public void tearDown() {
        dialog.dismiss();
    }
}