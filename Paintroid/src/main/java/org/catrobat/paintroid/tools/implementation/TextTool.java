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

package org.catrobat.paintroid.tools.implementation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ToggleButton;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.implementation.LayerCommand;
import org.catrobat.paintroid.command.implementation.TextToolCommand;
import org.catrobat.paintroid.dialog.IndeterminateProgressDialog;
import org.catrobat.paintroid.listener.LayerListener;
import org.catrobat.paintroid.listener.TextToolOptionsListener;
import org.catrobat.paintroid.tools.Layer;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.DrawingSurface;

public class TextTool extends BaseToolWithRectangleShape {

	private static final boolean ROTATION_ENABLED = true;
	private static final boolean RESPECT_IMAGE_BORDERS = false;
	private static final boolean RESIZE_POINTS_VISIBLE = true;

	private TextToolOptionsListener.OnTextToolOptionsChangedListener onTextToolOptionsChangedListener;
	private View textToolOptionsView;
	private String text = "";
	private String[] multilineText = {""};
	private String font = "Monospace";
	private boolean underlined = false;
	private boolean italic = false;
	private boolean bold = false;
	private int textSize = 20;
	private int textSizeMagnificationFactor = 3;
	private int boxOffset = 20;
	private float marginTop = 50.0f;
	private Paint textPaint;
	private boolean paintInitialized = false;

	public TextTool(Context context, ToolType toolType) {
		super(context, toolType);

		setRotationEnabled(ROTATION_ENABLED);
		setRespectImageBounds(RESPECT_IMAGE_BORDERS);
		setResizePointsVisible(RESIZE_POINTS_VISIBLE);

		paintInitialized = initializePaint();

		createAndSetBitmap();
		resetBoxPosition();
	}

	public boolean initializePaint() {
		textPaint = new Paint();
		textPaint.setAntiAlias(DEFAULT_ANTIALISING_ON);

		textPaint.setColor(canvasPaint.getColor());
		textPaint.setTextSize(textSize * textSizeMagnificationFactor);
		textPaint.setUnderlineText(underlined);
		textPaint.setFakeBoldText(bold);

		updateTypeface();
		return true;
	}

	public void createAndSetBitmap() {
		float textDescent = textPaint.descent();
		float textAscent = textPaint.ascent();

		float upperBoxEdge = toolPosition.y - boxHeight / 2.0f;
		float textHeight = textDescent - textAscent;
		boxHeight = textHeight * multilineText.length + 2 * boxOffset;
		toolPosition.y = upperBoxEdge + boxHeight / 2.0f;

		float maxTextWidth = 0;
		for (String str : multilineText) {
			float textWidth = textPaint.measureText(str);
			if (textWidth > maxTextWidth) {
				maxTextWidth = textWidth;
			}
		}
		boxWidth = maxTextWidth + 2 * boxOffset;

		Bitmap bitmap = Bitmap.createBitmap((int) boxWidth, (int) boxHeight,
				Bitmap.Config.ARGB_8888);
		Canvas drawCanvas = new Canvas(bitmap);

		for (int i = 0; i < multilineText.length; i++) {
			drawCanvas.drawText(multilineText[i], boxOffset, boxOffset - textAscent + textHeight * i, textPaint);
		}

		createOverlayButton();
		setBitmap(bitmap);
	}

	protected void setupOnTextToolDialogChangedListener() {
		onTextToolOptionsChangedListener = new TextToolOptionsListener.OnTextToolOptionsChangedListener() {
			@Override
			public void setText(String text) {
				TextTool.this.text = text;
				multilineText = TextTool.this.text.split("\n");
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
				textPaint.setTextSize(textSize * textSizeMagnificationFactor);
				createAndSetBitmap();
			}
		};
		TextToolOptionsListener.getInstance().setOnTextToolOptionsChangedListener(onTextToolOptionsChangedListener);
	}

	public void updateTypeface() {
		int style;

		if (italic) {
			style = Typeface.ITALIC;
		} else {
			style = Typeface.NORMAL;
		}

		if (font.equals("Sans Serif")) {
			textPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, style));
		} else if (font.equals("Serif")) {
			textPaint.setTypeface(Typeface.create(Typeface.SERIF, style));
		} else if (font.equals("Monospace")) {
			textPaint.setTypeface(Typeface.create(Typeface.MONOSPACE, style));
		} else if (font.equals("Alarabiya")) {
			try {
				textPaint.setTypeface(Typeface.createFromAsset(context.getAssets(), "Alarabiya.ttf"));
				if (style == Typeface.ITALIC) {
					textPaint.setTextSkewX(-0.25f);
				} else {
					textPaint.setTextSkewX(0.0f);
				}
			} catch (Exception e) {
				Log.e("Can't set custom font", "Alarabiya");
			}
		} else if (font.equals("Dubai")) {
			try {
				textPaint.setTypeface(Typeface.createFromAsset(context.getAssets(), "Dubai.TTF"));
				if (style == Typeface.ITALIC) {
					textPaint.setTextSkewX(-0.25f);
				} else {
					textPaint.setTextSkewX(0.0f);
				}
			} catch (Exception e) {
				Log.e("Can't set custom font", "Dubai");
			}
		}

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			textPaint.setTextSkewX(0.0f);
			if (font.equals("Monospace")) {
				textPaint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL));
				if (style == Typeface.ITALIC) {
					textPaint.setTextSkewX(-0.25f);
				}
			}
		}
	}

	protected void changeTextColor() {
		float width = boxWidth;
		float height = boxHeight;
		PointF position = new PointF(toolPosition.x, toolPosition.y);
		textPaint.setColor(canvasPaint.getColor());
		createAndSetBitmap();
		toolPosition = position;
		boxWidth = width;
		boxHeight = height;
	}

	@Override
	protected void resetInternalState() {
	}

	@Override
	protected void onClickInBox() {
		highlightBox();
		PointF toolPosition = new PointF(this.toolPosition.x, this.toolPosition.y);
		Command command = new TextToolCommand(multilineText, textPaint, boxOffset, boxWidth, boxHeight,
				toolPosition, boxRotation);
		((TextToolCommand) command).addObserver(this);
		IndeterminateProgressDialog.getInstance().show();

		Layer layer = LayerListener.getInstance().getCurrentLayer();
		PaintroidApplication.commandManager.commitCommandToLayer(new LayerCommand(layer), command);
	}

	public void resetBoxPosition() {
		DrawingSurface surface = PaintroidApplication.drawingSurface;
		toolPosition.x = surface.getBitmapWidth() / 2.0f;
		toolPosition.y = boxHeight / 2.0f + marginTop;
	}

	@Override
	protected void drawToolSpecifics(Canvas canvas) {
	}

	@SuppressLint("InflateParams")
	@Override
	public void setupToolOptions() {
		LayoutInflater inflater = LayoutInflater.from(context);
		textToolOptionsView = inflater.inflate(R.layout.dialog_text_tool, null);

		ToggleButton underlinedButton = (ToggleButton) textToolOptionsView.findViewById(R.id.text_tool_dialog_toggle_underlined);
		underlinedButton.setPaintFlags(underlinedButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

		toolSpecificOptionsLayout.addView(textToolOptionsView);
		TextToolOptionsListener.init(context, textToolOptionsView);
		setupOnTextToolDialogChangedListener();

		toolSpecificOptionsLayout.post(new Runnable() {
			@Override
			public void run() {
				toggleShowToolOptions();
			}
		});
	}

	@Override
	public void toggleShowToolOptions() {
		super.toggleShowToolOptions();
		if (paintInitialized) {
			createAndSetBitmap();
		}
	}

	@Override
	public void setDrawPaint(Paint paint) {
		super.setDrawPaint(paint);
		textPaint.setColor(canvasPaint.getColor());
	}

	@Override
	public void changePaintColor(int color) {
		super.changePaintColor(color);
		changeTextColor();
	}
}
