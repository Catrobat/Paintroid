package org.catrobat.paintroid.test.integration;


import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.Perspective;
import org.junit.Before;

public class LandscapeTest extends BaseIntegrationTestClass {


    public LandscapeTest() throws Exception {
        super();
    }


    public void testBottomBarPosition() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
            IllegalAccessException {

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        LinearLayout mainBottomBar = (LinearLayout) mSolo.getView(R.id.main_bottom_bar);

        Point size = new Point();
        mSolo.getCurrentActivity().getWindowManager().getDefaultDisplay().getSize(size);

        int bottomBarWidth = mainBottomBar.getWidth();
        int bottomBarHeight = mainBottomBar.getHeight();
        int coordinates[] = new int[2];
        mainBottomBar.getLocationOnScreen(coordinates);
        int bottomBarX = coordinates[0];
        int bottomBarY = coordinates[1];
        int screenWidth = size.x;
        int screenHeight = size.y;

        assertEquals(screenWidth - bottomBarWidth, bottomBarX);
        assertEquals(screenHeight - bottomBarHeight, bottomBarY);

    }


    public void testTopBarPosition() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
            IllegalAccessException{

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        LinearLayout mainToolbar = (LinearLayout) mSolo.getView(R.id.toolbar_container);

        Point size = new Point();
        mSolo.getCurrentActivity().getWindowManager().getDefaultDisplay().getSize(size);

        int toolBarWidth = mainToolbar.getWidth();
        int toolBarHeight = mainToolbar.getHeight();
        int coordinates[] = new int[2];
        mainToolbar.getLocationOnScreen(coordinates);
        int toolBarY = coordinates[1];
        int toolbarRight = mainToolbar.getRight();
        int screenHeight = size.y;

        assertEquals(toolBarWidth, toolbarRight);
        assertEquals(screenHeight - toolBarHeight, toolBarY);

    }

}
