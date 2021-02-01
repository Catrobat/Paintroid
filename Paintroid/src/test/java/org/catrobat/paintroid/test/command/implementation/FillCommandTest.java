package org.catrobat.paintroid.test.command.implementation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import org.catrobat.paintroid.command.implementation.FillCommand;
import org.catrobat.paintroid.contract.LayerContracts;
import org.catrobat.paintroid.model.LayerModel;
import org.catrobat.paintroid.tools.helper.FillAlgorithm;
import org.catrobat.paintroid.tools.helper.FillAlgorithmFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FillCommandTest {
	@Mock
	private FillAlgorithmFactory fillAlgorithmFactory;

	@Mock
	private FillAlgorithm fillAlgorithm;

	@Mock
	private Point clickedPixel;

	@Mock
	private Paint paint;

	@Before
	public void setUp() {
		when(fillAlgorithmFactory.createFillAlgorithm())
				.thenReturn(fillAlgorithm);
	}

	@Test
	public void testSetUp() {
		FillCommand command = new FillCommand(fillAlgorithmFactory, clickedPixel, paint, 0);

		assertNotNull(command);
		verifyZeroInteractions(fillAlgorithmFactory, clickedPixel, paint);
	}

	@Test
	public void testRun() {
		clickedPixel.x = 3;
		clickedPixel.y = 5;
		FillCommand command = new FillCommand(fillAlgorithmFactory, clickedPixel, paint, 0.5f);
		Canvas canvas = mock(Canvas.class);
		LayerModel layerModel = new LayerModel();
		LayerContracts.Layer layer = mock(LayerContracts.Layer.class);
		Bitmap bitmap = mock(Bitmap.class);
		layerModel.setCurrentLayer(layer);

		when(layer.getBitmap()).thenReturn(bitmap);
		when(bitmap.getPixel(3, 5)).thenReturn(Color.RED);
		when(paint.getColor()).thenReturn(Color.BLUE);

		command.run(canvas, layerModel);

		verify(fillAlgorithm).setParameters(bitmap, clickedPixel, Color.BLUE, Color.RED, 0.5f);
		verify(fillAlgorithm).performFilling();
	}
}
