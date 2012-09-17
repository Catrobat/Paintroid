//package org.catrobat.paintroid.tools.implementation;
//
//import org.catrobat.paintroid.R;
//import org.catrobat.paintroid.ui.DrawingSurface;
//import org.catrobat.paintroid.ui.button.ToolbarButton.ToolButtonIDs;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.Paint.Style;
//import android.graphics.RectF;
//
//public class RectangleFillTool extends BaseToolWithRectangleShape {
//
//	private static final float SHAPE_OFFSET = 10f;
//	private static final boolean ROTATION_ENABLED = true;
//	private static final boolean USE_COLOR_CHOOSER = true;
//
//	private BaseShape mBaseShape;
//	private ShapeDrawType mShapeDrawType;
//
//	public static enum ShapeDrawType {
//		OUTLINE, FILL
//	}
//
//	public static enum BaseShape {
//		RECTANGLE, OVAL
//	};
//
//	public RectangleFillTool(Context context, ToolType toolType) {
//		super(context, toolType, ROTATION_ENABLED, USE_COLOR_CHOOSER);
//		mBaseShape = BaseShape.RECTANGLE;
//		mShapeDrawType = ShapeDrawType.OUTLINE;
//		createAndSetBitmap(null);
//	}
//
//	@Override
//	protected void createAndSetBitmap(DrawingSurface drawingSurface) {
//		Bitmap bitmap = Bitmap.createBitmap((int) mBoxWidth, (int) mBoxHeight,
//				Bitmap.Config.ARGB_8888);
//		Canvas drawCanvas = new Canvas(bitmap);
//		RectF shapeRect = new RectF(SHAPE_OFFSET, SHAPE_OFFSET, mBoxWidth
//				- SHAPE_OFFSET, mBoxHeight - SHAPE_OFFSET);
//		Paint drawPaint = new Paint();
//		drawPaint.setColor(mBitmapPaint.getColor());
//		drawPaint.setAntiAlias(DEFAULT_ANTIALISING_ON);
//		switch (mShapeDrawType) {
//		case FILL:
//			drawPaint.setStyle(Style.FILL);
//			break;
//		case OUTLINE:
//			drawPaint.setStyle(Style.STROKE);
//			drawPaint.setStrokeWidth(mBitmapPaint.getStrokeWidth());
//			drawPaint.setStrokeCap(mBitmapPaint.getStrokeCap());
//			break;
//		default:
//			break;
//		}
//
//		switch (mBaseShape) {
//		case RECTANGLE:
//			drawCanvas.drawRect(shapeRect, drawPaint);
//			break;
//		case OVAL:
//			drawCanvas.drawOval(shapeRect, drawPaint);
//			break;
//		default:
//			break;
//		}
//
//		mDrawingBitmap = bitmap;
//	}
//
//	@Override
//	public int getAttributeButtonResource(ToolButtonIDs buttonNumber) {
//		switch (buttonNumber) {
//		case BUTTON_ID_PARAMETER_TOP_1:
//			return getStrokeWidthResource();
//		case BUTTON_ID_PARAMETER_TOP_2:
//			return getStrokeColorResource();
//		case BUTTON_ID_PARAMETER_BOTTOM_1:
//			return R.drawable.icon_menu_strokes;
//		case BUTTON_ID_PARAMETER_BOTTOM_2:
//			return R.drawable.icon_menu_color_palette;
//		default:
//			return super.getAttributeButtonResource(buttonNumber);
//		}
//	}
//
//	@Override
//	public void attributeButtonClick(ToolButtonIDs buttonNumber) {
//		switch (buttonNumber) {
//		case BUTTON_ID_PARAMETER_BOTTOM_1:
//		case BUTTON_ID_PARAMETER_TOP_1:
//			showBrushPicker();
//			break;
//		case BUTTON_ID_PARAMETER_BOTTOM_2:
//		case BUTTON_ID_PARAMETER_TOP_2:
//			showColorPicker();
//			break;
//		default:
//			break;
//		}
//	}
//
//	@Override
//	public int getAttributeButtonColor(ToolButtonIDs buttonNumber) {
//		switch (buttonNumber) {
//		case BUTTON_ID_PARAMETER_TOP_1:
//			return Color.TRANSPARENT;
//		default:
//			return super.getAttributeButtonColor(buttonNumber);
//		}
//	}
// }
