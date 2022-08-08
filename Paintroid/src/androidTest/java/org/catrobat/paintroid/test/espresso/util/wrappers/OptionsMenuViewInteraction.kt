package org.catrobat.paintroid.test.espresso.util.wrappers

import androidx.annotation.StringRes
import androidx.test.espresso.assertion.ViewAssertions
import org.catrobat.paintroid.test.espresso.util.UiMatcher
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.Espresso
import org.hamcrest.CoreMatchers
import androidx.appcompat.widget.MenuPopupWindow.MenuDropDownListView
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
		fun onOptionsMenu(): OptionsMenuViewInteraction { return OptionsMenuViewInteraction() }
    }

    init {
        optionsMenu = Espresso.onView(CoreMatchers.instanceOf(MenuDropDownListView::class.java))
    }
}
