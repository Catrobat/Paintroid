package at.tugraz.ist.paintroid.helper;

import android.graphics.Color;
import android.graphics.Paint.Cap;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.MainActivity.ToolType;
import at.tugraz.ist.paintroid.dialog.DialogColorPicker;
import at.tugraz.ist.paintroid.dialog.DialogHelp;
import at.tugraz.ist.paintroid.dialog.DialogStrokePicker;
import at.tugraz.ist.paintroid.graphic.DrawingSurface.ColorPickupListener;

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


/**
 * Holds toolbar buttons and changes their functionality and apperance
 * 
 * Status: refactored 20.02.2011
 * @author PaintroidTeam
 * @version 0.6.4b
 */
public class Toolbar implements OnClickListener, OnLongClickListener {
	
	protected MainActivity activity;
	protected TextView toolButton;
	protected TextView attributeButton1;
	protected TextView attributeButton2;
	protected Button undoButton;
	protected ToolType toolType;
	
	/**
	 * Constructor
	 * 
	 * Sets default button behavior and appearance
	 * @param activity current activity
	 */
	public Toolbar(MainActivity activity)
	{
		this.activity = activity;
		
		toolButton = (Button) activity.findViewById(R.id.btn_Tool);
		toolButton.setOnClickListener(this);
		toolButton.setOnLongClickListener(this);
		toolButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.brush64);
		toolButton.setBackgroundResource(R.drawable.attribute_button_selector);
		
		attributeButton1 = (TextView) activity.findViewById(R.id.btn_Parameter1);
	    attributeButton1.setOnClickListener(this);
	    attributeButton1.setOnLongClickListener(this);
	    attributeButton1.setBackgroundColor(activity.getSelectedColor());
		
		attributeButton2 = (TextView) activity.findViewById(R.id.btn_Parameter2);
		attributeButton2.setOnClickListener(this);
		attributeButton2.setOnLongClickListener(this);
		
		undoButton = (Button) activity.findViewById(R.id.btn_Undo);
		undoButton.setOnClickListener(this);
		undoButton.setOnLongClickListener(this);
		undoButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.undo64);
		undoButton.setBackgroundResource(R.drawable.attribute_button_selector);
		
		toolType = ToolType.BRUSH;
	}
	
	/**
	 * Sets the buttons appearance if the stroke width or type changed
	 * 
	 * @param stroke brush width
	 * @param strokeType brush type
	 */
	public void setStrokeAndShape(int stroke, Cap strokeType)
	{
	    if((toolType != ToolType.BRUSH && toolType != ToolType.CURSOR) || strokeType == null)
	    {
	    	return;
	    }
		switch (strokeType) {
	    case SQUARE:
	      switch (stroke) {

	      case 1:
	        attributeButton2.setBackgroundResource(R.drawable.rect_1_32);
	        break;
	      case 5:
	        attributeButton2.setBackgroundResource(R.drawable.rect_2_32);
	        break;
	      case 15:
	        attributeButton2.setBackgroundResource(R.drawable.rect_3_32);
	        break;
	      case 25:
	        attributeButton2.setBackgroundResource(R.drawable.rect_4_32);
	        break;
	      }
	      break;
	    case ROUND:
	      switch (stroke) {

	      case 1:
	        attributeButton2.setBackgroundResource(R.drawable.circle_1_32);
	        break;
	      case 5:
	        attributeButton2.setBackgroundResource(R.drawable.circle_2_32);
	        break;
	      case 15:
	        attributeButton2.setBackgroundResource(R.drawable.circle_3_32);
	        break;
	      case 25:
	        attributeButton2.setBackgroundResource(R.drawable.circle_4_32);
	        break;
	      }
	      break;
	    default:
	      break;
	    }
	}
	
	/**
	 * Handle all button events
	 * 
	 */
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_Tool:
			  activity.callToolMenu();
			  break;
		case R.id.btn_Parameter1:
			switch (toolType) {
			case MAGIC:
			case CURSOR:
			case BRUSH:
			case PIPETTE:
				DialogColorPicker.OnColorChangedListener mColor = new DialogColorPicker.OnColorChangedListener() {
	  				@Override
	  				public void colorChanged(int color) {
	  					if (color == Color.TRANSPARENT) {
	  						Log.d("PAINTROID", "Transparent set");
	  				        attributeButton1.setBackgroundResource(R.drawable.transparent_64);
	  						activity.setColor(color);
	  					} else {
	  					  attributeButton1.setBackgroundColor(color);
	  					  activity.setColor(color);
	  					}
	  				}
	  			};
	  			DialogColorPicker colorpicker = new DialogColorPicker(activity, mColor,
	  					activity.getSelectedColor());
	  			colorpicker.show();
	  			break;
			case ZOOM:
				(activity.getZoomStatus()).resetZoomState();
				break;
			case FLOATINGBOX:
				//Rotate left
			    if(!(activity.getDrawingSurface()).rotateFloatingBox(-90))
			    {
				    Toast toast = Toast.makeText(activity, R.string.warning_floating_box_rotate,  Toast.LENGTH_SHORT);
		            toast.show();
			    }
			    break;
			}
			break;
		case R.id.btn_Parameter2: // starting stroke chooser dialog
			switch (toolType) {
			case BRUSH:
			case CURSOR:
  			DialogStrokePicker.OnStrokeChangedListener mStroke = new DialogStrokePicker.OnStrokeChangedListener() {
  
  				@Override
  				public void strokeChanged(int stroke) {
  					activity.setStroke(stroke);
  				}
  
  				@Override
  				public void strokeShape(Cap type) {
  					activity.setShape(type);
  				}
  			};
  			
  			DialogStrokePicker strokepicker = new DialogStrokePicker(activity,
  					mStroke);
  			strokepicker.show();
  			break;
			case FLOATINGBOX:
			  //Rotate right
			  if(!(activity.getDrawingSurface()).rotateFloatingBox(90))
			  {
				  Toast toast = Toast.makeText(activity, R.string.warning_floating_box_rotate,  Toast.LENGTH_SHORT);
		          toast.show();
			  }
		    }
			break;
		case R.id.btn_Undo:
			(activity.getDrawingSurface()).undoOneStep();
			break;
		default:
			// set default option
			(activity.getDrawingSurface()).setToolType(ToolType.BRUSH);
		}
	}
	
	/**
	 * Changes the button functionality and appearance regarding to
	 * the selected tool
	 * 
	 * @param tool selected tool
	 */
	public void setTool(ToolType tool)
	{
		if(tool != ToolType.UNDO && tool != ToolType.REDO)
		{
			resetAttributeButtons();
		}
		toolType = tool;
		switch(tool)
	    {
	    case BRUSH:
	      toolButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.brush64);
	      attributeButton1.setVisibility(View.VISIBLE);
	      if(activity.getSelectedColor() == Color.TRANSPARENT){ 
		    attributeButton1.setBackgroundResource(R.drawable.transparent_64);		        
		  }else{
		    attributeButton1.setBackgroundColor(activity.getSelectedColor());  
		  }
	      attributeButton2.setVisibility(View.VISIBLE);
	      setStrokeAndShape(activity.getCurrentBrushWidth(), activity.getCurrentBrush());
	      (activity.getDrawingSurface()).setToolType(ToolType.BRUSH);
	      break;
	    case CURSOR:
	      toolButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.cursor64);
	      attributeButton1.setVisibility(View.VISIBLE);
	      if(activity.getSelectedColor() == Color.TRANSPARENT){ 
	        attributeButton1.setBackgroundResource(R.drawable.transparent_64);		        
	      }else{
	    	attributeButton1.setBackgroundColor(activity.getSelectedColor());  
	      }
	      attributeButton2.setVisibility(View.VISIBLE);
	      setStrokeAndShape(activity.getCurrentBrushWidth(), activity.getCurrentBrush());
	      (activity.getDrawingSurface()).activateCursor();
	      break;
	    case SCROLL:
	      toolButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.scroll64);
	      (activity.getDrawingSurface()).setToolType(ToolType.SCROLL);
	      break;
	    case ZOOM:
	      toolButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.zoom64);
	      attributeButton1.setVisibility(View.VISIBLE);
	      attributeButton1.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.zoom48, 0, 0);
	      attributeButton1.setText(R.string.button_reset_zoom);
	      (activity.getDrawingSurface()).setToolType(ToolType.ZOOM);
	      break;
	    case PIPETTE:
	      toolButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.pipette64);
	      attributeButton1.setVisibility(View.VISIBLE);
	      if(activity.getSelectedColor() == Color.TRANSPARENT){ 
		    attributeButton1.setBackgroundResource(R.drawable.transparent_64);
		  }else{
		    attributeButton1.setBackgroundColor(activity.getSelectedColor());  
		  }
	      (activity.getDrawingSurface()).setToolType(ToolType.PIPETTE);
	      ColorPickupListener list = new ColorPickupListener() {
	          
	          @Override
	          public void colorChanged(int color) {
	            // set selected color when new color picked up
	            attributeButton1.setBackgroundColor(color);
	            activity.setColor(color);
	          }
	      };
	      // set the created listener
	      (activity.getDrawingSurface()).setColorPickupListener(list);
	      break;
	    case MAGIC:
	      toolButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.magic64);
	      (activity.getDrawingSurface()).setToolType(ToolType.MAGIC);		      
	      attributeButton1.setVisibility(View.VISIBLE);
	      if(activity.getSelectedColor() == Color.TRANSPARENT){ 
		    attributeButton1.setBackgroundResource(R.drawable.transparent_64);
		  }else{
		    attributeButton1.setBackgroundColor(activity.getSelectedColor());  
		  }
      break;
	    case UNDO:
	    	(activity.getDrawingSurface()).undoOneStep();
	      break;
	    case REDO:
	    	(activity.getDrawingSurface()).redoOneStep();
	      break;
	    case MIDDLEPOINT:
	      toolButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.middlepoint64);
	      (activity.getDrawingSurface()).activateMiddlepoint();
	      break;
	    case FLOATINGBOX:
	      toolButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.middlepoint64);
	      attributeButton1.setVisibility(View.VISIBLE);
	      attributeButton1.setBackgroundResource(0);
		  attributeButton1.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.rotate_left_64_inactive);
		  attributeButton2.setVisibility(View.VISIBLE);	
		  attributeButton2.setBackgroundResource(0);
		  attributeButton2.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.rotate_right_64_inactive);
	      (activity.getDrawingSurface()).activateFloatingBox();
	      break;
	    case IMPORTPNG:
	      toolType = ToolType.FLOATINGBOX;
	      toolButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.middlepoint64);
	      activity.callImportPng();
	      break;
	    }
	}
	
	/**
	 * Resets the attribute buttons to empty hidden text views
	 */
	protected void resetAttributeButtons()
	{
		toolButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		attributeButton1.setVisibility(View.INVISIBLE);
		attributeButton1.setBackgroundColor(Color.TRANSPARENT);
		attributeButton1.setBackgroundResource(R.drawable.attribute_button_selector);
	    attributeButton1.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
	    attributeButton1.setText("");
	    attributeButton2.setVisibility(View.INVISIBLE);
	    attributeButton2.setBackgroundColor(Color.TRANSPARENT);
	    attributeButton2.setBackgroundResource(R.drawable.attribute_button_selector);
	    attributeButton2.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
	    attributeButton2.setText("");
	}
	
	/**
	 * LongClick Listener for Help function
	 * 
	 */
	@Override
	public boolean onLongClick(View v) {
		DialogHelp help = new DialogHelp(activity, v.getId(), toolType);
		help.show();	
		return true;
	}

	/**
	 * Activates the buttons for the use of the
	 * floating box if the floating box is active
	 */
	public void activateFloatingBoxButtons() {
		if(toolType == ToolType.FLOATINGBOX)
		{
			attributeButton1.setVisibility(View.VISIBLE);
			attributeButton1.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.rotate_left_64);
			attributeButton1.setBackgroundResource(R.drawable.attribute_button_selector);
			attributeButton2.setVisibility(View.VISIBLE);
		    attributeButton2.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.rotate_right_64);
		    attributeButton2.setBackgroundResource(R.drawable.attribute_button_selector);
		}
	}

}
