package org.catrobat.paintroid.test.integration.dialog;

import android.graphics.PointF;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.integration.BaseIntegrationTestClass;
import org.catrobat.paintroid.tools.ToolType;

import static android.test.ViewAsserts.assertOnScreen;

/**
 * Created by Aiman Ayyal Awwad on 2/16/2015.
 */
public class ToolsDialogLocalizationTest extends BaseIntegrationTestClass {

    public ToolsDialogLocalizationTest() throws Exception {
        super();
    }

    public void testABCLanguageInterface() {

        mSolo.clickOnButton(mSolo.getString(R.string.done));
        PointF point = new PointF(mCurrentDrawingSurfaceBitmap.getWidth() / 2,
                mCurrentDrawingSurfaceBitmap.getHeight() / 2);

        mSolo.clickOnScreen(point.x, point.y);

    }

    public void testPreconditions() {
        assertNotNull(mSolo.getString(ToolType.BRUSH.getNameResource()));
        assertNotNull(mSolo.getString(ToolType.RESIZE.getNameResource()));
        assertNotNull(mSolo.getString(ToolType.CURSOR.getNameResource()));
        assertNotNull(mSolo.getString(ToolType.ELLIPSE.getNameResource()));
        assertNotNull(mSolo.getString(ToolType.ERASER.getNameResource()));
        assertNotNull(mSolo.getString(ToolType.FILL.getNameResource()));
        assertNotNull(mSolo.getString(ToolType.FLIP.getNameResource()));
        assertNotNull(mSolo.getString(ToolType.IMPORTPNG.getNameResource()));
        assertNotNull(mSolo.getString(ToolType.LINE.getNameResource()));
        assertNotNull(mSolo.getString(ToolType.MOVE.getNameResource()));
        assertNotNull(mSolo.getString(ToolType.PIPETTE.getNameResource()));
        assertNotNull(mSolo.getString(ToolType.RECT.getNameResource()));
        assertNotNull(mSolo.getString(ToolType.ROTATE.getNameResource()));
        assertNotNull(mSolo.getString(ToolType.STAMP.getNameResource()));
        assertNotNull(mSolo.getString(ToolType.ZOOM.getNameResource()));
    }

    public void testMissingTranslationForBrushTool() {
        selectTool(ToolType.BRUSH);
        String expected = mSolo.getString(ToolType.BRUSH.getNameResource());
        String actual = mSolo.getString(R.string.button_brush);
        assertEquals(actual, expected);
    }

    public void testMissingTranslationForCROPTool() {
        selectTool(ToolType.RESIZE);
        String expected = mSolo.getString(ToolType.RESIZE.getNameResource());
        String actual = mSolo.getString(R.string.button_resize);
        assertEquals(actual, expected);
    }

    public void testMissingTranslationForERASERTool() {
        selectTool(ToolType.ERASER);
        String expected = mSolo.getString(ToolType.ERASER.getNameResource());
        String actual = mSolo.getString(R.string.button_eraser);
        assertEquals(actual, expected);
    }

    public void testMissingTranslationForCURSORTool() {
        selectTool(ToolType.CURSOR);
        String expected = mSolo.getString(ToolType.CURSOR.getNameResource());
        String actual = mSolo.getString(R.string.button_cursor);
        assertEquals(actual, expected);
    }

    public void testMissingTranslationForELLIPSETool() {
        selectTool(ToolType.ELLIPSE);
        String expected = mSolo.getString(ToolType.ELLIPSE.getNameResource());
        String actual = mSolo.getString(R.string.button_ellipse);
        assertEquals(actual, expected);
    }

    public void testMissingTranslationForFILLTool() {
        selectTool(ToolType.FILL);
        String expected = mSolo.getString(ToolType.FILL.getNameResource());
        String actual = mSolo.getString(R.string.button_fill);
        assertEquals(actual, expected);
    }

    public void testMissingTranslationForFLIPTool() {
        selectTool(ToolType.FLIP);
        String expected = mSolo.getString(ToolType.FLIP.getNameResource());
        String actual = mSolo.getString(R.string.button_flip);
        assertEquals(actual, expected);
    }

    public void testMissingTranslationForIMPORTPNGTool() {
        String expected = mSolo.getString(ToolType.IMPORTPNG.getNameResource());
        String actual = mSolo.getString(R.string.button_import_image);
        assertEquals(actual, expected);
    }

    public void testMissingTranslationForLINETool() {
        selectTool(ToolType.LINE);
        String expected = mSolo.getString(ToolType.LINE.getNameResource());
        String actual = mSolo.getString(R.string.button_line);
        assertEquals(actual, expected);
    }

    public void testMissingTranslationForMOVETool() {
        selectTool(ToolType.MOVE);
        String expected = mSolo.getString(ToolType.MOVE.getNameResource());
        String actual = mSolo.getString(R.string.button_move);
        assertEquals(actual, expected);
    }

    public void testMissingTranslationForPIPETTETool() {
        selectTool(ToolType.PIPETTE);
        String expected = mSolo.getString(ToolType.PIPETTE.getNameResource());
        String actual = mSolo.getString(R.string.button_pipette);
        assertEquals(actual, expected);
    }

    public void testMissingTranslationForRECTTool() {
        selectTool(ToolType.RECT);
        String expected = mSolo.getString(ToolType.RECT.getNameResource());
        String actual = mSolo.getString(R.string.button_rectangle);
        assertEquals(actual, expected);
    }

    public void testMissingTranslationForROTATETool() {
        selectTool(ToolType.ROTATE);
        String expected = mSolo.getString(ToolType.ROTATE.getNameResource());
        String actual = mSolo.getString(R.string.button_rotate);
        assertEquals(actual, expected);
    }

    public void testMissingTranslationForSTAMPTool() {
        selectTool(ToolType.STAMP);
        String expected = mSolo.getString(ToolType.STAMP.getNameResource());
        String actual = mSolo.getString(R.string.button_stamp);
        assertEquals(actual, expected);
    }

    public void testMissingTranslationForZOOMTool() {
        selectTool(ToolType.ZOOM);
        String expected = mSolo.getString(ToolType.ZOOM.getNameResource());
        String actual = mSolo.getString(R.string.button_zoom);
        assertEquals(actual, expected);
    }

    public void testWRAPCONTENTForTextView() {
        mSolo.clickOnView(mMenuBottomTool);
        TextView mTextView = (TextView) mSolo.getView(R.id.tool_button_text);
        final ViewGroup.LayoutParams layoutParams = mTextView.getLayoutParams();
        assertNotNull(layoutParams);
        assertEquals(layoutParams.width, WindowManager.LayoutParams.WRAP_CONTENT);
        assertEquals(layoutParams.height, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    public void testViewToolsGravityAsExpected() {
        mSolo.clickOnView(mMenuBottomTool);
        TextView mTextView = (TextView) mSolo.getView(R.id.tool_button_text);
        int expected= 8388659;
        assertEquals(mTextView.getGravity(),expected);
    }

    public void testTextDirectionForToolsDialogIsRightToLeft() {
        mSolo.clickOnView(mMenuBottomTool);
        TextView mTextView = (TextView) mSolo.getView(R.id.tool_button_text);
        final int expected=View.LAYOUT_DIRECTION_RTL;
        assertEquals("The Text Direction is Left to right", mTextView.getLayoutDirection(), expected);
    }

    public void testTextDirectionForToolsDialogIsLocale()
    {
        mSolo.clickOnView(mMenuBottomTool);
        TextView mTextView = (TextView) mSolo.getView(R.id.tool_button_text);
        final int expected=View.TEXT_DIRECTION_LOCALE;
        // The paragraph direction of this view is Right to Left.
        String failMsg="The Text Direction is Left to right";
        assertEquals(failMsg, mTextView.getTextDirection(), expected);
    }

    public void testOnScreenForToolsDialogElements() {
        MainActivity mActivity = getActivity();
        mSolo.clickOnView(mMenuBottomTool);
        TextView mTextView = (TextView) mSolo.getView(R.id.tool_button_text);
        ImageView mImageView=(ImageView) mSolo.getView(R.id.tool_button_image);
        final View origin = mActivity.getWindow().getDecorView();
        assertOnScreen(origin,mTextView);
        assertOnScreen(origin,mImageView);
    }

    public void testToolsDialogElementsIsCorrectlyMirroredForT()
    {
        mSolo.clickOnView(mMenuBottomTool);
        TextView mTextView = (TextView) mSolo.getView(R.id.tool_button_text);
        ImageView mImageView=(ImageView) mSolo.getView(R.id.tool_button_image);
        String failMsg="Image should be right of Text";
        assertTrue(failMsg,mTextView.getRight()<= mImageView.getLeft());
    }


    public void testTextViewInToolsDialogIsNotTruncated() {
        mSolo.clickOnView(mMenuBottomTool);
        TextView mTextView = (TextView) mSolo.getView(R.id.tool_button_text);
        String st= String.valueOf(R.string.button_brush);
        assertFalse(isTruncatedText(st, mTextView));
    }


    public static boolean isTruncatedText( String text, TextView textView )
    {   boolean Ellipsis_State=false; int ellipsisCount=0;
        if ( textView != null && text != null )
        {
            Layout layout = textView.getLayout();
            if ( layout != null )
            {
                int lines = layout.getLineCount();
                if ( lines > 0 )
                {
                    for(int line=0;line<lines;line++)
                    {
                        ellipsisCount = layout.getEllipsisCount( line );
                        if ( ellipsisCount > 0 ) {
                            Ellipsis_State=true;
                            break;
                        }
                    }
                }
            }
        }
        return Ellipsis_State;
    }

}


