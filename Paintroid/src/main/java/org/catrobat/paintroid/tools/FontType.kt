package org.catrobat.paintroid.tools

import androidx.annotation.StringRes
import org.catrobat.paintroid.R

enum class FontType(@StringRes val nameResource: Int) {
    SANS_SERIF(R.string.text_tool_dialog_font_sans_serif),
    SERIF(R.string.text_tool_dialog_font_serif),
    MONOSPACE(R.string.text_tool_dialog_font_monospace),
    STC(R.string.text_tool_dialog_font_arabic_stc),
    DUBAI(R.string.text_tool_dialog_font_dubai),
    ROBOTO(R.string.text_tool_dialog_font_roboto),
    LATO(R.string.text_tool_dialog_font_lato),
    MONTSERRAT(R.string.text_tool_dialog_font_montserrat),
    OPENSANS(R.string.text_tool_dialog_font_opensans),
    OSWALD(R.string.text_tool_dialog_font_oswald);
}
