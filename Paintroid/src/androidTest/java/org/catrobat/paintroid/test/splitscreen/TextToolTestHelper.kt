package org.catrobat.paintroid.test.splitscreen

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.google.android.material.button.MaterialButton
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction
import org.catrobat.paintroid.tools.FontType
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.implementation.TextTool
import org.catrobat.paintroid.ui.tools.FontListAdapter
import org.junit.Assert

object TextToolTestHelper {
    private const val TEST_TEXT = "123 www 123"

    private lateinit var textTool: TextTool
    private lateinit var fontList: RecyclerView
    private lateinit var underlinedToggleButton: MaterialButton
    private lateinit var italicToggleButton: MaterialButton
    private lateinit var boldToggleButton: MaterialButton

    private lateinit var activity: MainActivity

    fun setupEnvironment(mainActivity: MainActivity) {
        activity = mainActivity
    }

    fun testDialogToolInteraction() {
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.TEXT)
        initViewElements()
        enterTextInput()
        Assert.assertEquals(TEST_TEXT, textTool.text)
        selectFontType(FontType.SERIF)
        Assert.assertEquals(FontType.SERIF, textTool.font)
        Assert.assertEquals(
            FontType.SERIF,
            (fontList.adapter as FontListAdapter).getSelectedItem()
        )
        selectFormatting(FormattingOptions.UNDERLINE)
        Assert.assertTrue(textTool.underlined)
        Assert.assertTrue(underlinedToggleButton.isChecked)
        Assert.assertEquals(
            getFormattingOptionAsString(FormattingOptions.UNDERLINE),
            underlinedToggleButton.text.toString()
        )
        selectFormatting(FormattingOptions.UNDERLINE)
        Assert.assertFalse(textTool.underlined)
        Assert.assertFalse(underlinedToggleButton.isChecked)
        Assert.assertEquals(
            getFormattingOptionAsString(FormattingOptions.UNDERLINE),
            underlinedToggleButton.text.toString()
        )
        selectFormatting(FormattingOptions.ITALIC)
        Assert.assertTrue(getToolMemberItalic())
        Assert.assertTrue(italicToggleButton.isChecked)
        Assert.assertEquals(
            getFormattingOptionAsString(FormattingOptions.ITALIC),
            italicToggleButton.text.toString()
        )
        selectFormatting(FormattingOptions.ITALIC)
        Assert.assertFalse(getToolMemberItalic())
        Assert.assertFalse(italicToggleButton.isChecked)
        Assert.assertEquals(
            getFormattingOptionAsString(FormattingOptions.ITALIC),
            italicToggleButton.text.toString()
        )
        selectFormatting(FormattingOptions.BOLD)
        Assert.assertTrue(getToolMemberBold())
        Assert.assertTrue(boldToggleButton.isChecked)
        Assert.assertEquals(
            getFormattingOptionAsString(FormattingOptions.BOLD),
            boldToggleButton.text.toString()
        )
        selectFormatting(FormattingOptions.BOLD)
        Assert.assertFalse(getToolMemberBold())
        Assert.assertFalse(boldToggleButton.isChecked)
        Assert.assertEquals(
            getFormattingOptionAsString(FormattingOptions.BOLD),
            boldToggleButton.text.toString()
        )
    }

    private fun enterTextInput() {
        Espresso.onView(withId(R.id.pocketpaint_text_tool_dialog_input_text))
            .perform(ViewActions.replaceText(TEST_TEXT))
        Espresso.closeSoftKeyboard()
        Espresso.onView(withId(R.id.pocketpaint_text_tool_dialog_input_text))
            .check(ViewAssertions.matches(ViewMatchers.withText(TEST_TEXT)))
    }

    private fun selectFormatting(format: FormattingOptions) {
        Espresso.onView(ViewMatchers.withText(getFormattingOptionAsString(format)))
            .perform(ViewActions.click())
    }

    private fun getFormattingOptionAsString(format: FormattingOptions): String {
        return when (format) {
            FormattingOptions.UNDERLINE -> activity.getString(R.string.text_tool_dialog_underline_shortcut)
            FormattingOptions.ITALIC -> activity.getString(R.string.text_tool_dialog_italic_shortcut)
            FormattingOptions.BOLD -> activity.getString(R.string.text_tool_dialog_bold_shortcut)
        }
    }


    private fun selectFontType(fontType: FontType) {
        Espresso.onView(withId(R.id.pocketpaint_text_tool_dialog_list_font))
            .perform(
                RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                    ViewMatchers.hasDescendant(
                        ViewMatchers.withText(getFontTypeAsString(fontType))
                    )
                )
            )
        Espresso.onView(ViewMatchers.withText(getFontTypeAsString(fontType)))
            .perform(ViewActions.click())
    }

    private fun getFontTypeAsString(fontType: FontType): String? {
        return when (fontType) {
            FontType.SANS_SERIF -> activity.getString(R.string.text_tool_dialog_font_sans_serif)
            FontType.SERIF -> activity.getString(R.string.text_tool_dialog_font_serif)
            FontType.MONOSPACE -> activity.getString(R.string.text_tool_dialog_font_monospace)
            FontType.STC -> activity.getString(R.string.text_tool_dialog_font_arabic_stc)
            FontType.DUBAI -> activity.getString(R.string.text_tool_dialog_font_dubai)
            else -> null
        }
    }

    private fun getToolMemberItalic(): Boolean {
        return textTool.italic
    }

    private fun getToolMemberBold(): Boolean {
        return textTool.bold
    }

    private enum class FormattingOptions {
        UNDERLINE, ITALIC, BOLD
    }

    private fun initViewElements() {
        textTool = activity.toolReference.tool as TextTool
        fontList = activity.findViewById(R.id.pocketpaint_text_tool_dialog_list_font)
        underlinedToggleButton =
            activity.findViewById(R.id.pocketpaint_text_tool_dialog_toggle_underlined)
        italicToggleButton =
            activity.findViewById(R.id.pocketpaint_text_tool_dialog_toggle_italic)
        boldToggleButton =
            activity.findViewById(R.id.pocketpaint_text_tool_dialog_toggle_bold)
    }
}
