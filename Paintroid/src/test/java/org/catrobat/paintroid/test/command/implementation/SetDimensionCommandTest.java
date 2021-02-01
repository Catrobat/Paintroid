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

import org.catrobat.paintroid.command.implementation.SetDimensionCommand;
import org.catrobat.paintroid.model.LayerModel;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SetDimensionCommandTest {

	@Test
	public void testRun() {
		LayerModel layerModel = new LayerModel();
		SetDimensionCommand command = new SetDimensionCommand(3, 4);

		command.run(null, layerModel);

		assertThat(layerModel.getWidth(), is(3));
		assertThat(layerModel.getHeight(), is(4));
	}
}
