package org.catrobat.paintroid.test.espresso.util.wrappers;

import android.support.annotation.StringRes;
import android.support.test.espresso.ViewInteraction;
import android.support.v7.widget.MenuPopupWindow;
import android.view.View;

import org.hamcrest.CoreMatchers;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;

import static org.catrobat.paintroid.test.espresso.util.UiMatcher.withAdaptedData;
import static org.hamcrest.Matchers.not;

public final class OptionsMenuViewInteraction {
	static ViewInteraction optionsMenu;

	private OptionsMenuViewInteraction() {
		optionsMenu = onView(CoreMatchers.<View>instanceOf(MenuPopupWindow.MenuDropDownListView.class));
	}

	public static OptionsMenuViewInteraction onOptionsMenu() {
		return new OptionsMenuViewInteraction();
	}

	public OptionsMenuViewInteraction checkItemExists(@StringRes int item) {
		optionsMenu.check(matches(withAdaptedData(item)));

		return this;
	}

	public OptionsMenuViewInteraction checkItemDoesNotExist(@StringRes int item) {
		optionsMenu.check(matches(not(withAdaptedData(item))));
		return this;
	}
}
