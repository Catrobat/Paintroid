package org.catrobat.paintroid.listener;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    private static GeometricFillTool.BaseShape mShape;


    public interface OnShapeToolOptionsChangedListener {
        void setToolType(GeometricFillTool.BaseShape shape);
    }


    public ShapeToolOptionsListener(Context context, View shapeToolOptionsView) {
        mContext = context;
        if(mShape == null)
            mShape = GeometricFillTool.BaseShape.RECTANGLE;
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

    private void initializeListeners(final View shapeToolOptionsView) {
        //setShapeActivated(shapeToolOptionsView, GeometricFillTool.BaseShape.RECTANGLE); //set default value
        setShapeActivated(shapeToolOptionsView, mShape);
        mSquareButton = (ImageButton) shapeToolOptionsView.findViewById(R.id.shapes_square_btn);
        mSquareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShape = GeometricFillTool.BaseShape.RECTANGLE;
                mOnShapeToolOptionsChangedListener.setToolType(mShape);
                setShapeActivated(shapeToolOptionsView, mShape);

            }
        });

        mCircleButton = (ImageButton) shapeToolOptionsView.findViewById(R.id.shapes_circle_btn);
        mCircleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShape = GeometricFillTool.BaseShape.OVAL;
                mOnShapeToolOptionsChangedListener.setToolType(mShape);
                setShapeActivated(shapeToolOptionsView, mShape);
            }
        });

        mHeartButton = (ImageButton) shapeToolOptionsView.findViewById(R.id.shapes_heart_btn);
        mHeartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShape = GeometricFillTool.BaseShape.HEART;
                mOnShapeToolOptionsChangedListener.setToolType(mShape);
                setShapeActivated(shapeToolOptionsView, mShape);
            }
        });

        mStarButton = (ImageButton) shapeToolOptionsView.findViewById(R.id.shapes_star_btn);
        mStarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShape = GeometricFillTool.BaseShape.STAR;
                mOnShapeToolOptionsChangedListener.setToolType(mShape);
                setShapeActivated(shapeToolOptionsView, mShape);
            }
        });

    }

    private void resetShapeActivated(View shapeToolOptionsView) {
        LinearLayout shapesContainer = (LinearLayout)shapeToolOptionsView.findViewById(R.id.shapes_container);
        for (int i = 0; i < shapesContainer.getChildCount(); i++) {
            shapesContainer.getChildAt(i).setBackgroundResource(R.color.transparent);
        }
    }

    private void setShapeActivated(View shapeToolOptionsView, GeometricFillTool.BaseShape shape) {
        resetShapeActivated(shapeToolOptionsView);
        TextView shapeToolDialogTitle = (TextView)shapeToolOptionsView.findViewById(R.id.shape_tool_dialog_title);
        switch (shape) {
            case RECTANGLE:
                shapeToolOptionsView.findViewById(R.id.shapes_square_btn).setBackgroundResource(R.color.bottom_bar_button_activated);
                shapeToolDialogTitle.setText(R.string.shape_tool_dialog_rect_title);
                break;
            case OVAL:
                shapeToolOptionsView.findViewById(R.id.shapes_circle_btn).setBackgroundResource(R.color.bottom_bar_button_activated);
                shapeToolDialogTitle.setText(R.string.shape_tool_dialog_ellipse_title);
                break;
            case STAR:
                shapeToolOptionsView.findViewById(R.id.shapes_star_btn).setBackgroundResource(R.color.bottom_bar_button_activated);
                shapeToolDialogTitle.setText(R.string.shape_tool_dialog_star_title);
                break;
            case HEART:
                shapeToolOptionsView.findViewById(R.id.shapes_heart_btn).setBackgroundResource(R.color.bottom_bar_button_activated);
                shapeToolDialogTitle.setText(R.string.shape_tool_dialog_heart_title);
                break;
            default:
                shapeToolOptionsView.findViewById(R.id.shapes_square_btn).setBackgroundResource(R.color.bottom_bar_button_activated);
                break;
        }
    }

    public void setOnShapeToolOptionsChangedListener(OnShapeToolOptionsChangedListener listener) {
        mOnShapeToolOptionsChangedListener = listener;
    }

}
