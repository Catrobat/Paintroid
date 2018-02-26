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

package org.catrobat.paintroid.test.junit.stubs;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.test.InstrumentationRegistry;

import org.catrobat.paintroid.command.implementation.BaseCommand;
import org.catrobat.paintroid.tools.Layer;

import java.util.ArrayList;
import java.util.List;

public class BaseCommandStub extends BaseCommand {

	private BaseStub baseStub;

	public BaseCommandStub() {
		super();
		baseStub = new BaseStub();
	}

	public BaseCommandStub(Paint paint) {
		super(paint);
		baseStub = new BaseStub();
	}

	@Override
	public void run(Canvas canvas, Layer layer) {
		Throwable throwable = new Throwable();
		List<Object> arguments = new ArrayList<>();
		arguments.add(canvas);
		arguments.add(layer);
		baseStub.addCall(throwable, arguments);
	}

	public void storeBitmapStub() {
		storeBitmap(InstrumentationRegistry.getTargetContext().getCacheDir());
	}
}
