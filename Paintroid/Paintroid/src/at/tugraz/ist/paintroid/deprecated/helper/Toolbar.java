/*
 *   This file is part of Paintroid, a software part of the Catroid project.
 *   Copyright (C) 2010  Catroid development team
 *   <http://code.google.com/p/catroid/wiki/Credits>
 *
 *   Paintroid is free software: you can redistribute it and/or modify it
 *   under the terms of the GNU Affero General Public License as published
 *   by the Free Software Foundation, either version 3 of the License, or
 *   at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.paintroid.deprecated.helper;

import android.graphics.Color;
import android.graphics.Paint.Cap;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.MainActivity.ToolType;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.deprecated.graphic.DrawingSurface;
import at.tugraz.ist.paintroid.deprecated.graphic.DrawingSurface.ColorPickupListener;
import at.tugraz.ist.paintroid.dialog.BrushPickerDialog;
import at.tugraz.ist.paintroid.dialog.BrushPickerDialog.OnBrushChangedListener;
import at.tugraz.ist.paintroid.dialog.DialogHelp;
import at.tugraz.ist.paintroid.dialog.colorpicker.ColorPickerDialog;
import at.tugraz.ist.paintroid.dialog.colorpicker.ColorPickerDialog.OnColorPickedListener;

/**
 * Holds toolbar buttons and changes their functionality and apperance
 * 
 * Status: refactored 20.02.2011
 * 
 * @author PaintroidTeam
 * @version 0.6.4b
 */
@Deprecated
public class Toolbar implements OnClickListener, OnLongClickListener {

	protected MainActivity activity;
	protected TextView toolButton;
	protected TextView attributeButton1;
	protected TextView attributeButton2;
	protected Button undoButton;
	protected ToolType toolType;

	protected DrawingSurface drawingSurface;

	/**
	 * Constructor
	 * 
	 * Sets default button behavior and appearance
	 * 
	 * @param activity current activity
	 */
	public Toolbar(MainActivity activity) {
		this.activity = activity;

		// drawingSurface = (DrawingSurface) activity.findViewById(R.id.surfaceview);

		toolButton = (TextView) activity.findViewById(R.id.btn_Tool);
		toolButton.setOnClickListener(this);
		toolButton.setOnLongClickListener(this);
		toolButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.ic_menu_more_brush_64);
		toolButton.setBackgroundResource(R.drawable.attribute_button_selector);

		attributeButton1 = (TextView) activity.findViewById(R.id.btn_Parameter1);
		attributeButton1.setOnClickListener(this);
		attributeButton1.setOnLongClickListener(this);
		attributeButton1.setBackgroundColor(drawingSurface.getActiveColor());

		attributeButton2 = (TextView) activity.findViewById(R.id.btn_Parameter2);
		attributeButton2.setOnClickListener(this);
		attributeButton2.setOnLongClickListener(this);

		setStrokeAndShape(drawingSurface.getActiveBrush().stroke, drawingSurface.getActiveBrush().cap);

		undoButton = (Button) activity.findViewById(R.id.btn_Undo);
		undoButton.setOnClickListener(this);
		undoButton.setOnLongClickListener(this);
		undoButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.undo64);
		undoButton.setBackgroundResource(R.drawable.attribute_button_selector);

		toolType = ToolType.BRUSH;
	}

	/**
	 * Sets the buttons appearance if the stroke width or type changed
	 * 
	 * @param stroke brush width
	 * @param strokeType brush type
	 */
	public void setStrokeAndShape(int stroke, Cap strokeType) {
		if ((toolType != ToolType.BRUSH && toolType != ToolType.CURSOR) || strokeType == null) {
			return;
		}
		switch (strokeType) {
		case SQUARE:
			switch (stroke) {

			case 1:
				attributeButton2.setBackgroundResource(R.drawable.rect_1_32);
				break;
			case 5:
				attributeButton2.setBackgroundResource(R.drawable.rect_2_32);
				break;
			case 15:
				attributeButton2.setBackgroundResource(R.drawable.rect_3_32);
				break;
			case 25:
				attributeButton2.setBackgroundResource(R.drawable.rect_4_32);
				break;
			}
			break;
		case ROUND:
			switch (stroke) {

			case 1:
				attributeButton2.setBackgroundResource(R.drawable.circle_1_32);
				break;
			case 5:
				attributeButton2.setBackgroundResource(R.drawable.circle_2_32);
				break;
			case 15:
				attributeButton2.setBackgroundResource(R.drawable.circle_3_32);
				break;
			case 25:
				attributeButton2.setBackgroundResource(R.drawable.circle_4_32);
				break;
			}
			break;
		default:
			break;
		}
	}

	/**
	 * Handle all button events
	 * 
	 */
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_Tool:
			activity.callToolMenu();
			break;
		case R.id.btn_Parameter1:
			switch (toolType) {
			case MAGIC:
			case CURSOR:
			case BRUSH:
			case PIPETTE:
				OnColorPickedListener mColor = new OnColorPickedListener() {
					@Override
					public void colorChanged(int color) {
						if (color == Color.TRANSPARENT) {
							Log.d("PAINTROID", "Transparent set");
							attributeButton1.setBackgroundResource(R.drawable.transparent_64);
							drawingSurface.setActiveColor(color);
						} else {
							attributeButton1.setBackgroundColor(color);
							drawingSurface.setActiveColor(color);
						}
					}
				};
				ColorPickerDialog colorpicker = new ColorPickerDialog(activity, mColor);
				colorpicker.show();
				break;
			case ZOOM:
				drawingSurface.resetPerspective();
				break;
			case STAMP:
				// Rotate left
				if (!drawingSurface.rotateFloatingBox(-90)) {
					Toast toast = Toast.makeText(activity, R.string.warning_floating_box_rotate, Toast.LENGTH_SHORT);
					toast.show();
				}
				break;
			}
			break;
		case R.id.btn_Parameter2: // starting stroke chooser dialog
			switch (toolType) {
			case BRUSH:
			case CURSOR:
				OnBrushChangedListener mStroke = new OnBrushChangedListener() {
					@Override
					public void setCap(Cap cap) {
						drawingSurface.setActiveBrush(cap);
					}

					@Override
					public void setStroke(int stroke) {
						drawingSurface.setActiveBrush(stroke);
					}
				};

				BrushPickerDialog strokepicker = new BrushPickerDialog(activity, mStroke);
				strokepicker.show();
				break;
			case STAMP:
				// Rotate right
				if (!drawingSurface.rotateFloatingBox(90)) {
					Toast toast = Toast.makeText(activity, R.string.warning_floating_box_rotate, Toast.LENGTH_SHORT);
					toast.show();
				}
			}
			break;
		case R.id.btn_Undo:
			drawingSurface.undoOneStep();
			break;
		default:
			// set default option
			drawingSurface.setToolType(ToolType.BRUSH);
		}
	}

	/**
	 * Changes the button functionality and appearance regarding to the selected tool
	 * 
	 * @param tool selected tool
	 */
	public void setTool(ToolType tool) {
		if (tool != ToolType.UNDO && tool != ToolType.REDO) {
			resetAttributeButtons();
		}
		toolType = tool;
		switch (tool) {
		case BRUSH:
			toolButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.ic_menu_more_brush_64);
			attributeButton1.setVisibility(View.VISIBLE);
			if (drawingSurface.getActiveColor() == Color.TRANSPARENT) {
				attributeButton1.setBackgroundResource(R.drawable.transparent_64);
			} else {
				attributeButton1.setBackgroundColor(drawingSurface.getActiveColor());
			}
			attributeButton2.setVisibility(View.VISIBLE);
			setStrokeAndShape(drawingSurface.getActiveBrush().stroke, drawingSurface.getActiveBrush().cap);
			drawingSurface.setToolType(ToolType.BRUSH);
			break;
		case CURSOR:
			toolButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.cursor64);
			attributeButton1.setVisibility(View.VISIBLE);
			if (drawingSurface.getActiveColor() == Color.TRANSPARENT) {
				attributeButton1.setBackgroundResource(R.drawable.transparent_64);
			} else {
				attributeButton1.setBackgroundColor(drawingSurface.getActiveColor());
			}
			attributeButton2.setVisibility(View.VISIBLE);
			setStrokeAndShape(drawingSurface.getActiveBrush().stroke, drawingSurface.getActiveBrush().cap);
			drawingSurface.activateCursor();
			break;
		case SCROLL:
			toolButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.scroll64);
			drawingSurface.setToolType(ToolType.SCROLL);
			break;
		case ZOOM:
			toolButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.ic_menu_more_zoom_64);
			attributeButton1.setVisibility(View.VISIBLE);
			attributeButton1.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.zoom48, 0, 0);
			attributeButton1.setText(R.string.button_reset_zoom);
			drawingSurface.setToolType(ToolType.ZOOM);
			break;
		case PIPETTE:
			toolButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.pipette64);
			attributeButton1.setVisibility(View.VISIBLE);
			if (drawingSurface.getActiveColor() == Color.TRANSPARENT) {
				attributeButton1.setBackgroundResource(R.drawable.transparent_64);
			} else {
				attributeButton1.setBackgroundColor(drawingSurface.getActiveColor());
			}
			drawingSurface.setToolType(ToolType.PIPETTE);
			ColorPickupListener list = new ColorPickupListener() {

				@Override
				public void colorChanged(int color) {
					// set selected color when new color picked up
					attributeButton1.setBackgroundColor(color);
					drawingSurface.setActiveColor(color);
				}
			};
			// set the created listener
			drawingSurface.setColorPickupListener(list);
			break;
		case MAGIC:
			toolButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.magic64);
			drawingSurface.setToolType(ToolType.MAGIC);
			attributeButton1.setVisibility(View.VISIBLE);
			if (drawingSurface.getActiveColor() == Color.TRANSPARENT) {
				attributeButton1.setBackgroundResource(R.drawable.transparent_64);
			} else {
				attributeButton1.setBackgroundColor(drawingSurface.getActiveColor());
			}
			break;
		case UNDO:
			drawingSurface.undoOneStep();
			break;
		case REDO:
			drawingSurface.redoOneStep();
			break;
		case STAMP:
			toolButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.scroll64);
			attributeButton1.setVisibility(View.VISIBLE);
			attributeButton1.setBackgroundResource(0);
			attributeButton1.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.rotate_left_64_inactive);
			attributeButton2.setVisibility(View.VISIBLE);
			attributeButton2.setBackgroundResource(0);
			attributeButton2.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.rotate_right_64_inactive);
			drawingSurface.activateFloatingBox();
			break;
		case IMPORTPNG:
			toolType = ToolType.STAMP;
			toolButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.scroll64);
			activity.callImportPng();
			break;
		}
	}

	/**
	 * Resets the attribute buttons to empty hidden text views
	 */
	protected void resetAttributeButtons() {
		toolButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		attributeButton1.setVisibility(View.INVISIBLE);
		attributeButton1.setBackgroundColor(Color.TRANSPARENT);
		attributeButton1.setBackgroundResource(R.drawable.attribute_button_selector);
		attributeButton1.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		attributeButton1.setText("");
		attributeButton2.setVisibility(View.INVISIBLE);
		attributeButton2.setBackgroundColor(Color.TRANSPARENT);
		attributeButton2.setBackgroundResource(R.drawable.attribute_button_selector);
		attributeButton2.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		attributeButton2.setText("");
	}

	/**
	 * LongClick Listener for Help function
	 * 
	 */
	@Override
	public boolean onLongClick(View v) {
		DialogHelp help = new DialogHelp(activity, v.getId(), toolType);
		help.show();
		return true;
	}

	/**
	 * Activates the buttons for the use of the floating box if the floating box is active
	 */
	public void activateFloatingBoxButtons() {
		if (toolType == ToolType.STAMP) {
			attributeButton1.setVisibility(View.VISIBLE);
			attributeButton1.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.rotate_left_64);
			attributeButton1.setBackgroundResource(R.drawable.attribute_button_selector);
			attributeButton2.setVisibility(View.VISIBLE);
			attributeButton2.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.rotate_right_64);
			attributeButton2.setBackgroundResource(R.drawable.attribute_button_selector);
		}
	}

}
