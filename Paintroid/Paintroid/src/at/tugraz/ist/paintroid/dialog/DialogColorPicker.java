/*    Catroid: An on-device graphical programming language for Android devices
 *    Copyright (C) 2010  Catroid development team
 *    (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.paintroid.dialog;

/**
 * This dialog provides a color picker for selecting
 * the current color.
 * 
 * TODO: Replace this class with a better color picker
 * 
 * Status: refactored 20.02.2011
 * @author PaintroidTeam
 * @version 6.0.4b
 */
import android.os.Bundle;
import android.app.Dialog;
import android.content.Context;
import android.graphics.*;
import android.view.MotionEvent;
import android.view.View;
import at.tugraz.ist.paintroid.R;

public class DialogColorPicker extends Dialog {
	
	/**
	 * Interface for the color change listener
	 *
	 */
    public interface OnColorChangedListener {
        void colorChanged(int color);
    }

    private OnColorChangedListener mListener;
    private int mInitialColor, mDefaultColor;

    /**
     * Class that handles and creates the color picker
     *
     * Public for Robotium
     */
	public static class ColorPickerView extends View {
		// Paint for the view
		private Paint mPaint;
		// Currently selected hue
		private float mCurrentHue = 0;
		// Coordinates of the selected color
		private int mCurrentX = 0, mCurrentY = 0;
		private int mCurrentColor, mDefaultColor;
		private final int[] mHueBarColors = new int[258];
		private int[] mMainColors = new int[256];
		private OnColorChangedListener mListener;

		/**
		 * Constructor
		 * 
		 * Defines the hue slider bar colors and the field colors
		 * depending on the selected hue
		 * 
		 * @param context View context
		 * @param listener On color change listener
		 * @param color Current color
		 * @param defaultColor Default color
		 */
		ColorPickerView(Context context, OnColorChangedListener listener, int color, int defaultColor) {
			super(context);
			mListener = listener;
			mDefaultColor = defaultColor;

			// Get the current hue from the current color and update the main color field
			float[] hsv = new float[3];
			Color.colorToHSV(color, hsv);
			mCurrentHue = hsv[0];
			updateMainColors();

			mCurrentColor = color;

			// Initialize the colors of the hue slider bar
			int index = 0;
			for (float i=0; i<256; i += 256/42) // Red (#f00) to pink (#f0f)
			{
				mHueBarColors[index] = Color.rgb(255, 0, (int) i);
				index++;
			}
			for (float i=0; i<256; i += 256/42) // Pink (#f0f) to blue (#00f)
			{
				mHueBarColors[index] = Color.rgb(255-(int) i, 0, 255);
				index++;
			}
			for (float i=0; i<256; i += 256/42) // Blue (#00f) to light blue (#0ff)
			{
				mHueBarColors[index] = Color.rgb(0, (int) i, 255);
				index++;
			}
			for (float i=0; i<256; i += 256/42) // Light blue (#0ff) to green (#0f0)
			{
				mHueBarColors[index] = Color.rgb(0, 255, 255-(int) i);
				index++;
			}
			for (float i=0; i<256; i += 256/42) // Green (#0f0) to yellow (#ff0)
			{
				mHueBarColors[index] = Color.rgb((int) i, 255, 0);
				index++;
			}
			for (float i=0; i<256; i += 256/42) // Yellow (#ff0) to red (#f00)
			{
				mHueBarColors[index] = Color.rgb(255, 255-(int) i, 0);
				index++;
			}

			// Initializes the Paint that will draw the View
			mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			mPaint.setTextAlign(Paint.Align.CENTER);
			mPaint.setTextSize(18);
		}

		/**
		 * Get the current selected color from the hue bar
		 * 
		 * @return selected color
		 */
		private int getCurrentMainColor()
		{
			int translatedHue = 255-(int)(mCurrentHue*255/360);
			int index = 0;
			for (float i=0; i<256; i += 256/42)
			{
				if (index == translatedHue)
					return Color.rgb(255, 0, (int) i);
				index++;
			}
			for (float i=0; i<256; i += 256/42)
			{
				if (index == translatedHue)
					return Color.rgb(255-(int) i, 0, 255);
				index++;
			}
			for (float i=0; i<256; i += 256/42)
			{
				if (index == translatedHue)
					return Color.rgb(0, (int) i, 255);
				index++;
			}
			for (float i=0; i<256; i += 256/42)
			{
				if (index == translatedHue)
					return Color.rgb(0, 255, 255-(int) i);
				index++;
			}
			for (float i=0; i<256; i += 256/42)
			{
				if (index == translatedHue)
					return Color.rgb((int) i, 255, 0);
				index++;
			}
			for (float i=0; i<256; i += 256/42)
			{
				if (index == translatedHue)
					return Color.rgb(255, 255-(int) i, 0);
				index++;
			}
			return Color.RED;
		}

		/**
		 * Update the main field colors depending on the current selected hue
		 * 
		 */
		private void updateMainColors()
		{
			int mainColor = getCurrentMainColor();
			int index = 0;
			// Define top colors
			for (int x=0; x<256; x++)
			{
				mMainColors[index] = 
					Color.rgb(
							255-(255-Color.red(mainColor))*x/255,
							255-(255-Color.green(mainColor))*x/255,
							255-(255-Color.blue(mainColor))*x/255);
				index++;
			}
		}

		/**
		 * Draws the actual view
		 * 
		 */
		@Override
		protected void onDraw(Canvas canvas) {
			int translatedHue = 255-(int)(mCurrentHue*255/360);
			// Display all the colors of the hue bar with lines
			for (int x=0; x<256; x++)
			{
				// If this is not the current selected hue, display the actual color
				if (translatedHue != x)
				{
					mPaint.setColor(mHueBarColors[x]);
					mPaint.setStrokeWidth(1);
				}
				else // else display a slightly larger black line
				{
					mPaint.setColor(Color.BLACK);
					mPaint.setStrokeWidth(3);
				}
				canvas.drawLine(x+10, 0, x+10, 40, mPaint);
			}

			// Display the main field colors using LinearGradient
			for (int x=0; x<256; x++)
			{
				int[] colors = new int[2];
				colors[0] = mMainColors[x];
				colors[1] = Color.BLACK;
				Shader shader = new LinearGradient(0, 50, 0, 306, colors, null, Shader.TileMode.REPEAT);
				mPaint.setShader(shader);
				canvas.drawLine(x+10, 50, x+10, 306, mPaint);
			}
			mPaint.setShader(null);

			// Display the circle around the currently selected color in the main field
			if (mCurrentX != 0 && mCurrentY != 0)
			{
				mPaint.setStyle(Paint.Style.STROKE);
				mPaint.setColor(Color.BLACK);
				canvas.drawCircle(mCurrentX, mCurrentY, 10, mPaint);
			}

			// Draw a 'button' with the currently selected color
			mPaint.setStyle(Paint.Style.FILL);
			mPaint.setColor(mCurrentColor);
			canvas.drawRect(10, 316, 138, 356, mPaint);

			// Set the text color according to the brightness of the color
			if (Color.red(mCurrentColor)+Color.green(mCurrentColor)+Color.blue(mCurrentColor) < 384)
				mPaint.setColor(Color.WHITE);
			else
				mPaint.setColor(Color.BLACK);
			canvas.drawText(getResources().getString(R.string.color_picker_choosecolor), 74, 340, mPaint);

			// Draw a 'button' with the default color
			mPaint.setStyle(Paint.Style.FILL);
			mPaint.setColor(mDefaultColor);
			canvas.drawRect(138, 316, 266, 356, mPaint);

			// Set the text color according to the brightness of the color
			if (Color.red(mDefaultColor)+Color.green(mDefaultColor)+Color.blue(mDefaultColor) < 384)
				mPaint.setColor(Color.WHITE);
			else
				mPaint.setColor(Color.BLACK);
			canvas.drawText(getResources().getString(R.string.color_picker_transparency), 202, 340, mPaint);
		}

		/**
		 * Set the window to a specified size
		 * 
		 */
		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			// Warning: changing the dimension will affect the color picker
			setMeasuredDimension(276, 366);
		}

		/**
		 * Determines where the user "clicked" and starts the proper action 
		 * 
		 */
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			float x = event.getX();
			float y = event.getY();

			// If you touch the color bar
			if (x >= 10 && x < 266 && y >= 0 && y < 40)
			{
				// Update the Colors in the main field
				mCurrentHue = (255-(x-10))*360/255;
				updateMainColors();

				// Update the current selected color
				int transX = mCurrentX-10;
				int transY = mCurrentY-60;
				if(transX >= 0 && transX < mMainColors.length)
				{
					// Calculate chosen color regarding to the new hue
					mCurrentColor = Color.rgb(
						(255-(transY))*Color.red(mMainColors[transX])/255,
						(255-(transY))*Color.green(mMainColors[transX])/255,
						(255-(transY))*Color.blue(mMainColors[transX])/255);
				}

				// Force the redraw of the dialog
				invalidate();
			}
			// If you touch the main field
			else if (x >= 10 && x < 266 && y >= 50 && y < 306)
			{
				mCurrentX = (int) x;
				mCurrentY = (int) y;
				int transX = mCurrentX-10;
				int transY = mCurrentY-50;
				if(transX >= 0 && transX < mMainColors.length)
				{
					// Calculate chosen color
					mCurrentColor = Color.rgb(
							(255-(transY))*Color.red(mMainColors[transX])/255,
							(255-(transY))*Color.green(mMainColors[transX])/255,
							(255-(transY))*Color.blue(mMainColors[transX])/255);
					// Force the redraw of the dialog
					invalidate();
				}
			}
			// If the touch event is located in the left button, notify the listener with the current color
			else if (x > 10 && x < 138 && y > 316 && y < 356)
			{
				mListener.colorChanged(mCurrentColor);
			}
			// If the touch event is located in the right button, notify the listener with transparent as color
			else if (x > 138 && x < 266 && y > 316 && y < 356)
			{
				mListener.colorChanged(Color.TRANSPARENT);
			}

			return true;
		}
	}
	//End ColorPickerView

	/**
	 * Constructor
	 * 
	 */
    public DialogColorPicker(Context context, OnColorChangedListener listener, int initialColor) {
        super(context);

        mListener = listener;
        mInitialColor = initialColor;
    }

    /**
     * Sets the view
     * 
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnColorChangedListener l = new OnColorChangedListener() {

			@Override
			public void colorChanged(int color) {
                mListener.colorChanged(color);
                dismiss();
			}
        };

        setContentView(new ColorPickerView(getContext(), l, mInitialColor, mDefaultColor));
        setTitle(R.string.color_picker_title);
    }
}