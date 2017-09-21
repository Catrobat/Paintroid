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
import org.catrobat.paintroid.test.espresso.util.EspressoUtils;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.ui.button.LayersAdapter;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.addNewLayer;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.closeLayerMenu;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.deleteSelectedLayer;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.openLayerMenu;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.openNavigationDrawer;
import static org.junit.Assert.assertEquals;
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
	public void testInitialSetup() {

		Bitmap addLayer = ((BitmapDrawable) newButton.getBackground()).getBitmap();
		Bitmap deleteLayer = ((BitmapDrawable) deleteButton.getBackground()).getBitmap();

		assertTrue("Add layer button should be enabled", addLayer.sameAs(newButtonBitmap));
		assertTrue("Delete layer button should be disabled", deleteLayer.sameAs(deleteButtonDisabledBitmap));
	}

	@Test
	public void testButtonsAddOneLayer() {
		openLayerMenu();
		addNewLayer();

		Bitmap addLayer = ((BitmapDrawable) newButton.getBackground()).getBitmap();
		Bitmap deleteLayer = ((BitmapDrawable) deleteButton.getBackground()).getBitmap();

		assertTrue("Add layer button should be enabled", addLayer.sameAs(newButtonBitmap));
		assertTrue("Delete layer button should be enabled", deleteLayer.sameAs(deleteButtonBitmap));

		EspressoUtils.addNewLayer();
		addNewLayer();

		addLayer = ((BitmapDrawable) newButton.getBackground()).getBitmap();
		deleteLayer = ((BitmapDrawable) deleteButton.getBackground()).getBitmap();
		assertTrue("Add layer button should be disabled", addLayer.sameAs(newButtonDisabledBitmap));
		assertTrue("Delete layer button should be enabled", deleteLayer.sameAs(deleteButtonBitmap));

		deleteSelectedLayer();
		deleteSelectedLayer();
		deleteSelectedLayer();

		addLayer = ((BitmapDrawable) newButton.getBackground()).getBitmap();
		deleteLayer = ((BitmapDrawable) deleteButton.getBackground()).getBitmap();
		assertTrue("Add layer button should be enabled", addLayer.sameAs(newButtonBitmap));
		assertTrue("Delete layer button should be disabled", deleteLayer.sameAs(deleteButtonDisabledBitmap));
	}

	@Test
	public void testButtonsAfterNewImage() {
		openLayerMenu();
		for (int i = 1; i < LayersAdapter.MAX_LAYER; i++) {
			addNewLayer();
		}

		Bitmap addLayer = ((BitmapDrawable) newButton.getBackground()).getBitmap();
		assertTrue("Add layer button should be disabled", addLayer.sameAs(newButtonDisabledBitmap));

		Bitmap deleteLayer = ((BitmapDrawable) deleteButton.getBackground()).getBitmap();
		assertTrue("Delete layer button should be enabled", deleteLayer.sameAs(deleteButtonBitmap));

		onView(withId(R.id.toolbar)).perform(click());
		openNavigationDrawer();
		onView(withText(R.string.menu_new_image)).perform(click());
		onView(withText(R.string.discard_button_text)).perform(click());
		onView(withText(R.string.menu_new_image_empty_image)).perform(click());

		openLayerMenu();

		addLayer = ((BitmapDrawable) newButton.getBackground()).getBitmap();
		assertTrue("Add layer button should be enabled", addLayer.sameAs(newButtonBitmap));

		deleteLayer = ((BitmapDrawable) deleteButton.getBackground()).getBitmap();
		assertTrue("Delete layer button should be disabled", deleteLayer.sameAs(deleteButtonDisabledBitmap));
	}

	public void testUndoRedoLayerAdd() {
		int heightOneLayer = navigationView.getHeight();
		openLayerMenu();
		addNewLayer();
		closeLayerMenu();
		int heightTwoLayer = navigationView.getHeight();
		assertTrue("One Layer should have been added", heightTwoLayer > heightOneLayer);

		onView(withId(R.id.btn_top_undo)).perform(click());

		int currentLayerHeight = navigationView.getHeight();
		assertEquals("There should be only one Layer after Undo", heightOneLayer, currentLayerHeight);

		onView(withId(R.id.btn_top_redo)).perform(click());
		currentLayerHeight = navigationView.getHeight();
		assertEquals("There should be two Layers after Redo", heightTwoLayer, currentLayerHeight);

	}

	@Test
	public void testUndoRedoLayerDelete() {
		int heightOneLayer = navigationView.getHeight();
		openLayerMenu();
		addNewLayer();

		int heightTwoLayer = navigationView.getHeight();
		assertTrue("One Layer should have been added", heightTwoLayer > heightOneLayer);

		deleteSelectedLayer();
		closeLayerMenu();
		int currentLayerHeight = navigationView.getHeight();
		assertEquals("New Layer should be deleted", heightOneLayer, currentLayerHeight);

		onView(withId(R.id.btn_top_undo)).perform(click());
		currentLayerHeight = navigationView.getHeight();
		assertEquals("There should be two Layers after Undo", heightTwoLayer, currentLayerHeight);

		onView(withId(R.id.btn_top_redo)).perform(click());
		currentLayerHeight = navigationView.getHeight();
		assertEquals("There should be one Layer after Redo", heightOneLayer, currentLayerHeight);
	}

	@Test
	public void testCreateManyLayers() {
		openLayerMenu();

		for(int i = 0; i < 100; i++) {
			addNewLayer();
			deleteSelectedLayer();
		}
	}
}
