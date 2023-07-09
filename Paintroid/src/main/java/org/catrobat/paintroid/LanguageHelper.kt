package org.catrobat.paintroid

import android.text.TextUtils
import android.view.View
import java.util.*

class LanguageHelper {

    companion object {
        fun isCurrentLanguageRTL(): Boolean {
            val layoutDirection = TextUtils.getLayoutDirectionFromLocale(Locale.getDefault())
            return layoutDirection == View.LAYOUT_DIRECTION_RTL
        }
    }

}