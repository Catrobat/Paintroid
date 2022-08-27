package org.catrobat.paintroid.test.espresso.util.wrappers

import androidx.annotation.StringRes
import androidx.appcompat.widget.MenuPopupWindow.MenuDropDownListView
import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions
import org.catrobat.paintroid.test.espresso.util.UiMatcher
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers

class OptionsMenuViewInteraction private constructor() {
    fun checkItemExists(@StringRes item: Int): OptionsMenuViewInteraction {
        optionsMenu.check(ViewAssertions.matches(UiMatcher.withAdaptedData(item)))
        return this
    }

    fun checkItemDoesNotExist(@StringRes item: Int): OptionsMenuViewInteraction {
        optionsMenu.check(ViewAssertions.matches(Matchers.not(UiMatcher.withAdaptedData(item))))
        return this
    }

    companion object {
        lateinit var optionsMenu: ViewInteraction

        @JvmStatic
        fun onOptionsMenu(): OptionsMenuViewInteraction = OptionsMenuViewInteraction()
    }

    init {
        optionsMenu = Espresso.onView(CoreMatchers.instanceOf(MenuDropDownListView::class.java))
    }
}
