/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.espresso.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.support.annotation.ColorInt;
import android.support.annotation.IdRes;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.dialog.colorpicker.PresetSelectorView;
import org.catrobat.paintroid.intro.TapTargetTopBar;
import org.catrobat.paintroid.listener.BrushPickerView;
import org.catrobat.paintroid.test.espresso.util.wrappers.ColorPickerViewInteraction;
import org.catrobat.paintroid.test.espresso.util.wrappers.LayerMenuViewInteraction;
import org.catrobat.paintroid.test.espresso.util.wrappers.NavigationDrawerInteraction;
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.test.utils.Utils;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.BaseTool;
import org.catrobat.paintroid.tools.implementation.BaseToolWithShape;
import org.catrobat.paintroid.tools.implementation.EraserTool;
import org.catrobat.paintroid.tools.implementation.FillTool;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.catrobat.paintroid.ui.Perspective;
import org.catrobat.paintroid.ui.button.ColorButton;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.paintroid.MultilingualActivity.updateLocale;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.selectViewPagerPage;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.unconstrainedScrollTo;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.hasTablePosition;
import static org.catrobat.paintroid.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.NavigationDrawerInteraction.onNavigationDrawer;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.fail;

/**
 * TODO: move PrivateAccess methods and constants to another class?
 */
public final class EspressoUtils {

	public static final Paint.Cap DEFAULT_STROKE_CAP = Paint.Cap.ROUND;

	public static final int DEFAULT_STROKE_WIDTH = 25;

	public static final String EXTRA_CATROID_PICTURE_PATH_NAME = "org.catrobat.extra.PAINTROID_PICTURE_PATH";

	public static final String EXTRA_CATROID_PICTURE_NAME_NAME = "org.catrobat.extra.PAINTROID_PICTURE_NAME";

	public static final int COLOR_CHOOSER_PRESET_BLACK_BUTTON_ID = 16;

	/**
	 * Field name for surface width of {@link Perspective} class. Use {@link #getSurfaceHeight()} to
	 * get the value
	 */
	public static final String FIELD_NAME_SURFACE_WIDTH = "surfaceWidth";

	/**
	 * Field name for surface height of {@link Perspective} class. Use {@link #getSurfaceHeight()} to
	 * get the value
	 */
	public static final String FIELD_NAME_SURFACE_HEIGHT = "surfaceHeight";

	/**
	 * Field name for current working bitmap {@link Bitmap} class. Use {@link #getWorkingBitmap()} to
	 * get the value
	 */
	public static final String FIELD_NAME_WORKING_BITMAP = "workingBitmap";

	/**
	 * Field name for {@link BaseTool} canvas {@link Paint} class. Use {@link #getCurrentToolPaint()} to
	 * get the value
	 */
	public static final String FIELD_NAME_CANVAS_PAINT = "canvasPaint";

	/**
	 * Field name for {@link FillTool} tolerance value. Use {@link #getCurrentToolPaint()} to
	 * get the value
	 */
	public static final String FIELD_NAME_COLOR_TOLERANCE = "colorTolerance";

	/**
	 * Field name for {@link PointF} tool position. Use {@link #getToolMemberBoxPosition()}  to
	 * get the value
	 */
	public static final String FIELD_NAME_TOOL_POSITION = "toolPosition";

	/**
	 * Field name for float surface x value. Use {@link #getSurfaceCenterX()}  to
	 * get the value
	 */
	public static final String FIELD_NAME_SURFACE_CENTER_X = "surfaceCenterX";

	/**
	 * Field name for float surface y value. Use {@link #getSurfaceCenterY()}  to
	 * get the value
	 */
	public static final String FIELD_NAME_SURFACE_CENTER_Y = "surfaceCenterY";

	public static final String FIELD_NAME_BOX_WIDTH = "boxWidth";

	public static final String FIELD_NAME_BOX_HEIGHT = "boxHeight";

	public static final String FIELD_NAME_DRAWING_BITMAP = "drawingBitmap";

	public static final String FIELD_NAME_COLOR_BUTTON = "colorButton";

	public static final int GREEN_COLOR_PICKER_BUTTON_POSITION = 2;
	public static final int BLACK_COLOR_PICKER_BUTTON_POSITION = 16;
	public static final int TRANSPARENT_COLOR_PICKER_BUTTON_POSITION = 19;

	private static final int COLOR_PICKER_BUTTONS_PER_ROW = 4;

	private EspressoUtils() {
	}

	/**
	 * @deprecated use {@link NavigationDrawerInteraction#performOpen()}
	 */
	@Deprecated
	public static void openNavigationDrawer() {
		onNavigationDrawer()
				.performOpen();
	}

	/**
	 * @deprecated use {@link NavigationDrawerInteraction#performClose()}
	 */
	@Deprecated
	public static void closeNavigationDrawer() {
		onNavigationDrawer()
				.performClose();
	}

	public static float getActionbarHeight() {
		return Utils.getActionbarHeight();
	}

	public static float getStatusbarHeight() {
		return Utils.getStatusbarHeight();
	}

	public static PointF getSurfacePointFromScreenPoint(PointF screenPoint) {
		return Utils.getSurfacePointFromScreenPoint(screenPoint);
	}

	public static PointF getCanvasPointFromScreenPoint(PointF screenPoint) {
		return Utils.getCanvasPointFromScreenPoint(screenPoint);
	}

	public static PointF getCanvasPointFromSurfacePoint(PointF surfacePoint) {
		return PaintroidApplication.perspective.getCanvasPointFromSurfacePoint(surfacePoint);
	}

	public static PointF convertFromCanvasToScreen(PointF canvasPoint, Perspective currentPerspective) throws NoSuchFieldException, IllegalAccessException {
		Point screenPoint = Utils.convertFromCanvasToScreen(new Point((int) canvasPoint.x, (int) canvasPoint.y), currentPerspective);
		return new PointF(screenPoint.x, screenPoint.y);
	}

	public static PointF getScreenPointFromSurfaceCoordinates(float pointX, float pointY) {
		return new PointF(pointX, pointY + getStatusbarHeight() + getActionbarHeight());
	}

	public static void resetDrawPaintAndBrushPickerView() {
		PaintroidApplication.currentTool.changePaintStrokeWidth(DEFAULT_STROKE_WIDTH);
		PaintroidApplication.currentTool.changePaintStrokeCap(DEFAULT_STROKE_CAP);
		BrushPickerView.getInstance().setCurrentPaint(PaintroidApplication.currentTool.getDrawPaint());
	}

	/**
	 * @deprecated use {@link ToolBarViewInteraction#performSelectTool(ToolType)}
	 */
	@Deprecated
	public static void selectTool(ToolType toolType) {
		ViewInteraction toolInteraction = onView(withId(toolType.getToolButtonID()))
				.perform(scrollTo());

		if (PaintroidApplication.currentTool.getToolType() != toolType) {
			toolInteraction.perform(click());
		}

		// Some test fail without wait
		waitMillis(500);
	}

	/**
	 * @deprecated use {@link ToolBarViewInteraction#performLongClickOnTool(ToolType)}
	 */
	@Deprecated
	public static void longClickOnTool(ToolType toolType) {
		ViewInteraction toolInteraction = onView(withId(toolType.getToolButtonID()))
				.perform(scrollTo());

		toolInteraction.perform(longClick());

		// Some test fail without wait
		waitMillis(500);
	}

	public static float getSurfaceWidth() throws NoSuchFieldException, IllegalAccessException {
		return (float) PrivateAccess.getMemberValue(Perspective.class, PaintroidApplication.perspective, FIELD_NAME_SURFACE_WIDTH);
	}

	public static float getSurfaceHeight() throws NoSuchFieldException, IllegalAccessException {
		return (float) PrivateAccess.getMemberValue(Perspective.class, PaintroidApplication.perspective, FIELD_NAME_SURFACE_HEIGHT);
	}

	public static Bitmap getWorkingBitmap() throws NoSuchFieldException, IllegalAccessException {
		return (Bitmap) PrivateAccess.getMemberValue(DrawingSurface.class, PaintroidApplication.drawingSurface, FIELD_NAME_WORKING_BITMAP);
	}

	public static Paint getCurrentToolPaint() throws NoSuchFieldException, IllegalAccessException {
		return (Paint) PrivateAccess.getMemberValue(BaseTool.class, PaintroidApplication.currentTool, FIELD_NAME_CANVAS_PAINT);
	}

	public static float getToolMemberColorTolerance(FillTool fillTool) throws NoSuchFieldException, IllegalAccessException {
		return (float) PrivateAccess.getMemberValue(FillTool.class, fillTool, FIELD_NAME_COLOR_TOLERANCE);
	}

	public static ColorButton getToolMemberColorButton(EraserTool eraserTool) throws NoSuchFieldException, IllegalAccessException {
		return (ColorButton) PrivateAccess.getMemberValue(EraserTool.class, eraserTool, FIELD_NAME_COLOR_BUTTON);
	}

	public static PointF getToolMemberBoxPosition() throws NoSuchFieldException, IllegalAccessException {
		return (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class, PaintroidApplication.currentTool, FIELD_NAME_TOOL_POSITION);
	}

	public static float getSurfaceCenterX() {
		try {
			return (float) PrivateAccess.getMemberValue(Perspective.class, PaintroidApplication.perspective, FIELD_NAME_SURFACE_CENTER_X);
		} catch (Exception e) {
			fail("Getting member mSurfaceCenterX failed");
		}
		return 0f;
	}

	public static float getSurfaceCenterY() {
		try {
			return (float) PrivateAccess.getMemberValue(Perspective.class, PaintroidApplication.perspective, FIELD_NAME_SURFACE_CENTER_Y);
		} catch (Exception e) {
			fail("Getting member mSurfaceCenterY failed");
		}
		return 0f;
	}

	/**
	 * @deprecated use {@link ToolBarViewInteraction#performOpenToolOptions()}
	 */
	@Deprecated
	public static void openToolOptionsForCurrentTool() {
		clickSelectedToolButton();
	}

	/**
	 * @deprecated use {@link ToolBarViewInteraction#performClickSelectedToolButton()}}
	 */
	@Deprecated
	public static void clickSelectedToolButton() {
		onToolBarView()
				.performClickSelectedToolButton();
	}

	public static void waitMillis(final long millis) {
		onView(isRoot()).perform(UiInteractions.waitFor(millis));
	}

	public static void waitForIdleSync() {
		InstrumentationRegistry.getInstrumentation().waitForIdleSync();
	}

	/**
	 * @deprecated use {@link ColorPickerViewInteraction#performOpenColorPicker()}
	 */
	@Deprecated
	public static void openColorPickerDialog() {
		onView(withId(R.id.btn_top_color)).perform(click());
	}

	/**
	 * @deprecated use {@link ColorPickerViewInteraction#performCloseColorPickerWithDialogButton()}
	 */
	@Deprecated
	public static void closeColorPickerDialogWithDialogButton() {
		onView(withId(R.id.btn_colorchooser_ok)).perform(click());
	}

	@ColorInt
	public static int[] getColorArrayFromResource(Context context, @IdRes int id) {
		TypedArray typedColors = context.getResources().obtainTypedArray(id);
		try {
			@ColorInt
			int[] colors = new int[typedColors.length()];
			for (int i = 0; i < typedColors.length(); i++) {
				colors[i] = typedColors.getColor(i, Color.BLACK);
			}
			return colors;
		} finally {
			typedColors.recycle();
		}
	}

	/**
	 * Opens color picker dialog, clicks on button given by its <i>buttonPosition</i> and
	 * closes color picker by acknowledging the color change.
	 *
	 * @param buttonPosition index origin is zero
	 */
	public static void selectColorPickerPresetSelectorColor(final int buttonPosition) {
		openColorPickerDialog();

		clickColorPickerPresetSelectorButton(buttonPosition);

		closeColorPickerDialogWithDialogButton();
	}

	/**
	 * @deprecated use {@link ColorPickerViewInteraction#performClickColorPickerPresetSelectorButton(int)}
	 */
	@Deprecated
	public static void clickColorPickerPresetSelectorButton(final int buttonPosition) {
		final int colorButtonRowPosition = (buttonPosition / COLOR_PICKER_BUTTONS_PER_ROW);
		final int colorButtonColPosition = buttonPosition % COLOR_PICKER_BUTTONS_PER_ROW;

		onView(
				allOf(
						isDescendantOfA(withClassName(containsString(PresetSelectorView.class.getSimpleName()))),
						isDescendantOfA(isAssignableFrom(TableLayout.class)),
						isDescendantOfA(isAssignableFrom(TableRow.class)),
						hasTablePosition(colorButtonRowPosition, colorButtonColPosition)
				)
		).perform(
				unconstrainedScrollTo()
		).check(
				matches(isDisplayed())
		).perform(
				click()
		);
	}

	/**
	 * Resets color to {@link android.graphics.Color#BLACK} by using color dialog. <i>Reset only if
	 * a tool with color picker dialog support is selected</i>
	 */
	public static void resetColorPicker() {
		selectColorPickerPresetSelectorColor(BLACK_COLOR_PICKER_BUTTON_POSITION);
	}

	public static void changeIntroPage(int page) {
		onView(withId(R.id.view_pager)).perform(selectViewPagerPage(page));
	}

	public static View getDescendantView(int ancestorResource, int targetResource, Activity activity) {
		return activity.findViewById(ancestorResource).findViewById(targetResource);
	}

	public static void checkViewMatchesText(final int viewResourceId, final int stringResourceId) {
		onView(withId(viewResourceId)).check(matches(withText(stringResourceId)));
	}

	public static void shouldStartSequence(boolean start) throws NoSuchFieldException, IllegalAccessException {
		shouldStartSequence(null, start);
	}

	public static void shouldStartSequence(TapTargetTopBar topBar, boolean start) throws NoSuchFieldException, IllegalAccessException {
		PrivateAccess.setMemberValue(TapTargetTopBar.class, topBar, "firsTimeSequence", start);
	}

	public static void setRtl(boolean rtl) {
		Context context = InstrumentationRegistry.getTargetContext();
		if (rtl) {
			updateLocale(context, "he", null);
		} else {
			updateLocale(context, "", null);
		}
	}

	/**
	 * @deprecated use {@link LayerMenuViewInteraction#performOpen()}
	 */
	@Deprecated
	public static void openLayerMenu() {
		onLayerMenuView()
				.performOpen();
	}

	/**
	 * @deprecated use {@link LayerMenuViewInteraction#performClose()}
	 */
	@Deprecated
	public static void closeLayerMenu() {
		onLayerMenuView()
				.performClose();
	}

	/**
	 * @deprecated use {@link LayerMenuViewInteraction#performSelectLayer(int)}
	 */
	@Deprecated
	public static void selectLayer(int listPosition) {
		onLayerMenuView()
				.performSelectLayer(listPosition);
	}

	/**
	 * @deprecated use {@link LayerMenuViewInteraction#performAddLayer()}
	 */
	@Deprecated
	public static void addNewLayer() {
		onLayerMenuView()
				.performAddLayer();
	}

	/**
	 * @deprecated use {@link LayerMenuViewInteraction#performDeleteLayer()}
	 */
	@Deprecated
	public static void deleteSelectedLayer() {
		onLayerMenuView()
				.performDeleteLayer();
	}

	public static Resources getResources() {
		return InstrumentationRegistry.getTargetContext().getResources();
	}

	public static Configuration getConfiguration() {
		return getResources().getConfiguration();
	}
}
