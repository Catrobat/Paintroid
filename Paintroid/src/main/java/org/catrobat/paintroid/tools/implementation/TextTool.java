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

package org.catrobat.paintroid.tools.implementation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.tools.ContextCallback;
import org.catrobat.paintroid.tools.ToolPaint;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.tools.options.TextToolOptionsView;
import org.catrobat.paintroid.tools.options.ToolOptionsVisibilityController;

import androidx.annotation.VisibleForTesting;

public class TextTool extends BaseToolWithRectangleShape {

	private static final boolean ROTATION_ENABLED = true;
	private static final boolean RESIZE_POINTS_VISIBLE = true;
	@VisibleForTesting
	public static final int TEXT_SIZE_MAGNIFICATION_FACTOR = 3;
	@VisibleForTesting
	public static final int BOX_OFFSET = 20;
	@VisibleForTesting
	public static final float MARGIN_TOP = 50.0f;
	private static final float ITALIC_TEXT_SKEW = -0.25f;
	private static final float DEFAULT_TEXT_SKEW = 0.0f;
	private static final String BUNDLE_TOOL_UNDERLINED = "BUNDLE_TOOL_UNDERLINED";
	private static final String BUNDLE_TOOL_ITALIC = "BUNDLE_TOOL_ITALIC";
	private static final String BUNDLE_TOOL_BOLD = "BUNDLE_TOOL_BOLD";
	private static final String BUNDLE_TOOL_TEXT = "BUNDLE_TOOL_TEXT";
	private static final String BUNDLE_TOOL_TEXT_SIZE = "BUNDLE_TOOL_TEXT_SIZE";
	private static final String BUNDLE_TOOL_FONT = "BUNDLE_TOOL_FONT";

	@VisibleForTesting
	public final Paint textPaint;
	private final Typeface stc;
	private final Typeface dubai;

	@VisibleForTesting
	public String text = "";
	@VisibleForTesting
	public String font = "Monospace";
	@VisibleForTesting
	public boolean underlined = false;
	@VisibleForTesting
	public boolean italic = false;
	@VisibleForTesting
	public boolean bold = false;
	@VisibleForTesting
	public int textSize = 20;
	private TextToolOptionsView textToolOptionsView;

	public TextTool(TextToolOptionsView textToolOptionsView, ContextCallback contextCallback, ToolOptionsVisibilityController toolOptionsViewController,
			ToolPaint toolPaint, Workspace workspace, CommandManager commandManager) {
		super(contextCallback, toolOptionsViewController, toolPaint, workspace, commandManager);

		this.textToolOptionsView = textToolOptionsView;

		this.rotationEnabled = ROTATION_ENABLED;
		this.resizePointsVisible = RESIZE_POINTS_VISIBLE;

		stc = contextCallback.getFont(R.font.stc_regular);
		dubai = contextCallback.getFont(R.font.dubai);

		textPaint = new Paint();
		initializePaint();

		createAndSetBitmap();
		resetBoxPosition();

		toolOptionsViewController.setCallback(new ToolOptionsVisibilityController.Callback() {
			@Override
			public void onHide() {
				createAndSetBitmap();
			}

			@Override
			public void onShow() {
				createAndSetBitmap();
			}
		});

		TextToolOptionsView.Callback callback =
				new TextToolOptionsView.Callback() {
					@Override
					public void setText(String text) {
						TextTool.this.text = text;
						createAndSetBitmap();
					}

					@Override
					public void setFont(String font) {
						TextTool.this.font = font;
						updateTypeface();
						createAndSetBitmap();
					}

					@Override
					public void setUnderlined(boolean underlined) {
						TextTool.this.underlined = underlined;
						textPaint.setUnderlineText(TextTool.this.underlined);
						createAndSetBitmap();
					}

					@Override
					public void setItalic(boolean italic) {
						TextTool.this.italic = italic;
						updateTypeface();
						createAndSetBitmap();
					}

					@Override
					public void setBold(boolean bold) {
						TextTool.this.bold = bold;
						textPaint.setFakeBoldText(TextTool.this.bold);
						createAndSetBitmap();
					}

					@Override
					public void setTextSize(int size) {
						textSize = size;
						textPaint.setTextSize(textSize * TEXT_SIZE_MAGNIFICATION_FACTOR);
						createAndSetBitmap();
					}

					@Override
					public void hideToolOptions() {
						TextTool.this.toolOptionsViewController.hide();
					}
				};

		textToolOptionsView.setCallback(callback);
		toolOptionsViewController.showDelayed();
	}

	private void initializePaint() {
		textPaint.setAntiAlias(DEFAULT_ANTIALIASING_ON);

		textPaint.setColor(toolPaint.getPreviewColor());
		textPaint.setTextSize(textSize * TEXT_SIZE_MAGNIFICATION_FACTOR);
		textPaint.setUnderlineText(underlined);
		textPaint.setFakeBoldText(bold);

		updateTypeface();
	}

	private void createAndSetBitmap() {
		String[] multilineText = getMultilineText();
		float textDescent = textPaint.descent();
		float textAscent = textPaint.ascent();

		float upperBoxEdge = toolPosition.y - boxHeight / 2.0f;
		float textHeight = textDescent - textAscent;
		boxHeight = textHeight * multilineText.length + 2 * BOX_OFFSET;
		toolPosition.y = upperBoxEdge + boxHeight / 2.0f;

		float maxTextWidth = 0;
		for (String str : multilineText) {
			maxTextWidth = Math.max(maxTextWidth, textPaint.measureText(str));
		}
		boxWidth = maxTextWidth + 2 * BOX_OFFSET;

		Bitmap bitmap = Bitmap.createBitmap((int) boxWidth, (int) boxHeight, Bitmap.Config.ARGB_8888);
		Canvas drawCanvas = new Canvas(bitmap);

		for (int i = 0; i < multilineText.length; i++) {
			drawCanvas.drawText(multilineText[i], BOX_OFFSET, BOX_OFFSET - textAscent + textHeight * i, textPaint);
		}

		setBitmap(bitmap);
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
		bundle.putBoolean(BUNDLE_TOOL_UNDERLINED, underlined);
		bundle.putBoolean(BUNDLE_TOOL_ITALIC, italic);
		bundle.putBoolean(BUNDLE_TOOL_BOLD, bold);
		bundle.putString(BUNDLE_TOOL_TEXT, text);
		bundle.putInt(BUNDLE_TOOL_TEXT_SIZE, textSize);
		bundle.putString(BUNDLE_TOOL_FONT, font);
	}

	@Override
	public void onRestoreInstanceState(Bundle bundle) {
		super.onRestoreInstanceState(bundle);
		underlined = bundle.getBoolean(BUNDLE_TOOL_UNDERLINED, underlined);
		italic = bundle.getBoolean(BUNDLE_TOOL_ITALIC, italic);
		bold = bundle.getBoolean(BUNDLE_TOOL_BOLD, bold);
		text = bundle.getString(BUNDLE_TOOL_TEXT, text);
		textSize = bundle.getInt(BUNDLE_TOOL_TEXT_SIZE, textSize);
		font = bundle.getString(BUNDLE_TOOL_FONT, font);

		textToolOptionsView.setState(bold, italic, underlined, text, textSize, font);
		textPaint.setUnderlineText(underlined);
		textPaint.setFakeBoldText(bold);
		updateTypeface();
		createAndSetBitmap();
	}

	private void updateTypeface() {
		int style = italic ? Typeface.ITALIC : Typeface.NORMAL;
		final float textSkewX = italic ? ITALIC_TEXT_SKEW : DEFAULT_TEXT_SKEW;

		switch (font) {
			case "Sans Serif":
				textPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, style));
				break;
			case "Serif":
				textPaint.setTypeface(Typeface.create(Typeface.SERIF, style));
				break;
			case "Monospace":
				textPaint.setTypeface(Typeface.create(Typeface.MONOSPACE, style));
				break;
			case "STC":
				try {
					textPaint.setTypeface(stc);
					textPaint.setTextSkewX(textSkewX);
				} catch (Exception e) {
					Log.e("Can't set custom font", "stc_regular");
				}
				break;
			case "Dubai":
				try {
					textPaint.setTypeface(dubai);
					textPaint.setTextSkewX(textSkewX);
				} catch (Exception e) {
					Log.e("Can't set custom font", "dubai");
				}
				break;
		}
	}

	private void changeTextColor() {
		float width = boxWidth;
		float height = boxHeight;
		PointF position = new PointF(toolPosition.x, toolPosition.y);
		textPaint.setColor(toolPaint.getPreviewColor());
		createAndSetBitmap();
		toolPosition.set(position);
		boxWidth = width;
		boxHeight = height;
	}

	@Override
	protected void resetInternalState() {
	}

	@VisibleForTesting
	public String[] getMultilineText() {
		return text.split("\n");
	}

	@Override
	public void onClickOnButton() {
		highlightBox();
		PointF toolPosition = new PointF(this.toolPosition.x, this.toolPosition.y);
		Command command = commandFactory.createTextToolCommand(getMultilineText(), textPaint, BOX_OFFSET, boxWidth,
				boxHeight, toolPosition, boxRotation);
		commandManager.addCommand(command);
	}

	@VisibleForTesting
	public void resetBoxPosition() {
		if (workspace.getScale() <= 1) {
			toolPosition.x = workspace.getWidth() / 2.0f;
			toolPosition.y = boxHeight / 2.0f + MARGIN_TOP;
		}
	}

	@Override
	public ToolType getToolType() {
		return ToolType.TEXT;
	}

	@Override
	public void changePaintColor(int color) {
		super.changePaintColor(color);
		changeTextColor();
	}
}
