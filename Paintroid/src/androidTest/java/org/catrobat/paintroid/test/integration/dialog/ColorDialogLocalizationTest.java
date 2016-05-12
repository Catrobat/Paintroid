package org.catrobat.paintroid.test.integration.dialog;

import android.graphics.PointF;
import android.util.LayoutDirection;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.integration.BaseIntegrationTestClass;

import static android.test.ViewAsserts.assertBottomAligned;
import static android.test.ViewAsserts.assertOnScreen;
import static android.test.ViewAsserts.assertRightAligned;
import static android.test.ViewAsserts.assertTopAligned;

/**
 * Created by Aiman Ayyal Awwad on 2/24/2015.
 */


public class ColorDialogLocalizationTest extends BaseIntegrationTestClass {
    TextView mRedTextView;
    SeekBar mRedSeekBar;
    TextView mRedValueTextView;
    TextView mGreenTextView;
    SeekBar mGreenSeekBar;
    TextView mGreenValueTextView;
    TextView mBlueTextView;
    SeekBar mBlueSeekBar;
    TextView mBlueValueTextView;
    TextView mAlphaTextView;
    SeekBar mAlphaSeekBar;
    TextView mAlphaValueTextView;
    final int RGB_TAB_INDEX = 1;
    float downXValue;
    float UpXValue;

    public ColorDialogLocalizationTest() throws Exception {
        super();

    }

   public void testABCLanguageInterface()
    {
        String buttonLanguage = getActivity().getString(R.string.menu_language_settings);
        clickOnMenuItem(buttonLanguage);
        mSolo.clickOnRadioButton(0);
        mSolo.clickOnButton(mSolo.getString(R.string.done));
        PointF point = new PointF(mCurrentDrawingSurfaceBitmap.getWidth() / 2,
                mCurrentDrawingSurfaceBitmap.getHeight() / 2);

        mSolo.clickOnScreen(point.x, point.y);

    }

    public void testPreconditions()
    {

        mSolo.clickOnView(mButtonTopColor);
        TabHost tabHost = (TabHost) mSolo.getView(R.id.colorview_tabColors);
        TabWidget colorTabWidget = tabHost.getTabWidget();
        mSolo.clickOnView(colorTabWidget.getChildAt(RGB_TAB_INDEX), true);
        assertNotNull(mSolo.searchText(mSolo.getString(R.string.color_red)));
        assertNotNull(mSolo.searchText(mSolo.getString(R.string.color_green)));
        assertNotNull(mSolo.searchText(mSolo.getString(R.string.color_blue)));
        mSolo.goBack();
    }

    public void testNoMissingTranslationInColorDialog()
    {

        mSolo.clickOnView(mButtonTopColor);
        TabHost tabHost = (TabHost) mSolo.getView(R.id.colorview_tabColors);
        TabWidget colorTabWidget = tabHost.getTabWidget();
        mSolo.clickOnView(colorTabWidget.getChildAt(RGB_TAB_INDEX), true);
        assertTrue("In rgb tab the red string is Missing", mSolo.searchText(mSolo.getString(R.string.color_red)));
        assertTrue("In rgb tab and green string is Missing",mSolo.searchText(mSolo.getString(R.string.color_green)));
        assertTrue("In rgb tab and blue string is Missing",mSolo.searchText(mSolo.getString(R.string.color_blue)));
        mSolo.goBack();

    }


    public void testFillParentForViewRedLayout()
    {
        mSolo.clickOnView(mButtonTopColor);
        TabHost tabHost = (TabHost) mSolo.getView(R.id.colorview_tabColors);
        TabWidget colorTabWidget = tabHost.getTabWidget();
        mSolo.clickOnView(colorTabWidget.getChildAt(RGB_TAB_INDEX), true);
        mRedTextView= (TextView) mSolo.getView(R.id.color_rgb_textview_red);
        final ViewGroup.LayoutParams layoutParams=mRedTextView.getLayoutParams();
        assertNotNull(layoutParams);
        assertEquals(layoutParams.width, WindowManager.LayoutParams.FILL_PARENT);
        assertEquals(layoutParams.height, WindowManager.LayoutParams.FILL_PARENT);
        mSolo.goBack();

    }

    public void testJustificationForRGBaTextViewsIsCenterVertical()
    {
        mSolo.clickOnView(mButtonTopColor);
        TabHost tabHost = (TabHost) mSolo.getView(R.id.colorview_tabColors);
        TabWidget colorTabWidget = tabHost.getTabWidget();
        mSolo.clickOnView(colorTabWidget.getChildAt(RGB_TAB_INDEX), true);
        mRedTextView= (TextView) mSolo.getView(R.id.color_rgb_textview_red);
        mGreenTextView= (TextView) mSolo.getView(R.id.color_rgb_textview_green);
        mBlueTextView= (TextView) mSolo.getView(R.id.color_rgb_textview_blue);
        mAlphaTextView= (TextView) mSolo.getView(R.id.color_rgb_textview_alpha);
        final int expected=Gravity.START |Gravity.CENTER_VERTICAL;
        assertEquals(mRedTextView.getGravity(),expected);
        assertEquals(mGreenTextView.getGravity(),expected);
        assertEquals(mBlueTextView.getGravity(),expected);
        assertEquals(mAlphaTextView.getGravity(),expected);
        mSolo.goBack();
    }


    public void testFillParentForRGBaTextViews()
    {
        mSolo.clickOnView(mButtonTopColor);
        TabHost tabHost = (TabHost) mSolo.getView(R.id.colorview_tabColors);
        TabWidget colorTabWidget = tabHost.getTabWidget();
        mSolo.clickOnView(colorTabWidget.getChildAt(RGB_TAB_INDEX), true);
        mRedTextView= (TextView) mSolo.getView(R.id.color_rgb_textview_red);
        mGreenTextView= (TextView) mSolo.getView(R.id.color_rgb_textview_green);
        mBlueTextView= (TextView) mSolo.getView(R.id.color_rgb_textview_blue);
        mAlphaTextView= (TextView) mSolo.getView(R.id.color_rgb_textview_alpha);
        final ViewGroup.LayoutParams redTextViewLayoutParams=mRedTextView.getLayoutParams();
        final ViewGroup.LayoutParams greenTextViewLayoutParams=mGreenTextView.getLayoutParams();
        final ViewGroup.LayoutParams blueTextViewLayoutParams=mBlueTextView.getLayoutParams();
        final ViewGroup.LayoutParams alphaTextViewLayoutParams=mAlphaTextView.getLayoutParams();
        assertNotNull(redTextViewLayoutParams);
        assertNotNull(greenTextViewLayoutParams);
        assertNotNull(blueTextViewLayoutParams);
        assertNotNull(alphaTextViewLayoutParams);
        assertEquals(redTextViewLayoutParams.width, WindowManager.LayoutParams.FILL_PARENT);
        assertEquals(redTextViewLayoutParams.height, WindowManager.LayoutParams.FILL_PARENT);
        assertEquals(greenTextViewLayoutParams.width, WindowManager.LayoutParams.FILL_PARENT);
        assertEquals(greenTextViewLayoutParams.height, WindowManager.LayoutParams.FILL_PARENT);
        assertEquals(blueTextViewLayoutParams.width, WindowManager.LayoutParams.FILL_PARENT);
        assertEquals(blueTextViewLayoutParams.height, WindowManager.LayoutParams.FILL_PARENT);
        assertEquals(alphaTextViewLayoutParams.width, WindowManager.LayoutParams.FILL_PARENT);
        assertEquals(alphaTextViewLayoutParams.height, WindowManager.LayoutParams.FILL_PARENT);

    }

    public void testGravityAndPaddingForRGBValuesViews()
    {
        mSolo.clickOnView(mButtonTopColor);
        TabHost tabHost = (TabHost) mSolo.getView(R.id.colorview_tabColors);
        TabWidget colorTabWidget = tabHost.getTabWidget();
        mSolo.clickOnView(colorTabWidget.getChildAt(RGB_TAB_INDEX), true);
        mRedTextView= (TextView) mSolo.getView(R.id.rgb_red_value);
        mGreenTextView= (TextView) mSolo.getView(R.id.rgb_green_value);
        mBlueTextView= (TextView) mSolo.getView(R.id.rgb_blue_value);
        mAlphaTextView= (TextView) mSolo.getView(R.id.rgb_alpha_value);
        final int expected=Gravity.CENTER_VERTICAL + Gravity.END;
        String  failMsg="The layout_gravity is not CENTER_VERTICAL|end";
        assertEquals(failMsg,mRedTextView.getGravity(),expected);
        assertEquals(failMsg,mGreenTextView.getGravity(),expected);
        assertEquals(failMsg,mBlueTextView.getGravity(),expected);
        assertEquals(failMsg,mAlphaTextView.getGravity(),expected);
        assertEquals(mGreenTextView.getPaddingRight(),0);
        assertEquals(mRedTextView.getPaddingRight(),0);
        assertEquals(mBlueTextView.getPaddingRight(),0);
        assertEquals(mAlphaTextView.getPaddingRight(),0);

    }

    public void testNoMissingTranslationForDoneButton()
    {
        mSolo.clickOnView(mButtonTopColor);
        String failMsg="In rgb tab the red string is Missing";
        assertTrue(failMsg, mSolo.searchText(mSolo.getString(R.string.done)));
    }

    public void testGravityOfDoneButtonIsCenter()
    {
        mSolo.clickOnView(mButtonTopColor);
        Button mButton= (Button) mSolo.getView(R.id.btn_colorchooser_ok);
        final ViewGroup.LayoutParams layoutParams=mButton.getLayoutParams();
        assertNotNull(layoutParams);
        assertEquals(layoutParams.width, WindowManager.LayoutParams.FILL_PARENT);
        final int expected= Gravity.CENTER;
        String failMsg="Button Done is not in the Center";
        assertEquals(failMsg,mButton.getGravity(),expected);
        mSolo.goBack();

    }

    public void testTextDirectionForRGBaTextViewsIsRightToLeft()
    {
        mSolo.clickOnView(mButtonTopColor);
        TabHost tabHost = (TabHost) mSolo.getView(R.id.colorview_tabColors);
        TabWidget colorTabWidget = tabHost.getTabWidget();
        mSolo.clickOnView(colorTabWidget.getChildAt(RGB_TAB_INDEX), true);
         mRedTextView= (TextView) mSolo.getView(R.id.color_rgb_textview_red);
        mGreenTextView= (TextView) mSolo.getView(R.id.color_rgb_textview_green);
        mBlueTextView= (TextView) mSolo.getView(R.id.color_rgb_textview_blue);
        mAlphaTextView= (TextView) mSolo.getView(R.id.color_rgb_textview_alpha);
        final int expected= LayoutDirection.RTL;
        // The paragraph direction of this view is Right to Left.
        String failMsg="The Direction of Red TextView is Left-to-Right";
        assertEquals(failMsg,mRedTextView.getLayoutDirection(),expected);
        assertEquals(failMsg,mGreenTextView.getLayoutDirection(),expected);
        assertEquals(failMsg,mBlueTextView.getLayoutDirection(),expected);
        assertEquals(failMsg,mAlphaTextView.getLayoutDirection(),expected);
        mSolo.goBack();
    }


    public void testAlignmentForRGBaTextViewsIsRight() {
        final int margin = 0;
        mSolo.clickOnView(mButtonTopColor);
        TabHost tabHost = (TabHost) mSolo.getView(R.id.colorview_tabColors);
        TabWidget colorTabWidget = tabHost.getTabWidget();
        mSolo.clickOnView(colorTabWidget.getChildAt(RGB_TAB_INDEX), true);
        mRedTextView= (TextView) mSolo.getView(R.id.color_rgb_textview_red);
        mGreenTextView= (TextView) mSolo.getView(R.id.color_rgb_textview_green);
        mBlueTextView= (TextView) mSolo.getView(R.id.color_rgb_textview_blue);
        mAlphaTextView= (TextView) mSolo.getView(R.id.color_rgb_textview_alpha);
        mRedValueTextView= (TextView) mSolo.getView(R.id. rgb_red_value);
        mGreenValueTextView= (TextView) mSolo.getView(R.id. rgb_green_value);
        mBlueValueTextView= (TextView) mSolo.getView(R.id. rgb_blue_value);
        mAlphaValueTextView= (TextView) mSolo.getView(R.id. rgb_alpha_value);
        assertRightAligned(mRedTextView, mGreenTextView, margin);
        assertRightAligned(mBlueTextView, mAlphaTextView, margin);
        assertRightAligned(mRedValueTextView, mGreenValueTextView,margin);
        assertRightAligned(mBlueValueTextView, mAlphaValueTextView,margin);
    }


    public void testTopAlignmentForRGBaTextViews()
    {
        final int margin=0;
        mSolo.clickOnView(mButtonTopColor);
        TabHost tabHost = (TabHost) mSolo.getView(R.id.colorview_tabColors);
        TabWidget colorTabWidget = tabHost.getTabWidget();
        mSolo.clickOnView(colorTabWidget.getChildAt(RGB_TAB_INDEX), true);
         mRedTextView= (TextView) mSolo.getView(R.id.color_rgb_textview_red);
         mGreenTextView= (TextView) mSolo.getView(R.id.color_rgb_textview_green);
         mBlueTextView= (TextView) mSolo.getView(R.id.color_rgb_textview_blue);
         mAlphaTextView= (TextView) mSolo.getView(R.id.color_rgb_textview_alpha);
         mRedValueTextView= (TextView) mSolo.getView(R.id. rgb_red_value);
         mGreenValueTextView= (TextView) mSolo.getView(R.id. rgb_green_value);
         mBlueValueTextView= (TextView) mSolo.getView(R.id. rgb_blue_value);
         mAlphaValueTextView= (TextView) mSolo.getView(R.id. rgb_alpha_value);
        assertTopAligned(mRedTextView, mRedValueTextView, margin);
        assertTopAligned(mGreenTextView, mGreenValueTextView, margin);
        assertTopAligned(mBlueTextView, mBlueValueTextView, margin);
        assertTopAligned(mAlphaTextView, mAlphaValueTextView, margin);

    }

    public void testUserInterfaceElementsIsOnScreen() {
        MainActivity mActivity = getActivity();
        mSolo.clickOnView(mButtonTopColor);
        TabHost tabHost = (TabHost) mSolo.getView(R.id.colorview_tabColors);
        TabWidget colorTabWidget = tabHost.getTabWidget();
        mSolo.clickOnView(colorTabWidget.getChildAt(RGB_TAB_INDEX), true);
         mRedSeekBar= (SeekBar) mSolo.getView(R.id.color_rgb_seekbar_red);
         mGreenSeekBar= (SeekBar) mSolo.getView(R.id.color_rgb_seekbar_green);
         mBlueSeekBar= (SeekBar) mSolo.getView(R.id.color_rgb_seekbar_blue);
         mAlphaSeekBar= (SeekBar) mSolo.getView(R.id.color_rgb_seekbar_alpha);
         mRedTextView= (TextView) mSolo.getView(R.id.color_rgb_textview_red);
         mGreenTextView= (TextView) mSolo.getView(R.id.color_rgb_textview_green);
         mBlueTextView= (TextView) mSolo.getView(R.id.color_rgb_textview_blue);
         mAlphaTextView= (TextView) mSolo.getView(R.id.color_rgb_textview_alpha);
         mRedValueTextView= (TextView) mSolo.getView(R.id. rgb_red_value);
         mGreenValueTextView= (TextView) mSolo.getView(R.id. rgb_green_value);
         mBlueValueTextView= (TextView) mSolo.getView(R.id. rgb_blue_value);
         mAlphaValueTextView= (TextView) mSolo.getView(R.id. rgb_alpha_value);
        final View origin = mActivity.getWindow().getDecorView();
        assertOnScreen(origin, mRedSeekBar);
        assertOnScreen(origin, mGreenSeekBar);
        assertOnScreen(origin, mBlueSeekBar);
        assertOnScreen(origin, mAlphaSeekBar);
        assertOnScreen(origin, mRedTextView);
        assertOnScreen(origin, mGreenTextView);
        assertOnScreen(origin, mBlueTextView);
        assertOnScreen(origin, mAlphaTextView);
        assertOnScreen(origin, mRedValueTextView);
        assertOnScreen(origin, mGreenValueTextView);
        assertOnScreen(origin, mBlueValueTextView);
        assertOnScreen(origin, mAlphaValueTextView);

    }

    public void testBottomAlignmentForTextViews() {
        mSolo.clickOnView(mButtonTopColor);
        TabHost tabHost = (TabHost) mSolo.getView(R.id.colorview_tabColors);
        TabWidget colorTabWidget = tabHost.getTabWidget();
        mSolo.clickOnView(colorTabWidget.getChildAt(RGB_TAB_INDEX), true);
         mRedTextView= (TextView) mSolo.getView(R.id.color_rgb_textview_red);
         mRedValueTextView= (TextView) mSolo.getView(R.id. rgb_red_value);
        SeekBar mRedSeekBar= (SeekBar) mSolo.getView(R.id.color_rgb_seekbar_red);
        final int margin = 0;
        assertBottomAligned(mRedTextView , mRedValueTextView , margin);
        assertBottomAligned(mRedTextView , mRedSeekBar , margin);
    }

    public void testTextDirectionForRGBaTextViewsIsLocaleDirection()
    {
        mSolo.clickOnView(mButtonTopColor);
        TabHost tabHost = (TabHost) mSolo.getView(R.id.colorview_tabColors);
        TabWidget colorTabWidget = tabHost.getTabWidget();
        mSolo.clickOnView(colorTabWidget.getChildAt(RGB_TAB_INDEX), true);
        mRedTextView= (TextView) mSolo.getView(R.id.color_rgb_textview_red);
        mGreenTextView= (TextView) mSolo.getView(R.id.color_rgb_textview_green);
        mBlueTextView= (TextView) mSolo.getView(R.id.color_rgb_textview_blue);
        mAlphaTextView= (TextView) mSolo.getView(R.id.color_rgb_textview_alpha);
        final int expected=View.TEXT_DIRECTION_LOCALE;
        assertEquals(mRedTextView.getTextDirection(),expected);
        assertEquals(mGreenTextView.getTextDirection(),expected);
        assertEquals(mBlueTextView.getTextDirection(),expected);
        assertEquals(mAlphaTextView.getTextDirection(),expected);

    }

    public void testNoOverLappingInColorDialog()
    {
        mSolo.clickOnView(mButtonTopColor);
        TabHost tabHost = (TabHost) mSolo.getView(R.id.colorview_tabColors);
        TabWidget colorTabWidget = tabHost.getTabWidget();
        mSolo.clickOnView(colorTabWidget.getChildAt(RGB_TAB_INDEX), true);
         mRedTextView= (TextView) mSolo.getView(R.id.color_rgb_textview_red);
         mRedSeekBar= (SeekBar) mSolo.getView(R.id.color_rgb_seekbar_red);
         mRedValueTextView= (TextView) mSolo.getView(R.id.rgb_red_value);
         mGreenTextView= (TextView) mSolo.getView(R.id.color_rgb_textview_green);
         mGreenSeekBar= (SeekBar) mSolo.getView(R.id.color_rgb_seekbar_green);
         mGreenValueTextView= (TextView) mSolo.getView(R.id. rgb_green_value);
         mBlueTextView= (TextView) mSolo.getView(R.id.color_rgb_textview_blue);
         mBlueSeekBar= (SeekBar) mSolo.getView(R.id.color_rgb_seekbar_blue);
         mBlueValueTextView= (TextView) mSolo.getView(R.id. rgb_blue_value);
         mAlphaTextView= (TextView) mSolo.getView(R.id.color_rgb_textview_alpha);
         mAlphaSeekBar= (SeekBar) mSolo.getView(R.id.color_rgb_seekbar_alpha);
         mAlphaValueTextView= (TextView) mSolo.getView(R.id. rgb_alpha_value);
        String failMsg="TextView should be right of SeekBar";
        assertTrue(failMsg,mRedSeekBar.getRight()<=mRedTextView.getLeft());
        assertTrue(failMsg,mRedValueTextView.getRight()<=mRedSeekBar.getLeft());
        assertTrue(failMsg,mGreenSeekBar.getRight()<=mGreenTextView.getLeft());
        assertTrue(failMsg,mGreenValueTextView.getRight()<=mGreenSeekBar.getLeft());
        assertTrue(failMsg,mBlueSeekBar.getRight()<=mBlueTextView.getLeft());
        assertTrue(failMsg,mBlueValueTextView.getRight()<=mBlueSeekBar.getLeft());
        assertTrue(failMsg,mAlphaSeekBar.getRight()<=mAlphaTextView.getLeft());
        assertTrue(failMsg,mAlphaValueTextView.getRight()<=mAlphaSeekBar.getLeft());

    }

public void testDirectionOfSeekBarIsRightToLeft()
{
    mSolo.clickOnView(mButtonTopColor);
    TabHost tabHost = (TabHost) mSolo.getView(R.id.colorview_tabColors);
    TabWidget colorTabWidget = tabHost.getTabWidget();
    mSolo.clickOnView(colorTabWidget.getChildAt(RGB_TAB_INDEX), true);
    mRedSeekBar= (SeekBar) mSolo.getView(R.id.color_rgb_seekbar_red);
    mRedValueTextView= (TextView) mSolo.getView(R.id.rgb_red_value);
    mSolo.clickLongOnView(mRedSeekBar);
    mSolo.clickLongOnView(mRedSeekBar);
    mRedSeekBar.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    });
    mRedSeekBar.setOnTouchListener(new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
            // store the X value when the user's finger was pressed down
                    downXValue = event.getX();
                    break;
                }
                case MotionEvent.ACTION_UP: {
            // Get the X value when the user released his/her finger
                    UpXValue = event.getX();
                    break;
                }
            }//end of Switch
            return true;
        }
    });
    mSolo.clickLongOnView(mRedSeekBar);
    mSolo.clickLongOnView(mRedSeekBar);
    mSolo.drag(mRedSeekBar.getLeft(),mRedSeekBar.getTop(),200,mRedSeekBar.getTop(),10);
    assertTrue(UpXValue>downXValue);
    String failMsg="The Direction of Red SeekBar is Left-to-Right";
    assertEquals(failMsg,mRedSeekBar.getLayoutDirection(),View.LAYOUT_DIRECTION_RTL);
    assertTrue(Integer.parseInt((String) mRedValueTextView.getText())>0);
}


    public void testSeekBarDirectionIsRTL() {
        mSolo.clickOnView(mButtonTopColor);
        TabHost tabHost = (TabHost) mSolo.getView(R.id.colorview_tabColors);
        TabWidget colorTabWidget = tabHost.getTabWidget();
        mSolo.clickOnView(colorTabWidget.getChildAt(RGB_TAB_INDEX), true);
        mSolo.waitForDialogToClose();
        String failMsg="The Direction of  SeekBar is Left-to-Right";
        mRedSeekBar = (SeekBar) mSolo.getView(R.id.color_rgb_seekbar_red);
        mRedSeekBar.setProgress(100);
        assertEquals(failMsg,View.LAYOUT_DIRECTION_RTL,mRedSeekBar.getLayoutDirection());
        mGreenSeekBar= (SeekBar) mSolo.getView(R.id.color_rgb_seekbar_green);
        mGreenSeekBar.setProgress(100);
        assertEquals(failMsg,View.LAYOUT_DIRECTION_RTL,mRedSeekBar.getLayoutDirection());
        mBlueSeekBar= (SeekBar) mSolo.getView(R.id.color_rgb_seekbar_blue);
        mBlueSeekBar.setProgress(100);
        assertEquals(failMsg,View.LAYOUT_DIRECTION_RTL,mRedSeekBar.getLayoutDirection());
        mAlphaSeekBar= (SeekBar) mSolo.getView(R.id.color_rgb_seekbar_alpha);
        mAlphaSeekBar.setProgress(100);
        assertEquals(failMsg,View.LAYOUT_DIRECTION_RTL,mRedSeekBar.getLayoutDirection());
        mSolo.goBack();
    }



}
