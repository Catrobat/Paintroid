package org.catrobat.paintroid.test.integration;


import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.Toolbar;


import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.dialog.colorpicker.ColorPickerDialog;
import org.catrobat.paintroid.dialog.colorpicker.ColorPickerView;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.DrawingSurface;

public class LandscapeTest extends BaseIntegrationTestClass {

    private final int SCREEN_ORIENTATION_PORTRAIT = 1;
    private final int SCREEN_ORIENTATION_LANDSCAPE = 2;

    ImageButton mColorButton;

    @Override
    protected void setUp() {
        super.setUp();
        selectTool(ToolType.BRUSH);
        resetBrush();
        setOrienation(SCREEN_ORIENTATION_LANDSCAPE);
        mColorButton = (ImageButton)mSolo.getView(R.id.btn_top_color);
    }

    public LandscapeTest() throws Exception {
        super();
    }

    public void testLandscapeMode() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
            IllegalAccessException {
        setOrienation(SCREEN_ORIENTATION_PORTRAIT);
        setOrienation(SCREEN_ORIENTATION_LANDSCAPE);
        assertTrue(!mSolo.getCurrentActivity().isDestroyed());
    }

    public void testBottomBarPosition() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
        IllegalAccessException {

        LinearLayout mainBottomBar = (LinearLayout) mSolo.getView(R.id.main_bottom_bar);

        Point size = new Point();
        mSolo.getCurrentActivity().getWindowManager().getDefaultDisplay().getSize(size);
        mSolo.sleep(SHORT_TIMEOUT);

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

        LinearLayout mainToolbar = (LinearLayout) mSolo.getView(R.id.toolbar_container);

        Point size = new Point();
        mSolo.getCurrentActivity().getWindowManager().getDefaultDisplay().getSize(size);
        mSolo.sleep(SHORT_TIMEOUT);

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

    public void testToolBarOptionWidth() throws  SecurityException, IllegalArgumentException, NoSuchFieldException,
            IllegalAccessException{

        Point size = new Point();
        mSolo.getCurrentActivity().getWindowManager().getDefaultDisplay().getSize(size);
        int screen_width = size.x;

        ToolType tool = ToolType.BRUSH;
        if(!isCurrentTool(tool))
            selectTool(tool);
        openToolOptionsForCurrentTool(tool);
        int mainToolOptionWidth = mSolo.getView(R.id.main_tool_options).getWidth();
        int bottomBarWidth = mSolo.getView(R.id.main_bottom_bar).getWidth();
        int toolbarWidth = mSolo.getView(R.id.toolbar_container).getWidth();

        int expectedMainToolOptionWidth = screen_width - bottomBarWidth - toolbarWidth;

        assertEquals("MainToolOption width is wrong", expectedMainToolOptionWidth, mainToolOptionWidth);


    }

    public void testToolBarOption() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
            IllegalAccessException{
        ToolType tool = ToolType.PIPETTE;
        if(!isCurrentTool(tool))
            selectTool(tool);
        openToolOptionsForCurrentTool(tool);
        assertTrue("Toolbar is not shown", mSolo.getView(R.id.main_tool_options).isShown());
    }

    public void testTools() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
    IllegalAccessException {

        LinearLayout toolsLayout = (LinearLayout) mSolo.getView(R.id.tools_layout);
        int toolCount = toolsLayout.getChildCount() - getNumberOfNotVisibleTools();

        for(int i = 0; i < toolCount; i++)
        {
            View toolButton = toolsLayout.getChildAt(i);
            ToolType tool = getToolTypeByButtonId(toolButton.getId());
            if(tool == ToolType.IMPORTPNG)
                continue;
            if(!isCurrentTool(tool))
                selectTool(tool);
            if(!getCurrentTool().getToolOptionsAreShown())
                openToolOptionsForCurrentTool(tool);
            mSolo.sleep(SHORT_SLEEP);
        }


    }

    public void testCorrectSelectionInBothOrientations() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
            IllegalAccessException {

        setOrienation(SCREEN_ORIENTATION_LANDSCAPE);

        LinearLayout toolsLayout = (LinearLayout) mSolo.getView(R.id.tools_layout);
        int toolCount = toolsLayout.getChildCount() - getNumberOfNotVisibleTools();

        for(int i = 0; i < toolCount; i++)
        {
            setOrienation(SCREEN_ORIENTATION_LANDSCAPE);
            View toolButton = toolsLayout.getChildAt(i);
            ToolType tool = getToolTypeByButtonId(toolButton.getId());
            if(tool == ToolType.IMPORTPNG)
                continue;
            if(!isCurrentTool(tool))
                selectTool(tool);

            setOrienation(SCREEN_ORIENTATION_PORTRAIT);
            mSolo.sleep(MEDIUM_TIMEOUT);
            assertEquals("Selected Tool is not the same after orientation change", tool, getCurrentTool().getToolType());

        }
    }

    public void testNavigationdrawerAppears() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
            IllegalAccessException {

        setOrienation(SCREEN_ORIENTATION_LANDSCAPE);
        View toolbar = (View)mSolo.getView(R.id.toolbar);

        mSolo.clickOnView(toolbar);
        mSolo.sleep(MEDIUM_TIMEOUT);

        View drawer = (View)mSolo.getView(R.id.nav_view);
        assertTrue("Navigationdrawer is not shown", drawer.isShown());
    }

    public void testOpenColorPickerDialogInLandscape() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
            IllegalAccessException {

        mSolo.clickOnView(mColorButton);
        ColorPickerView colorPickerView = (ColorPickerView) mSolo.getView(R.id.view_colorpicker);
        assertTrue("ColorPickerView is not shown", colorPickerView.isShown());
    }

    public void testOpenColorPickerDialogChooseColorInLandscape() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
            IllegalAccessException {


        mSolo.clickOnView(mColorButton);
        ColorPickerView colorPickerView = (ColorPickerView) mSolo.getView(R.id.view_colorpicker);
        assertTrue("ColorPickerView is not shown", colorPickerView.isShown());
        mSolo.scrollUp();

        TypedArray presetColors = getActivity().getResources().obtainTypedArray(R.array.preset_colors);
        Button presetColorButton = mSolo.getButton(5);
        int presetColorButtonColor =  presetColors.getColor(presetColorButton.getId(),0);
        mSolo.clickOnButton(presetColorButton.getId());

        if(!mSolo.getView(R.id.colorchooser_ok_button_base_layout).isFocused())
            mSolo.scrollDown();

        String doneButtonName = getActivity().getResources().getString(R.string.done);
        Button doneButton = mSolo.getButton(doneButtonName);

        Drawable buttonDoneDrawable = doneButton.getBackground();
        int doneButtonColor = ((ColorDrawable)buttonDoneDrawable).getColor();
        assertEquals("Background color of DoneButton has unexpected color", presetColorButtonColor, doneButtonColor);

        int currentChosenColor = PaintroidApplication.currentTool.getDrawPaint().getColor();
        assertEquals("Current chosen color has unexpected color", presetColorButtonColor, currentChosenColor);
    }

    public void testColorPickerDialogSwitchTabsInLandscape() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
            IllegalAccessException {

        String[] colorChooserTags = { mSolo.getString(R.string.color_pre),mSolo.getString(R.string.color_hsv), mSolo.getString(R.string.color_rgb) };

        mSolo.clickOnView(mColorButton);
        TabHost tabHost = (TabHost) mSolo.getView(R.id.colorview_tabColors);
        TabWidget colorTabWidget = tabHost.getTabWidget();

        assertEquals("Wrong tab count ", colorTabWidget.getTabCount(), colorChooserTags.length);
        for (int tabChildIndex = 0; tabChildIndex < colorTabWidget.getChildCount(); tabChildIndex++) {
            mSolo.scrollUp();
            mSolo.clickOnView(colorTabWidget.getChildAt(tabChildIndex), true);
            mSolo.sleep(500);
            if (colorChooserTags[tabChildIndex].equalsIgnoreCase(mSolo.getString(R.string.color_pre)))
                assertFalse("In preselection tab and rgb (red) string found",
                        mSolo.searchText(mSolo.getString(R.string.color_red)));
            else if (colorChooserTags[tabChildIndex].equalsIgnoreCase(mSolo.getString(R.string.color_rgb))) {
                assertTrue("In rgb tab and red string not found", mSolo.searchText(mSolo.getString(R.string.color_red)));
                assertTrue("In rgb tab and green string not found",
                        mSolo.searchText(mSolo.getString(R.string.color_green)));
                assertTrue("In rgb tab and blue string not found",
                        mSolo.searchText(mSolo.getString(R.string.color_blue)));
            }
        }
    }







    private ToolType getToolTypeByButtonId(int id) {
        ToolType retToolType = null;
        for (ToolType toolType : ToolType.values()) {
            if (toolType.getToolButtonID() == id) {
                retToolType =  toolType;
                break;
            }
        }
        assertNotNull(retToolType);
        return retToolType;
    }
    private boolean isCurrentTool(ToolType toolType) {
        if(PaintroidApplication.currentTool.getToolType() == toolType)
            return true;
        else
            return false;
    }

    private void setOrienation(int orienation) {
        if(orienation == SCREEN_ORIENTATION_PORTRAIT)
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        else if(orienation == SCREEN_ORIENTATION_LANDSCAPE)
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mSolo.sleep(MEDIUM_TIMEOUT);

    }




}
