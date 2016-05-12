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
 * Created by Aiman Ayyal Awwad on 2/22/2015.
 */
public class BrushPickerDialogLocalizationTest extends BaseIntegrationTestClass {
    TextView mStrokeTextView;
    TextView mWidthTextView;
    SeekBar mStrokeWidthSeekBar;
    RadioButton mRectRadioButton;
    RadioButton mCircleRadioButton;
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
        mSolo.clickOnRadioButton(0);
        mSolo.clickOnButton(mSolo.getString(R.string.done));
        PointF point = new PointF(mCurrentDrawingSurfaceBitmap.getWidth() / 2,
                mCurrentDrawingSurfaceBitmap.getHeight() / 2);

        mSolo.clickOnScreen(point.x, point.y);
    }

    public void testPreconditions() {
        mSolo.clickOnView(mMenuBottomParameter1);
        assertNotNull(mSolo.searchText(mSolo.getString(R.string.dialog_shape_text)));
        assertNotNull(mSolo.searchText(mSolo.getString(R.string.dialog_brush_width_text)));
        mSolo.goBack();
    }

    public void testNoMissingTranslationInBrushPickerDialog() {
        mSolo.clickOnView(mMenuBottomParameter1);
        mStrokeTextView = (TextView) mSolo.getView(R.id.stroke_width_shape_text);
        mWidthTextView = (TextView) mSolo.getView(R.id.stroke_width_text);
        assertNotNull(mStrokeTextView );
        assertNotNull(mWidthTextView);
    }

    public void testVisibilityOfBrushPickerDialogElements()
    {
        mSolo.clickOnView(mMenuBottomParameter1);
        mStrokeTextView = (TextView) mSolo.getView(R.id.stroke_width_shape_text);
        mWidthTextView = (TextView) mSolo.getView(R.id.stroke_width_text);
        assertEquals(mStrokeTextView.getVisibility(), View.VISIBLE); // Assert text is displayed
        assertEquals(mWidthTextView.getVisibility(), View.VISIBLE); // Assert text is displayed

    }

    public void testWrapContentAndMatchParentForViewsLayout() {
        mSolo.clickOnView(mMenuBottomParameter1);
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

    public void testLocaleOfViewsText() {
        mSolo.clickOnView(mMenuBottomParameter1);
        mStrokeTextView = (TextView) mSolo.getView(R.id.stroke_width_shape_text);
        mWidthTextView = (TextView) mSolo.getView(R.id.stroke_width_text);
        Locale locale=new Locale("ar");
        String localeAsString=mWidthTextView.getTextLocale().toString();
        String languageStr=localeAsString.substring(0,2);
        String failMsg="The Direction of Stroke TextView is Left-to-Right";
        assertEquals(failMsg, languageStr,locale.toString());
        mSolo.goBack();
    }

    public void testRTLDirectionForSeekBar() {
        mSolo.clickOnView(mMenuBottomParameter1);
        mStrokeWidthSeekBar = (SeekBar) mSolo.getView(R.id.stroke_width_seek_bar);
        int StrokeWidth = 60;
        mSolo.setProgressBar(0, StrokeWidth);
        final int expected=View.LAYOUT_DIRECTION_RTL;
        String failMsg="The Direction of Stroke TextView is Left-to-Right";
        assertEquals(failMsg, mStrokeWidthSeekBar.getLayoutDirection(),expected);
    }

    public void testRTLMarginForTextViews() {
        mSolo.clickOnView(mMenuBottomParameter1);
        mWidthTextView = (TextView) mSolo.getView(R.id.stroke_width_text);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mWidthTextView .getLayoutParams();
        assertNotNull("There is no getMarginStart value for Stroke Width", params.getMarginStart());
        assertNotNull("There is no getMarginStart value for Stroke Shape", params.getMarginEnd());
        mSolo.goBack();

    }

    public void testRTLMarginForSeekBar() {
        mSolo.clickOnView(mMenuBottomParameter1);
         mStrokeWidthSeekBar = (SeekBar) mSolo.getView(R.id.stroke_width_seek_bar);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mStrokeWidthSeekBar .getLayoutParams();
        String failMsg="There is no MarginStart for RTL";
        assertNotNull(failMsg, params.getMarginStart());
        assertNotNull(failMsg, params.getMarginEnd());
        mSolo.goBack();
    }

    public void testRTLMarginForRadioButton() {
        mSolo.clickOnView(mMenuBottomParameter1);
         mRectRadioButton = (RadioButton) mSolo.getView(R.id.stroke_rbtn_rect);
         mCircleRadioButton = (RadioButton) mSolo.getView(R.id.stroke_rbtn_circle);
        ViewGroup.MarginLayoutParams params1 = (ViewGroup.MarginLayoutParams) mRectRadioButton.getLayoutParams();
        ViewGroup.MarginLayoutParams params2 = (ViewGroup.MarginLayoutParams) mCircleRadioButton.getLayoutParams();
        String failMsg="There is no MarginEnd for RTL Layout";
        assertNotNull(failMsg, params1.getMarginEnd());
        assertNotNull(failMsg, params2.getMarginStart());
        assertNotNull(failMsg, params2.getMarginEnd());
        mSolo.goBack();
    }

    public void testRTLMarginForImageButton() {
        mSolo.clickOnView(mMenuBottomParameter1);
        mRectImageButton = (ImageButton) mSolo.getView(R.id.stroke_ibtn_rect);
        ViewGroup.MarginLayoutParams params1 = (ViewGroup.MarginLayoutParams) mRectImageButton.getLayoutParams();
        String failMsg="There is no MarginEnd for RTL Layout";
        assertNotNull(failMsg, params1.getMarginEnd());
        mSolo.goBack();
    }

    public void testRightAlignmentForTextView() {
        mSolo.clickOnView(mMenuBottomParameter1);
        mStrokeTextView = (TextView) mSolo.getView(R.id.stroke_width_shape_text);
        mWidthTextView = (TextView) mSolo.getView(R.id.stroke_width_text);
        final int margin = 0;
        assertRightAligned(mStrokeTextView , mWidthTextView , margin);
    }

    public void testBottomAlignmentForRadioButton() {
         mSolo.clickOnView(mMenuBottomParameter1);
         mRectRadioButton = (RadioButton) mSolo.getView(R.id.stroke_rbtn_rect);
         mCircleRadioButton = (RadioButton) mSolo.getView(R.id.stroke_rbtn_circle);
        final int margin = 0;
        assertBottomAligned(mRectRadioButton , mCircleRadioButton , margin);
    }

    public void testUserInterfaceElementsOnScreenForBrushPickerDialog() {
        mSolo.clickOnView(mMenuBottomParameter1);
        mStrokeTextView = (TextView) mSolo.getView(R.id.stroke_width_shape_text);
        mWidthTextView = (TextView) mSolo.getView(R.id.stroke_width_text);
        mStrokeWidthSeekBar = (SeekBar) mSolo.getView(R.id.stroke_width_seek_bar);
        mRectRadioButton = (RadioButton) mSolo.getView(R.id.stroke_rbtn_rect);
        mCircleRadioButton = (RadioButton) mSolo.getView(R.id.stroke_rbtn_circle);
        mRectImageButton = (ImageButton) mSolo.getView(R.id.stroke_ibtn_rect);
        final View origin = mWidthTextView .getRootView();
        assertOnScreen(origin, mRectRadioButton );
        assertOnScreen(origin, mCircleRadioButton );
        assertOnScreen(origin, mStrokeTextView );
        assertOnScreen(origin, mWidthTextView );
        assertOnScreen(origin, mStrokeWidthSeekBar );
    }


    public void testRTLDirectionForTextViews() {
        mSolo.clickOnView(mMenuBottomParameter1);
        mStrokeTextView = (TextView) mSolo.getView(R.id.stroke_width_shape_text);
        mWidthTextView = (TextView) mSolo.getView(R.id.stroke_width_text);
        final int expected=View.TEXT_DIRECTION_LOCALE;
        String failMsg="The Direction of  TextView is Left-to-Right";
        assertEquals(failMsg, mStrokeTextView.getTextDirection(),expected);
        assertEquals(failMsg, mWidthTextView.getTextDirection(),expected);
        mSolo.goBack();
    }



}
