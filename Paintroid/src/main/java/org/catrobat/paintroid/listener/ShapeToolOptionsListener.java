package org.catrobat.paintroid.listener;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.tools.ToolFactory;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.BaseTool;
import org.catrobat.paintroid.tools.implementation.GeometricFillTool;

/**
 * Created by joschi on 14.02.17.
 */

public class ShapeToolOptionsListener{

    private static final String NOT_INITIALIZED_ERROR_MESSAGE = "ShapeToolDialog has not been initialized. Call init() first!";
    private Context mContext;
    private static ShapeToolOptionsListener instance;
    private OnShapeToolOptionsChangedListener mOnShapeToolOptionsChangedListener;
    private ImageButton mSquareButton;
    private ImageButton mCircleButton;
    private ImageButton mHeartButton;
    private ImageButton mStarButton;
    private GeometricFillTool.BaseShape mShape;

    public interface OnShapeToolOptionsChangedListener {
        void setToolType(GeometricFillTool.BaseShape shape);
    }


    public ShapeToolOptionsListener(Context context, View shapeToolOptionsView) {
        mContext = context;
        initializeListeners(shapeToolOptionsView);
    }

    public static ShapeToolOptionsListener getInstance() {
        if (instance == null) {
            throw new IllegalStateException(NOT_INITIALIZED_ERROR_MESSAGE);
        }
        return instance;
    }

    public static void init(Context context, View shapeToolOptionsView) {
        instance = new ShapeToolOptionsListener(context, shapeToolOptionsView);
    }

    private void initializeListeners(View shapeToolOptionsView) {


        mSquareButton = (ImageButton) shapeToolOptionsView.findViewById(R.id.shapes_square_btn);
        mSquareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShape = GeometricFillTool.BaseShape.RECTANGLE;
                mOnShapeToolOptionsChangedListener.setToolType(mShape);

            }
        });

        mCircleButton = (ImageButton) shapeToolOptionsView.findViewById(R.id.shapes_circle_btn);
        mCircleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShape = GeometricFillTool.BaseShape.OVAL;
                mOnShapeToolOptionsChangedListener.setToolType(mShape);
            }
        });

        mHeartButton = (ImageButton) shapeToolOptionsView.findViewById(R.id.shapes_heart_btn);
        mHeartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShape = GeometricFillTool.BaseShape.HEART;
                mOnShapeToolOptionsChangedListener.setToolType(mShape);
            }
        });

        mStarButton = (ImageButton) shapeToolOptionsView.findViewById(R.id.shapes_star_btn);
        mStarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShape = GeometricFillTool.BaseShape.STAR;
                mOnShapeToolOptionsChangedListener.setToolType(mShape);
            }
        });

    }

    public void setOnShapeToolOptionsChangedListener(OnShapeToolOptionsChangedListener listener) {
        mOnShapeToolOptionsChangedListener = listener;
    }

}
