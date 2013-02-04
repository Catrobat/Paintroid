package org.catrobat.paintroid.tools.implementation;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.implementation.StampCommand;
import org.catrobat.paintroid.dialog.colorpicker.ColorPickerDialog.OnColorPickedListener;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.catrobat.paintroid.ui.button.ToolbarButton.ToolButtonIDs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.RectF;

public class RectangleFillTool extends BaseToolWithRectangleShape {

	private static final boolean ROTATION_ENABLED = true;
	private static final boolean RESPECT_IMAGE_BOUNDS = false;
	private static final float SHAPE_OFFSET = 10f;

	private BaseShape mBaseShape;
	private ShapeDrawType mShapeDrawType;

	public static enum ShapeDrawType {
		OUTLINE, FILL
	};

	public static enum BaseShape {
		RECTANGLE, OVAL
	};

	public RectangleFillTool(Context context, ToolType toolType) {
		super(context, toolType);

		setRotationEnabled(ROTATION_ENABLED);
		setRespectImageBounds(RESPECT_IMAGE_BOUNDS);

		mBaseShape = BaseShape.RECTANGLE;
		mShapeDrawType = ShapeDrawType.FILL;

		mColor = new OnColorPickedListener() {
			@Override
			public void colorChanged(int color) {
				changePaintColor(color);
				createAndSetBitmap(PaintroidApplication.DRAWING_SURFACE);
			}
		};

		createAndSetBitmap(PaintroidApplication.DRAWING_SURFACE);
	}

	@Override
	public void setDrawPaint(Paint paint) {
		// necessary because of timing in MainActivity and Eraser
		super.setDrawPaint(paint);
		createAndSetBitmap(PaintroidApplication.DRAWING_SURFACE);
	}

	protected void createAndSetBitmap(DrawingSurface drawingSurface) {
		Bitmap bitmap = Bitmap.createBitmap((int) mBoxWidth, (int) mBoxHeight,
				Bitmap.Config.ARGB_8888);
		Canvas drawCanvas = new Canvas(bitmap);

		RectF shapeRect = new RectF(SHAPE_OFFSET, SHAPE_OFFSET, mBoxWidth
				- SHAPE_OFFSET, mBoxHeight - SHAPE_OFFSET);
		Paint drawPaint = new Paint();

		drawPaint.setColor(mCanvasPaint.getColor());
		drawPaint.setAntiAlias(DEFAULT_ANTIALISING_ON);

		switch (mShapeDrawType) {
		case FILL:
			drawPaint.setStyle(Style.FILL);
			break;
		case OUTLINE:
			drawPaint.setStyle(Style.STROKE);
			float strokeWidth = mBitmapPaint.getStrokeWidth();
			shapeRect = new RectF(SHAPE_OFFSET + (strokeWidth / 2),
					SHAPE_OFFSET + (strokeWidth / 2), mBoxWidth - SHAPE_OFFSET
							- (strokeWidth / 2), mBoxHeight - SHAPE_OFFSET
							- (strokeWidth / 2));
			drawPaint.setStrokeWidth(strokeWidth);
			drawPaint.setStrokeCap(Paint.Cap.BUTT);
			break;
		default:
			break;
		}

		switch (mBaseShape) {
		case RECTANGLE:
			drawCanvas.drawRect(shapeRect, drawPaint);
			break;
		case OVAL:
			drawCanvas.drawOval(shapeRect, drawPaint);
			break;
		default:
			break;
		}

		mDrawingBitmap = bitmap;
	}

	@Override
	protected void onClickInBox() {
		Point intPosition = new Point((int) mToolPosition.x,
				(int) mToolPosition.y);
		Command command = new StampCommand(mDrawingBitmap, intPosition,
				mBoxWidth, mBoxHeight, mBoxRotation);
		((StampCommand) command).addObserver(this);
		mProgressDialog.show();
		PaintroidApplication.COMMAND_MANAGER.commitCommand(command);
	}

	@Override
	public void attributeButtonClick(ToolButtonIDs buttonNumber) {
		switch (buttonNumber) {
		case BUTTON_ID_PARAMETER_TOP:
		case BUTTON_ID_PARAMETER_BOTTOM_2:
			showColorPicker();
			break;
		default:
			break;
		}
	}

	@Override
	public int getAttributeButtonResource(ToolButtonIDs buttonNumber) {
		switch (buttonNumber) {
		case BUTTON_ID_PARAMETER_TOP:
			return getStrokeColorResource();
		case BUTTON_ID_PARAMETER_BOTTOM_2:
			return R.drawable.icon_menu_color_palette;
		default:
			return super.getAttributeButtonResource(buttonNumber);
		}
	}

	@Override
	protected void drawToolSpecifics(Canvas canvas) {
		// TODO Auto-generated method stub
	}

	@Override
	public void resetInternalState() {
		// TODO Auto-generated method stub
	}
}
