package org.catrobat.paintroid.test.integration.dialog;

import android.support.test.espresso.NoMatchingViewException;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.assertion.LayoutAssertions.noEllipsizedText;
import static android.support.test.espresso.assertion.LayoutAssertions.noMultilineButtons;
import static android.support.test.espresso.assertion.LayoutAssertions.noOverlaps;
import static android.support.test.espresso.assertion.PositionAssertions.isRightOf;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Created by Aiman Ayyal Awwad on 10/29/2015.
 */
@RunWith(AndroidJUnit4.class)

public class ViewsAssertionLocalizationTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule;

    public ViewsAssertionLocalizationTest() {
        mActivityRule = new ActivityTestRule (MainActivity.class);
    }

    @Test
    public void assertNoOverlappingForLineStrokeDialog()
    {

        onView(withId(R.id.btn_bottom_attribute1))
                .perform(click());
        onView(withId(R.id.linearlayout1)).check(noOverlaps());
    }

    @Test
     public void assertNoOverlappingForColorPalletDialog()
    {
        onView(withId(R.id.btn_bottom_attribute2))
                .perform(click());
        onView(withId(R.id.colorchooser_base_layout)).check(noOverlaps());
    }

    @Test
    public void assertNoOverlappingForToolsDialog()
    {
        onView(withId(R.id.btn_bottom_tools))
                .perform(click());
        onView(withId(R.id.gridview_tools_menu)).check(noOverlaps());
    }

    @Test
    public void assertNoOverLappingForMainActivity()
    {
        onView(withId(R.id.main_layout)).check(noOverlaps());

    }
    @Test
    public void assertNoEllipseizedTextInToolsDialog() {
        onView(withId(R.id.btn_bottom_tools))
                .perform(click());
        onView(withId(R.id.gridview_tools_menu)).check(noEllipsizedText());
    }

    @Test
    public void assertNoEllipseizedTextInStrokeLineDialog()
    {
        onView(withId(R.id.btn_bottom_attribute1))
                .perform(click());
        onView(withId(R.id.stroke_width_shape_text)).check(noEllipsizedText());
    }

    @Test
    public void assertNoEllipseizedTextInColorPallet()
    {
        onView(withId(R.id.btn_bottom_attribute2))
                .perform(click());
        onView(withId(R.id.colorchooser_base_layout)).check(noEllipsizedText());
    }
   @Test
   public void assertNoEllipseizedMainActivity()
   {
       onView(withId(R.id.main_layout)).check(noEllipsizedText());
   }

    @Test
    public void assertIsDisplayedTextForToolsText() {
        onView(withId(R.id.btn_bottom_tools))
                .perform(click());
        onView(withId(R.id.gridview_tools_menu)).check(matches(isDisplayed()));
    }

    @Test
    public void assertIsDisplayedForMainActivity() {

        onView(withId(R.id.main_layout)).check(matches(isDisplayed()));
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.menu_language_settings)).perform(click());
        onView(withId(R.id.Arabic_choice)).perform(click());
        onView(withText(R.string.done)).perform(click());
        onView(withId(R.id.main_layout)).perform(click());

    }

   @Test
   public void assertIsDisplayedForColorPalletDialog()
   {
    onView(withId(R.id.btn_bottom_attribute2))
            .perform(click());
       onView(withId(R.id.view_colorpicker)).check(matches(isDisplayed()));
   }

    @Test
    public void assertIsDisplayedForStrokeLineDialog()
    {
        onView(withId(R.id.btn_bottom_attribute1))
                .perform(click());
        onView(withId(R.id.stroke_width_shape_text)).check(matches(isDisplayed()));
    }

    @Test
    public void assertNoMultilineButtons() {
        onView(withId(R.id.btn_bottom_tools))
                .perform(click());
        onView(withId(R.id.gridview_tools_menu)).check(noMultilineButtons());
    }


    @Test
    public void assertSeekBarIsRightOfValue() {
        onView(withId(R.id.btn_bottom_attribute1))
                .perform(click());
        onView(withId(R.id.stroke_width_seek_bar)).check(isRightOf(withId(R.id.stroke_width_width_text)));
    }

    @Test
    public void assertSwippingRightforStrokeSeekbar()
    {
        onView(withId(R.id.btn_bottom_attribute1))
                .perform(click());
        onView(withId(R.id.stroke_width_seek_bar)).perform(swipeRight());
    }
    @Test
    public void assertToolsRightOfText()
    {
        onView(withId(R.id.btn_bottom_tools))
                .perform(click());
        onView(allOf(withId(R.id.tool_button_image), hasSibling(
                                                         withText(R.string.button_brush))))
        .check(isRightOf(allOf(withId(R.id.tool_button_text), hasSibling(
                withText(R.string.button_brush)))));
    }
    @Test
    public void assertExistanceForToolsDialog()
    {
        onView(withId(R.id.btn_bottom_tools))
                .perform(click());
        try {
            onView(withId(R.id.btn_bottom_tools)).check(matches(isDisplayed()));
            onView(withId(R.id.tool_button_text)).check(matches(isDisplayed()));
        } catch (NoMatchingViewException e) {
        }
    }

    @Test
    public void assertNoNullValuesForToolsDialog()
    {
        onView(withId(R.id.btn_bottom_tools))
                .perform(click());
        onView(allOf(withId(R.id.tool_button_image), hasSibling(
                withText(R.string.button_brush)))).check(matches(notNullValue()));
        onView(allOf(withId(R.id.tool_button_image), hasSibling(
                withText(R.string.button_resize)))).check(matches(notNullValue()));
        onView(allOf(withId(R.id.tool_button_image), hasSibling(
                withText(R.string.button_cursor)))).check(matches(notNullValue()));
        onView(allOf(withId(R.id.tool_button_image), hasSibling(
                withText(R.string.button_ellipse)))).check(matches(notNullValue()));
        onView(allOf(withId(R.id.tool_button_image), hasSibling(
                withText(R.string.button_fill)))).check(matches(notNullValue()));
        onView(allOf(withId(R.id.tool_button_image), hasSibling(
                withText(R.string.button_fill)))).check(matches(notNullValue()));
        onView(allOf(withId(R.id.tool_button_image), hasSibling(
                withText(R.string.button_line)))).check(matches(notNullValue()));
    }

    @Test
    public void assertNoEllipseizedForToolsDialog()
    {
        onView(withId(R.id.btn_bottom_tools))
                .perform(click());
        onView(allOf(withId(R.id.tool_button_image), hasSibling(
                withText(R.string.button_brush)))).check(noEllipsizedText());
        onView(allOf(withId(R.id.tool_button_image), hasSibling(
                withText(R.string.button_ellipse)))).check(noEllipsizedText());
        onView(allOf(withId(R.id.tool_button_image), hasSibling(
                withText(R.string.button_fill)))).check(noEllipsizedText());
        onView(allOf(withId(R.id.tool_button_image), hasSibling(
                withText(R.string.button_line)))).check(noEllipsizedText());
        onView(allOf(withId(R.id.tool_button_image), hasSibling(
                withText(R.string.button_cursor)))).check(noEllipsizedText());
        onView(allOf(withId(R.id.tool_button_image), hasSibling(
                withText(R.string.button_import_image)))).check(noEllipsizedText());
        onView(allOf(withId(R.id.tool_button_image), hasSibling(
                withText(R.string.button_resize)))).check(noEllipsizedText());
    }

    @Test
    public void assertCompletelyDisplayedForToolsDialog()
    {
        onView(withId(R.id.btn_bottom_tools))
                .perform(click());
        onView(allOf(withId(R.id.tool_button_image), hasSibling(
                withText(R.string.button_brush)))).check(matches(isCompletelyDisplayed()));
        onView(allOf(withId(R.id.tool_button_image), hasSibling(
                withText(R.string.button_ellipse)))).check(matches(isCompletelyDisplayed()));
        onView(allOf(withId(R.id.tool_button_image), hasSibling(
                withText(R.string.button_fill)))).check(matches(isCompletelyDisplayed()));
        onView(allOf(withId(R.id.tool_button_image), hasSibling(
                withText(R.string.button_line)))).check(matches(isCompletelyDisplayed()));
        onView(allOf(withId(R.id.tool_button_image), hasSibling(
                withText(R.string.button_cursor)))).check(matches(isCompletelyDisplayed()));
        onView(allOf(withId(R.id.tool_button_image), hasSibling(
                withText(R.string.button_import_image)))).check(matches(isCompletelyDisplayed()));
        onView(allOf(withId(R.id.tool_button_image), hasSibling(
                withText(R.string.button_resize)))).check(matches(isCompletelyDisplayed()));
    }


}


