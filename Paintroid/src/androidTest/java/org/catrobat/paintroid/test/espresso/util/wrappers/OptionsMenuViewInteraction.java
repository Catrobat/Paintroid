package org.catrobat.paintroid.test.espresso.util.wrappers;

import android.view.View;

import org.hamcrest.CoreMatchers;

import androidx.annotation.StringRes;
import androidx.appcompat.widget.MenuPopupWindow;
import androidx.test.espresso.ViewInteraction;

import static org.catrobat.paintroid.test.espresso.util.UiMatcher.withAdaptedData;
import static org.hamcrest.Matchers.not;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

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
