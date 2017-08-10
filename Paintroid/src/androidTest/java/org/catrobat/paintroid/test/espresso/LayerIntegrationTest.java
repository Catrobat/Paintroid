/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2015 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.espresso;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.design.widget.NavigationView;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.content.ContextCompat;
import android.widget.ImageButton;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.listener.LayerListener;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class LayerIntegrationTest {

	public static final String FIELD_NAME_NAVIGATION_VIEW = "mNavigationView";
	public static final String FIELD_NAME_CONTEXT = "mContext";

	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);

	private NavigationView navigationView;
	private Context context;
	private ImageButton newButton;
	private ImageButton deleteButton;

	private Bitmap newButtonBitmap;
	private Bitmap newButtonDisabledBitmap;
	private Bitmap deleteButtonBitmap;
	private Bitmap deleteButtonDisabledBitmap;

	@Before
	public void setUp() throws Exception {
		navigationView = (NavigationView) PrivateAccess.getMemberValue(LayerListener.class, LayerListener.getInstance(), FIELD_NAME_NAVIGATION_VIEW);
		context = (Context) PrivateAccess.getMemberValue(LayerListener.class, LayerListener.getInstance(), FIELD_NAME_CONTEXT);
		newButton = (ImageButton) navigationView.findViewById(R.id.layer_side_nav_button_add);
		deleteButton = (ImageButton) navigationView.findViewById(R.id.layer_side_nav_button_delete);

		newButtonBitmap = ((BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.icon_layers_new)).getBitmap();
		newButtonDisabledBitmap = ((BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.icon_layers_new_disabled)).getBitmap();

		deleteButtonBitmap = ((BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.icon_layers_delete)).getBitmap();
		deleteButtonDisabledBitmap = ((BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.icon_layers_delete_disabled)).getBitmap();
	}

	@After
	public void tearDown() {

	}

	@Test
	public void testInitalSetup() {

		Bitmap addLayer = ((BitmapDrawable) newButton.getBackground()).getBitmap();
		Bitmap deleteLayer = ((BitmapDrawable) deleteButton.getBackground()).getBitmap();

		assertTrue("Add layer button should be enabled", addLayer.sameAs(newButtonBitmap));
		assertTrue("Delete layer button should be disabled", deleteLayer.sameAs(deleteButtonDisabledBitmap));
	}

	@Test
	public void testButtonsAddOneLayer() {
		onView(withId(R.id.btn_top_layers)).perform(click());
		onView(withId(R.id.layer_side_nav_button_add)).perform(click());

		Bitmap addLayer = ((BitmapDrawable) newButton.getBackground()).getBitmap();
		Bitmap deleteLayer = ((BitmapDrawable) deleteButton.getBackground()).getBitmap();

		assertTrue("Add layer button should be enabled", addLayer.sameAs(newButtonBitmap));
		assertTrue("Delete layer button should be enabled", deleteLayer.sameAs(deleteButtonBitmap));

		onView(withId(R.id.layer_side_nav_button_add)).perform(click());
		onView(withId(R.id.layer_side_nav_button_add)).perform(click());

		addLayer = ((BitmapDrawable) newButton.getBackground()).getBitmap();
		deleteLayer = ((BitmapDrawable) deleteButton.getBackground()).getBitmap();
		assertTrue("Add layer button should be disabled", addLayer.sameAs(newButtonDisabledBitmap));
		assertTrue("Delete layer button should be enabled", deleteLayer.sameAs(deleteButtonBitmap));

		onView(withId(R.id.layer_side_nav_button_delete)).perform(click());
		onView(withId(R.id.layer_side_nav_button_delete)).perform(click());
		onView(withId(R.id.layer_side_nav_button_delete)).perform(click());

		addLayer = ((BitmapDrawable) newButton.getBackground()).getBitmap();
		deleteLayer = ((BitmapDrawable) deleteButton.getBackground()).getBitmap();
		assertTrue("Add layer button should be enabled", addLayer.sameAs(newButtonBitmap));
		assertTrue("Delete layer button should be disabled", deleteLayer.sameAs(deleteButtonDisabledBitmap));
	}
}
