package org.catrobat.paintroid.test.integration.dialog;

import android.graphics.PointF;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.integration.BaseIntegrationTestClass;
import org.junit.After;
import org.junit.Before;

import java.util.Locale;

import static android.test.ViewAsserts.assertBottomAligned;
import static android.test.ViewAsserts.assertOnScreen;
import static android.test.ViewAsserts.assertRightAligned;

/**
 * Created by dell on 2/22/2015.
 */
public class BrushPickerDialogLocalizationTest extends BaseIntegrationTestClass {
    TextView mStrokeTextView;
    TextView mWidthTextView;
    SeekBar mStrokeWidthSeekBar;
    RadioButton mRectRadionButton;
    RadioButton mCircleRadionButton;
    ImageButton mRectImageButton;
    public BrushPickerDialogLocalizationTest() throws Exception {
        super();
    }

    @Override
    @Before
    protected void setUp() {
        super.setUp();
    }

    @Override
    @After
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testABCLanguageInterface() {
        String buttonLanguage = getActivity().getString(R.string.menu_language_settings);
        clickOnMenuItem(buttonLanguage);
        mSolo.sleep(500);
        mSolo.clickOnRadioButton(0);
        mSolo.clickOnButton(mSolo.getString(R.string.done));
        PointF point = new PointF(mCurrentDrawingSurfaceBitmap.getWidth() / 2,
                mCurrentDrawingSurfaceBitmap.getHeight() / 2);

        mSolo.clickOnScreen(point.x, point.y);
    }

    public void testPreconditions() {
        mSolo.clickOnView(mMenuBottomParameter1);
        mSolo.sleep(2000);
        assertNotNull(mSolo.searchText(mSolo.getString(R.string.dialog_shape_text)));
        assertNotNull(mSolo.searchText(mSolo.getString(R.string.dialog_brush_width_text)));
        mSolo.goBack();
    }

    public void testMissingTranslation() {
        mSolo.clickOnView(mMenuBottomParameter1);
        mSolo.sleep(2000);
        mStrokeTextView = (TextView) mSolo.getView(R.id.stroke_width_shape_text);
        mWidthTextView = (TextView) mSolo.getView(R.id.stroke_width_text);
        assertNotNull(mStrokeTextView );
        assertNotNull(mWidthTextView);
    }

    public void testVisibility()
    {
        mSolo.clickOnView(mMenuBottomParameter1);
        mSolo.sleep(2000);
        mStrokeTextView = (TextView) mSolo.getView(R.id.stroke_width_shape_text);
        mWidthTextView = (TextView) mSolo.getView(R.id.stroke_width_text);
        assertEquals(mStrokeTextView.getVisibility(), View.VISIBLE); // Assert text is displayed
        assertEquals(mWidthTextView.getVisibility(), View.VISIBLE); // Assert text is displayed

    }

    public void testViewsLayout() {
        mSolo.clickOnView(mMenuBottomParameter1);
        mSolo.sleep(2000);
        mStrokeTextView = (TextView) mSolo.getView(R.id.stroke_width_shape_text);
        final ViewGroup.LayoutParams layoutParamsStroke = mStrokeTextView.getLayoutParams();
        mWidthTextView = (TextView) mSolo.getView(R.id.stroke_width_text);
        final ViewGroup.LayoutParams layoutParamsWidth = mWidthTextView.getLayoutParams();
        assertNotNull(layoutParamsStroke);
        assertNotNull(layoutParamsWidth);
        assertEquals(layoutParamsStroke.width, WindowManager.LayoutParams.MATCH_PARENT);
        assertEquals(layoutParamsStroke.height, WindowManager.LayoutParams.WRAP_CONTENT);
        assertEquals(layoutParamsWidth.width, WindowManager.LayoutParams.MATCH_PARENT);
        assertEquals(layoutParamsWidth.height, WindowManager.LayoutParams.WRAP_CONTENT);
        mSolo.goBack();
    }

    public void testViewsTextLocale() {
        mSolo.clickOnView(mMenuBottomParameter1);
        mSolo.sleep(2000);
        mStrokeTextView = (TextView) mSolo.getView(R.id.stroke_width_shape_text);
        mWidthTextView = (TextView) mSolo.getView(R.id.stroke_width_text);
        Locale locale=new Locale("ar");
        String localeAsString=mWidthTextView.getTextLocale().toString();
        String languagestr=localeAsString.substring(0,2);
        String failMsg="The Direction of Stroke TextView is Left-to-Right";
        assertEquals(failMsg, languagestr,locale.toString());
        mSolo.goBack();
    }

    public void testSeekBarRTLDirection() {
        mSolo.clickOnView(mMenuBottomParameter1);
        mSolo.sleep(2000);
        mStrokeWidthSeekBar = (SeekBar) mSolo.getView(R.id.stroke_width_seek_bar);
        int StrokeWidth = 60;
        mSolo.setProgressBar(0, StrokeWidth);
        final int expected=View.LAYOUT_DIRECTION_RTL;
        String failMsg="The Direction of Stroke TextView is Left-to-Right";
        assertEquals(failMsg, mStrokeWidthSeekBar.getLayoutDirection(),expected);
    }

    public void testTextViewsMarginForRTL() {
        mSolo.clickOnView(mMenuBottomParameter1);
        mSolo.sleep(2000);
        mWidthTextView = (TextView) mSolo.getView(R.id.stroke_width_text);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mWidthTextView .getLayoutParams();
        assertNotNull("There is no getMarginStart value for Stroke Width", params.getMarginStart());
        assertNotNull("There is no getMarginStart value for Stroke Shape", params.getMarginEnd());
        mSolo.goBack();

    }

    public void testSeekBarMarginForRTL() {
        mSolo.clickOnView(mMenuBottomParameter1);
        mSolo.sleep(2000);
         mStrokeWidthSeekBar = (SeekBar) mSolo.getView(R.id.stroke_width_seek_bar);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mStrokeWidthSeekBar .getLayoutParams();
        String failMsg="There is no MarginStart for RTL";
        assertNotNull(failMsg, params.getMarginStart());
        assertNotNull(failMsg, params.getMarginEnd());
        mSolo.goBack();
    }

    public void testRadioButtonMarginRTL() {
        mSolo.clickOnView(mMenuBottomParameter1);
        mSolo.sleep(2000);
         mRectRadionButton = (RadioButton) mSolo.getView(R.id.stroke_rbtn_rect);
         mCircleRadionButton = (RadioButton) mSolo.getView(R.id.stroke_rbtn_circle);
        ViewGroup.MarginLayoutParams params1 = (ViewGroup.MarginLayoutParams) mRectRadionButton.getLayoutParams();
        ViewGroup.MarginLayoutParams params2 = (ViewGroup.MarginLayoutParams) mCircleRadionButton.getLayoutParams();
        String failMsg="There is no MarginEnd for RTL Layout";
        assertNotNull(failMsg, params1.getMarginEnd());
        assertNotNull(failMsg, params2.getMarginStart());
        assertNotNull(failMsg, params2.getMarginEnd());
        mSolo.goBack();
    }

    public void testImageButtonMarginRTL() {
        mSolo.clickOnView(mMenuBottomParameter1);
        mSolo.sleep(2000);
        mRectImageButton = (ImageButton) mSolo.getView(R.id.stroke_ibtn_rect);
        ViewGroup.MarginLayoutParams params1 = (ViewGroup.MarginLayoutParams) mRectImageButton.getLayoutParams();
        String failMsg="There is no MarginEnd for RTL Layout";
        assertNotNull(failMsg, params1.getMarginEnd());
        mSolo.goBack();
    }

    public void testRightAlignment() {
        mSolo.clickOnView(mMenuBottomParameter1);
        mSolo.sleep(2000);
        mStrokeTextView = (TextView) mSolo.getView(R.id.stroke_width_shape_text);
        mWidthTextView = (TextView) mSolo.getView(R.id.stroke_width_text);
        final int margin = 0;
        assertRightAligned(mStrokeTextView , mWidthTextView , margin);
    }

    public void testBottomAlignment() {
        mSolo.clickOnView(mMenuBottomParameter1);
        mSolo.sleep(2000);
         mRectRadionButton = (RadioButton) mSolo.getView(R.id.stroke_rbtn_rect);
         mCircleRadionButton = (RadioButton) mSolo.getView(R.id.stroke_rbtn_circle);
        final int margin = 0;
        assertBottomAligned(mRectRadionButton , mCircleRadionButton , margin);
    }

    public void testUserInterfaceLayout() {
        mSolo.clickOnView(mMenuBottomParameter1);
        mSolo.sleep(2000);
        mStrokeTextView = (TextView) mSolo.getView(R.id.stroke_width_shape_text);
        mWidthTextView = (TextView) mSolo.getView(R.id.stroke_width_text);
        mStrokeWidthSeekBar = (SeekBar) mSolo.getView(R.id.stroke_width_seek_bar);
        mRectRadionButton = (RadioButton) mSolo.getView(R.id.stroke_rbtn_rect);
        mCircleRadionButton = (RadioButton) mSolo.getView(R.id.stroke_rbtn_circle);
        mRectImageButton = (ImageButton) mSolo.getView(R.id.stroke_ibtn_rect);
        final View origin = mWidthTextView .getRootView();
        assertOnScreen(origin, mRectRadionButton );
        assertOnScreen(origin, mCircleRadionButton );
        assertOnScreen(origin, mStrokeTextView );
        assertOnScreen(origin, mWidthTextView );
        assertOnScreen(origin, mStrokeWidthSeekBar );
    }


    public void testViewsTextDirection() {
        mSolo.clickOnView(mMenuBottomParameter1);
        mSolo.sleep(2000);
        mStrokeTextView = (TextView) mSolo.getView(R.id.stroke_width_shape_text);
        mWidthTextView = (TextView) mSolo.getView(R.id.stroke_width_text);
        final int expected=View.TEXT_DIRECTION_LOCALE;
        String failMsg="The Direction of  TextView is Left-to-Right";
        assertEquals(failMsg, mStrokeTextView.getTextDirection(),expected);
        assertEquals(failMsg, mWidthTextView.getTextDirection(),expected);

        mSolo.goBack();
    }



}
