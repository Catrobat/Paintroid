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

package org.catrobat.paintroid.test.command.implementation;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.PointF;

import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.implementation.AddLayerCommand;
import org.catrobat.paintroid.command.implementation.CompositeCommand;
import org.catrobat.paintroid.command.implementation.CropCommand;
import org.catrobat.paintroid.command.implementation.DefaultCommandFactory;
import org.catrobat.paintroid.command.implementation.FlipCommand;
import org.catrobat.paintroid.command.implementation.MergeLayersCommand;
import org.catrobat.paintroid.command.implementation.PointCommand;
import org.catrobat.paintroid.command.implementation.RemoveLayerCommand;
import org.catrobat.paintroid.command.implementation.ReorderLayersCommand;
import org.catrobat.paintroid.command.implementation.ResizeCommand;
import org.catrobat.paintroid.command.implementation.RotateCommand;
import org.catrobat.paintroid.command.implementation.SelectLayerCommand;
import org.junit.Before;
import org.junit.Test;

import static org.catrobat.paintroid.command.implementation.FlipCommand.FlipDirection.FLIP_HORIZONTAL;
import static org.catrobat.paintroid.command.implementation.RotateCommand.RotateDirection.ROTATE_LEFT;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class DefaultCommandFactoryTest {

	private DefaultCommandFactory commandFactory;

	@Before
	public void setUp() {
		commandFactory = new DefaultCommandFactory();
	}

	@Test
	public void testCreateInitCommand() {
		Command command = commandFactory.createInitCommand(10, 20);
		assertThat(command, is(instanceOf(CompositeCommand.class)));
	}

	@Test
	public void testCreateInitCommandWithBitmap() {
		Command command = commandFactory.createInitCommand(mock(Bitmap.class));
		assertThat(command, is(instanceOf(CompositeCommand.class)));
	}

	@Test
	public void testCreateResetCommand() {
		Command command = commandFactory.createResetCommand();
		assertThat(command, is(instanceOf(CompositeCommand.class)));
	}

	@Test
	public void testCreateAddLayerCommand() {
		Command command = commandFactory.createAddLayerCommand();
		assertThat(command, is(instanceOf(AddLayerCommand.class)));
	}

	@Test
	public void testCreateSelectLayerCommand() {
		Command command = commandFactory.createSelectLayerCommand(0);
		assertThat(command, is(instanceOf(SelectLayerCommand.class)));
	}

	@Test
	public void testCreateRemoveLayerCommand() {
		Command command = commandFactory.createRemoveLayerCommand(0);
		assertThat(command, is(instanceOf(RemoveLayerCommand.class)));
	}

	@Test
	public void testCreateReorderLayersCommand() {
		Command command = commandFactory.createReorderLayersCommand(0, 1);
		assertThat(command, is(instanceOf(ReorderLayersCommand.class)));
	}

	@Test
	public void testCreateMergeLayersCommand() {
		Command command = commandFactory.createMergeLayersCommand(0, 1);
		assertThat(command, is(instanceOf(MergeLayersCommand.class)));
	}

	@Test
	public void testCreateRotateCommand() {
		Command command = commandFactory.createRotateCommand(ROTATE_LEFT);
		assertThat(command, is(instanceOf(RotateCommand.class)));
	}

	@Test
	public void testCreateFlipCommand() {
		Command command = commandFactory.createFlipCommand(FLIP_HORIZONTAL);
		assertThat(command, is(instanceOf(FlipCommand.class)));
	}

	@Test
	public void testCreateCropCommand() {
		Command command = commandFactory.createCropCommand(0, 0, 1, 1, 2);
		assertThat(command, is(instanceOf(CropCommand.class)));
	}

	@Test
	public void testCreatePointCommand() {
		PointF coordinate = mock(PointF.class);
		Paint paint = mock(Paint.class);

		Command command = commandFactory.createPointCommand(paint, coordinate);
		assertThat(command, is(instanceOf(PointCommand.class)));
	}

	@Test
	public void testCreateResizeCommand() {
		Command command = commandFactory.createResizeCommand(10, 20);
		assertThat(command, is(instanceOf(ResizeCommand.class)));
	}
}
