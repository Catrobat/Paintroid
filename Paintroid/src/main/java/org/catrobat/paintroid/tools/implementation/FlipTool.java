/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.tools.implementation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.implementation.FlipCommand;
import org.catrobat.paintroid.command.implementation.FlipCommand.FlipDirection;
import org.catrobat.paintroid.command.implementation.LayerCommand;
import org.catrobat.paintroid.dialog.IndeterminateProgressDialog;
import org.catrobat.paintroid.dialog.LayersDialog;
import org.catrobat.paintroid.listener.LayerListener;
import org.catrobat.paintroid.tools.Layer;
import org.catrobat.paintroid.tools.ToolType;

public class FlipTool extends BaseTool {

	private ImageButton mFlipBtnHorizontal;
	private ImageButton mFlipBtnVertical;
	private LinearLayout mFlipButtonsLayout;

	public FlipTool(Context context, ToolType toolType) {
		super(context, toolType);
	}

	@Override
	public boolean handleDown(PointF coordinate) {
		return false;
	}

	@Override
	public boolean handleMove(PointF coordinate) {
		return false;
	}

	@Override
	public boolean handleUp(PointF coordinate) {
		return false;
	}

	@Override
	public void resetInternalState() {
	}

	private void flip(FlipDirection flipDirection) {
		Command command = new FlipCommand(flipDirection);
		IndeterminateProgressDialog.getInstance().show();
		((FlipCommand) command).addObserver(this);
		Layer layer = LayerListener.getInstance().getCurrentLayer();
		PaintroidApplication.commandManager.commitCommandToLayer(new LayerCommand(layer), command);
	}

	@Override
	public void draw(Canvas canvas) {
	}

	@Override
	public void setupToolOptions() {
		mFlipButtonsLayout  = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.flip_tool_buttons, null);

		mFlipBtnHorizontal = (ImageButton) mFlipButtonsLayout.findViewById(R.id.flip_horizontal_btn);
		mFlipBtnHorizontal.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						v.setBackgroundColor(mContext.getResources().getColor(R.color.bottom_bar_button_activated));
						break;
					case MotionEvent.ACTION_UP:
						v.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
						flip(FlipDirection.FLIP_HORIZONTAL);
						break;
					default:
						return false;
				}
				return true;
			}
		});
		mFlipBtnVertical = (ImageButton) mFlipButtonsLayout.findViewById(R.id.flip_vertical_btn);
		mFlipBtnVertical.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						v.setBackgroundColor(mContext.getResources().getColor(R.color.bottom_bar_button_activated));
						break;
					case MotionEvent.ACTION_UP:
						v.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
						flip(FlipDirection.FLIP_VERTICAL);
						break;
					default:
						return false;
				}
				return true;
			}
		});

		mToolSpecificOptionsLayout.addView(mFlipButtonsLayout);
		mToolSpecificOptionsLayout.post(new Runnable() {
			@Override
			public void run() {
				toggleShowToolOptions();
			}
		});
	}

}
