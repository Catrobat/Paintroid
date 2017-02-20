package org.catrobat.paintroid.ui.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.listener.BrushPickerView;
import org.catrobat.paintroid.tools.ToolType;


public class DrawerPreview extends View{

    private final int BORDER = 2;

    public DrawerPreview(Context context) {
        super(context);
    }

    public DrawerPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }



    private void drawDrawerPreview(Canvas canvas) {
        Paint paint = new Paint();
        int strokeWidth =  BrushPickerView.getInstance().getStrokeWidth();
        int currentColor = PaintroidApplication.colorPickerInitialColor;

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(currentColor);
        paint.setAntiAlias(true);

        int centerX = getLeft() + getWidth() / 2;
        int centerY = getTop() + getHeight() / 2;
        int startX = getLeft() + getWidth() / 8;
        int startY = centerY;
        int endX = getRight() - getWidth() / 8;
        int endY = centerY;


        Path path = new Path();
        path.moveTo(startX, startY);
        float x2 = getLeft() + getWidth() / 4;
        float y2 = getTop();
        path.cubicTo(startX, startY, x2, y2, centerX, centerY);
        float x4 = getRight() - getWidth() / 4;
        float y4 = getBottom();
        path.cubicTo(centerX, centerY, x4, y4, endX, endY);

        canvas.drawPath(path, paint);


    }

    private void drawEraserPreview(Canvas canvas) {
        Paint paint = new Paint();
        Paint paintBorder = new Paint();
        int strokeWidth =  BrushPickerView.getInstance().getStrokeWidth();

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);

        paintBorder.setStyle(Paint.Style.STROKE);
        paintBorder.setStrokeWidth(strokeWidth + BORDER);
        paintBorder.setColor(Color.BLACK);
        paintBorder.setAntiAlias(true);

        int centerX = getLeft() + getWidth() / 2;
        int centerY = getTop() + getHeight() / 2;
        int startX = getLeft() + getWidth() / 8;
        int startY = centerY;
        int endX = getRight() - getWidth() / 8;
        int endY = centerY;


        Path path = new Path();
        path.moveTo(startX, startY);
        float x2 = getLeft() + getWidth() / 4;
        float y2 = getTop();
        path.cubicTo(startX, startY, x2, y2, centerX, centerY);
        float x4 = getRight() - getWidth() / 4;
        float y4 = getBottom();
        path.cubicTo(centerX, centerY, x4, y4, endX, endY);

        canvas.drawPath(path, paintBorder);
        canvas.drawPath(path, paint);
    }

    private void drawLinePreview(Canvas canvas) {
        Paint paint = new Paint();
        int strokeWidth =  BrushPickerView.getInstance().getStrokeWidth();
        int currentColor = PaintroidApplication.colorPickerInitialColor;

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(currentColor);
        paint.setAntiAlias(true);

        int startX = getLeft() + getWidth() / 8;
        int startY = getTop() + getHeight() / 2;
        int endX = getRight() - getWidth() / 8;
        int endY = getTop() + getHeight() / 2;


        canvas.drawLine(startX, startY, endX, endY, paint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(PaintroidApplication.currentTool.getToolType() == ToolType.BRUSH)
            drawDrawerPreview(canvas);
        else if(PaintroidApplication.currentTool.getToolType() == ToolType.ERASER)
            drawEraserPreview(canvas);
        else if(PaintroidApplication.currentTool.getToolType() == ToolType.LINE)
            drawLinePreview(canvas);
        invalidate();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int height = (int)(getMeasuredHeight() * 0.2);
        setMeasuredDimension(widthMeasureSpec, height);
    }


}
