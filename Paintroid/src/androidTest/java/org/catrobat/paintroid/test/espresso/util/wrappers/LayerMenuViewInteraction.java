/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.espresso.util.wrappers;

import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.DrawerActions;
import android.view.Gravity;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.tools.Layer;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.catrobat.paintroid.test.espresso.util.UiInteractions.assertListViewCount;
import static org.hamcrest.Matchers.instanceOf;

public final class LayerMenuViewInteraction extends CustomViewInteraction {
	private LayerMenuViewInteraction() {
		super(onView(withId(R.id.nav_view_layer)));
	}

	public static LayerMenuViewInteraction onLayerMenuView() {
		return new LayerMenuViewInteraction();
	}

	public ViewInteraction onButtonAdd() {
		return onView(withId(R.id.layer_side_nav_button_add));
	}

	public ViewInteraction onButtonDelete() {
		return onView(withId(R.id.layer_side_nav_button_delete));
	}

	public ViewInteraction onLayerList() {
		return onView(withId(R.id.nav_layer_list));
	}

	public LayerMenuViewInteraction checkLayerCount(int count) {
		onLayerList()
				.check(assertListViewCount(count));
		return this;
	}

	public DataInteraction onLayerAt(int listPosition) {
		return onData(instanceOf(Layer.class))
				.inAdapterView(withId(R.id.nav_layer_list))
				.atPosition(listPosition);
	}

	public LayerMenuViewInteraction performOpen() {
		onView(withId(R.id.btn_top_layers))
				.perform(click());
		check(matches(isDisplayed()));
		return this;
	}

	public LayerMenuViewInteraction performClose() {
		check(matches(isDisplayed()));
		onView(withId(R.id.drawer_layout))
			.perform(DrawerActions.close(Gravity.END));
		return this;
	}

	public LayerMenuViewInteraction performSelectLayer(int listPosition) {
		check(matches(isDisplayed()));
		onLayerAt(listPosition)
				.perform(click());
		return this;
	}

	public LayerMenuViewInteraction performAddLayer() {
		check(matches(isDisplayed()));
		onButtonAdd()
				.perform(click());
		return this;
	}

	public LayerMenuViewInteraction performDeleteLayer() {
		check(matches(isDisplayed()));
		onButtonDelete()
				.perform(click());
		return this;
	}
}
