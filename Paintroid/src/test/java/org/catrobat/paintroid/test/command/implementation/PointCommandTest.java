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

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import org.catrobat.paintroid.command.implementation.PointCommand;
import org.catrobat.paintroid.contract.LayerContracts;
import org.catrobat.paintroid.model.LayerModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class)
public class PointCommandTest {
	@Mock
	private Paint paint;

	@Mock
	private PointF point;

	@InjectMocks
	private PointCommand command;

	@Test
	public void testSetUp() {
		verifyZeroInteractions(paint, point);
	}

	@Test
	public void testDrawOnePoint() {
		Canvas canvas = mock(Canvas.class);
		LayerContracts.Model model = new LayerModel();

		point.x = 3;
		point.y = 7;

		command.run(canvas, model);

		verify(canvas).drawPoint(3, 7, paint);
		verifyZeroInteractions(paint);
		verifyZeroInteractions(point);
	}
}
