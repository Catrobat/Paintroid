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

package org.catrobat.paintroid.command.implementation;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.PointF;

import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.CommandFactory;
import org.catrobat.paintroid.command.implementation.FlipCommand.FlipDirection;
import org.catrobat.paintroid.command.implementation.RotateCommand.RotateDirection;
import org.catrobat.paintroid.common.CommonFactory;

public class DefaultCommandFactory implements CommandFactory {

	private CommonFactory commonFactory = new CommonFactory();

	@Override
	public Command createInitCommand(int width, int height) {
		CompositeCommand command = new CompositeCommand();
		command.addCommand(new SetDimensionCommand(width, height));
		command.addCommand(new AddLayerCommand(commonFactory));
		return command;
	}

	@Override
	public Command createInitCommand(Bitmap bitmap) {
		CompositeCommand command = new CompositeCommand();
		command.addCommand(new SetDimensionCommand(bitmap.getWidth(), bitmap.getHeight()));
		command.addCommand(new LoadCommand(bitmap));
		return command;
	}

	@Override
	public Command createAddLayerCommand() {
		return new AddLayerCommand(commonFactory);
	}

	@Override
	public Command createSelectLayerCommand(int position) {
		return new SelectLayerCommand(position);
	}

	@Override
	public Command createRemoveLayerCommand(int index) {
		return new RemoveLayerCommand(index);
	}

	@Override
	public Command createReorderLayersCommand(int position, int swapWith) {
		return new ReorderLayersCommand(position, swapWith);
	}

	@Override
	public Command createMergeLayersCommand(int position, int mergeWith) {
		return new MergeLayersCommand(position, mergeWith);
	}

	@Override
	public Command createRotateCommand(RotateDirection rotateDirection) {
		return new RotateCommand(rotateDirection);
	}

	@Override
	public Command createFlipCommand(FlipDirection flipDirection) {
		return new FlipCommand(flipDirection);
	}

	@Override
	public Command createResizeCommand(int resizeCoordinateXLeft, int resizeCoordinateYTop, int resizeCoordinateXRight, int resizeCoordinateYBottom, int maximumBitmapResolution) {
		return new ResizeCommand(resizeCoordinateXLeft, resizeCoordinateYTop, resizeCoordinateXRight, resizeCoordinateYBottom, maximumBitmapResolution);
	}

	@Override
	public Command createPointCommand(Paint paint, PointF coordinate) {
		return new PointCommand(
				commonFactory.createPaint(paint),
				commonFactory.createPointF(coordinate)
		);
	}
}
